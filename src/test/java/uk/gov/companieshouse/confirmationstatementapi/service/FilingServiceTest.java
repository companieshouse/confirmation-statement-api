package uk.gov.companieshouse.confirmationstatementapi.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.FILING_KIND_LPCS;
import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.FILING_KIND_SLPCS;
import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.LIMITED_PARTNERSHIP_LP_TYPE;
import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.LIMITED_PARTNERSHIP_PFLP_SUBTYPE;
import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.LIMITED_PARTNERSHIP_SLP_SUBTYPE;
import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.LIMITED_PARTNERSHIP_SPFLP_SUBTYPE;
import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.LIMITED_PARTNERSHIP_TYPE;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.handler.payment.PaymentResourceHandler;
import uk.gov.companieshouse.api.handler.payment.request.PaymentGet;
import uk.gov.companieshouse.api.handler.transaction.TransactionsResourceHandler;
import uk.gov.companieshouse.api.handler.transaction.request.TransactionsPaymentGet;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.filinggenerator.FilingApi;
import uk.gov.companieshouse.api.model.payment.PaymentApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.transaction.TransactionLinks;
import uk.gov.companieshouse.api.model.transaction.TransactionPayment;
import uk.gov.companieshouse.confirmationstatementapi.client.ApiClientService;
import uk.gov.companieshouse.confirmationstatementapi.exception.CompanyNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.exception.SubmissionNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.model.SectionStatus;
import uk.gov.companieshouse.confirmationstatementapi.model.json.ConfirmationStatementSubmissionDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.ConfirmationStatementSubmissionJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.TradingStatusDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.registeredemailaddress.RegisteredEmailAddressDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.siccode.SicCodeDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.siccode.SicCodeJson;

@ExtendWith(MockitoExtension.class)
class FilingServiceTest {

    private static final String CONFIRMATION_STATEMENT_ID = "abc123";
    private static final String COMPANY_NUMBER = "12345678";

    @InjectMocks
    private FilingService filingService;

    @Mock
    private ConfirmationStatementService csService;

    @Mock
    private ApiClientService apiClientService;

    @Mock
    private ApiClient apiClient;

    @Mock
    private TransactionsResourceHandler transactionsResourceHandler;

    @Mock
    private TransactionsPaymentGet transactionsPaymentGet;

    @Mock
    private PaymentResourceHandler paymentResourceHandler;

    @Mock
    private PaymentGet paymentGet;

    @Mock
    private CompanyProfileService companyProfileService;

    @Mock
    private SicCodeComparisonService sicCodeComparisonService;

    private Transaction transaction;

    @BeforeEach
    void init() {
        transaction = new Transaction();
        var transactionLinks = new TransactionLinks();
        transactionLinks.setPayment("/12345678/payment");
        transaction.setLinks(transactionLinks);
        transaction.setCompanyNumber(COMPANY_NUMBER);


        ReflectionTestUtils.setField(filingService, "costAmount", "34.00");
        ReflectionTestUtils.setField(filingService, "filingDescription", "Confirmation statement made on {made up date} with no updates");

    }

    private void getTransactionPaymentLinkMock() throws ApiErrorResponseException, URIValidationException {
        var transactionPayment = new TransactionPayment();
        transactionPayment.setPaymentReference("reference");

        var transactionApiResponse = new ApiResponse<>(200, null, transactionPayment);

        when(apiClientService.getApiKeyAuthenticatedClient()).thenReturn(apiClient);
        when(apiClient.transactions()).thenReturn(transactionsResourceHandler);
        when(transactionsResourceHandler.getPayment(anyString())).thenReturn(transactionsPaymentGet);
        when(transactionsPaymentGet.execute()).thenReturn(transactionApiResponse);
    }

    private void paymentGetMocks() throws ApiErrorResponseException, URIValidationException {
        var paymentApi = new PaymentApi();
        paymentApi.setPaymentMethod("payment-method");

        var paymentApiResponse = new ApiResponse<>(200, null, paymentApi);

        when(apiClient.payment()).thenReturn(paymentResourceHandler);
        when(paymentResourceHandler.get(anyString())).thenReturn(paymentGet);
        when(paymentGet.execute()).thenReturn(paymentApiResponse);
    }

    @Test
    void testWhenPayableSubmissionIsReturnedSuccessfully() throws SubmissionNotFoundException, ServiceException, URIValidationException, ApiErrorResponseException, CompanyNotFoundException {
        paymentGetMocks();
        getTransactionPaymentLinkMock();
        ConfirmationStatementSubmissionJson confirmationStatementSubmissionJson =  buildSubmissionJson(null, null);
        Optional<ConfirmationStatementSubmissionJson> opt = Optional.of(confirmationStatementSubmissionJson);
        ReflectionTestUtils.setField(filingService, "filingDescription", "Confirmation statement made on {made up date} with no updates");

        when(csService.getConfirmationStatement(CONFIRMATION_STATEMENT_ID)).thenReturn(opt);
              FilingApi filing = filingService.generateConfirmationFiling(CONFIRMATION_STATEMENT_ID, transaction);

        assertEquals("Confirmation statement made on 1 June 2021 with no updates", filing.getDescription());
        assertEquals(confirmationStatementSubmissionJson.getData().getMadeUpToDate(), filing.getData().get("confirmation_statement_date"));
        assertEquals(Boolean.TRUE, confirmationStatementSubmissionJson.getData().getAcceptLawfulPurposeStatement());
        assertFalse((Boolean) filing.getData().get("trading_on_market"));
        assertFalse((Boolean) filing.getData().get("dtr5_ind"));
        assertEquals("payment-method", filing.getData().get("payment_method"));
        assertEquals("reference", filing.getData().get("payment_reference"));
        assertFalse(filing.getData().containsKey("registered_email_address"));
    }

    @Test
    void testWhenPayableSubmissionWithREAInitialFilingIsReturnedSuccessfully() throws SubmissionNotFoundException, ServiceException, URIValidationException, ApiErrorResponseException, CompanyNotFoundException {
        // GIVEN

        paymentGetMocks();
        getTransactionPaymentLinkMock();
        String initialRea = "initial.rea@acme.com";
        ConfirmationStatementSubmissionJson confirmationStatementSubmissionJson =  buildSubmissionJson(initialRea, null);
        Optional<ConfirmationStatementSubmissionJson> opt = Optional.of(confirmationStatementSubmissionJson);
        ReflectionTestUtils.setField(filingService, "filingDescription", "Confirmation statement made on {made up date} with no updates");

        given(csService.getConfirmationStatement(CONFIRMATION_STATEMENT_ID)).willReturn(opt);

        // WHEN

        FilingApi filing = filingService.generateConfirmationFiling(CONFIRMATION_STATEMENT_ID, transaction);

        // THEN

        Map<String, Object> data = filing.getData();

        assertEquals("Confirmation statement made on 1 June 2021 with no updates", filing.getDescription());
        assertEquals(confirmationStatementSubmissionJson.getData().getMadeUpToDate(), data.get("confirmation_statement_date"));
        assertEquals(Boolean.TRUE, confirmationStatementSubmissionJson.getData().getAcceptLawfulPurposeStatement());
        assertFalse((Boolean) data.get("trading_on_market"));
        assertFalse((Boolean) data.get("dtr5_ind"));
        assertEquals("payment-method", data.get("payment_method"));
        assertEquals("reference", data.get("payment_reference"));
        assertEquals("reference", data.get("payment_reference"));
        assertEquals(initialRea, data.get("registered_email_address"));
    }

    @Test
    void testWhenPayableSubmissionWithREAConfirmedIsReturnedSuccessfully() throws SubmissionNotFoundException, ServiceException, URIValidationException, ApiErrorResponseException, CompanyNotFoundException {
        // GIVEN

        paymentGetMocks();
        getTransactionPaymentLinkMock();
        String confirmedRea = "confirmed.rea@acme.com";
        ConfirmationStatementSubmissionJson confirmationStatementSubmissionJson =  buildSubmissionJson(null, confirmedRea);
        Optional<ConfirmationStatementSubmissionJson> opt = Optional.of(confirmationStatementSubmissionJson);
        ReflectionTestUtils.setField(filingService, "filingDescription", "Confirmation statement made on {made up date} with no updates");

        given(csService.getConfirmationStatement(CONFIRMATION_STATEMENT_ID)).willReturn(opt);

        // WHEN

        FilingApi filing = filingService.generateConfirmationFiling(CONFIRMATION_STATEMENT_ID, transaction);

        // THEN

        Map<String, Object> data = filing.getData();

        assertEquals("Confirmation statement made on 1 June 2021 with no updates", filing.getDescription());
        assertEquals(confirmationStatementSubmissionJson.getData().getMadeUpToDate(), data.get("confirmation_statement_date"));
        assertEquals(Boolean.TRUE, confirmationStatementSubmissionJson.getData().getAcceptLawfulPurposeStatement());
        assertFalse((Boolean) data.get("trading_on_market"));
        assertFalse((Boolean) data.get("dtr5_ind"));
        assertEquals("payment-method", data.get("payment_method"));
        assertEquals("reference", data.get("payment_reference"));
        assertEquals("reference", data.get("payment_reference"));
        assertNull(data.get("registered_email_address"));
    }

    @Test
    void testWhenNonPayableSubmissionIsReturnedSuccessfully() throws SubmissionNotFoundException, ServiceException, CompanyNotFoundException {
        transaction.getLinks().setPayment(null);
        ConfirmationStatementSubmissionJson confirmationStatementSubmissionJson =  buildSubmissionJson(null, null);
        Optional<ConfirmationStatementSubmissionJson> opt = Optional.of(confirmationStatementSubmissionJson);
        ReflectionTestUtils.setField(filingService, "filingDescription", "Confirmation statement made on {made up date} with no updates");
        when(csService.getConfirmationStatement(CONFIRMATION_STATEMENT_ID)).thenReturn(opt);
        FilingApi filing = filingService.generateConfirmationFiling(CONFIRMATION_STATEMENT_ID, transaction);
        assertEquals("Confirmation statement made on 1 June 2021 with no updates", filing.getDescription());
        assertEquals(confirmationStatementSubmissionJson.getData().getMadeUpToDate(), filing.getData().get("confirmation_statement_date"));
        assertEquals(Boolean.TRUE, confirmationStatementSubmissionJson.getData().getAcceptLawfulPurposeStatement());
        assertFalse((Boolean) filing.getData().get("trading_on_market"));
        assertFalse((Boolean) filing.getData().get("dtr5_ind"));
        assertNull(filing.getData().get("payment_method"));
        assertNull(filing.getData().get("payment_reference"));
        assertFalse(filing.getData().containsKey("registered_email_address"));
    }

    @Test
    void testWhenGetPaymentThrowsException() throws ApiErrorResponseException, URIValidationException {
        paymentGetMocks();
        getTransactionPaymentLinkMock();
        when(paymentGet.execute()).thenThrow(ApiErrorResponseException.fromIOException(new IOException()));
        assertThrows(ServiceException.class, () -> filingService.generateConfirmationFiling(CONFIRMATION_STATEMENT_ID, transaction));
    }

    @Test
    void testWhenGetPaymentReferenceFromTransaction() throws ApiErrorResponseException, URIValidationException {
        getTransactionPaymentLinkMock();
        when(transactionsPaymentGet.execute()).thenThrow(ApiErrorResponseException.fromIOException(new IOException()));
        assertThrows(ServiceException.class, () -> filingService.generateConfirmationFiling(CONFIRMATION_STATEMENT_ID, transaction));
    }

    @Test
    void testWhenEmptySubmissionIsReturned() throws URIValidationException, ApiErrorResponseException {
        paymentGetMocks();
        getTransactionPaymentLinkMock();
        when(csService.getConfirmationStatement(CONFIRMATION_STATEMENT_ID)).thenReturn(Optional.empty());
        var submissionNotFoundException = assertThrows(SubmissionNotFoundException.class, () -> filingService.generateConfirmationFiling(CONFIRMATION_STATEMENT_ID, transaction));
        assertTrue(submissionNotFoundException.getMessage()
                .contains("Empty submission returned when generating filing for " + CONFIRMATION_STATEMENT_ID));
    }

    @Test
    void testWhenEmptySubmissionDataIsReturned() throws URIValidationException, ApiErrorResponseException {
        paymentGetMocks();
        getTransactionPaymentLinkMock();
        ConfirmationStatementSubmissionJson confirmationStatementSubmissionJson =  buildSubmissionJson(null, null);
        confirmationStatementSubmissionJson.setData(null);
        when(csService.getConfirmationStatement(CONFIRMATION_STATEMENT_ID)).thenReturn(Optional.of(confirmationStatementSubmissionJson));
        var submissionNotFoundException = assertThrows(SubmissionNotFoundException.class, () -> filingService.generateConfirmationFiling(CONFIRMATION_STATEMENT_ID, transaction));
        assertTrue(submissionNotFoundException.getMessage().contains("Submission contains no data " + CONFIRMATION_STATEMENT_ID));
    }

    @Test
    void testWhenPayableSubmissionIsReturnedSuccessfullyForNonLpJourney() throws SubmissionNotFoundException, ServiceException, URIValidationException, ApiErrorResponseException, CompanyNotFoundException {
        paymentGetMocks();
        getTransactionPaymentLinkMock();
        ConfirmationStatementSubmissionJson confirmationStatementSubmissionJson =  buildSubmissionJsonForLpJourney();
        Optional<ConfirmationStatementSubmissionJson> opt = Optional.of(confirmationStatementSubmissionJson);
        ReflectionTestUtils.setField(filingService, "filingDescription", "Confirmation statement made on {made up date} with no updates");
        when(csService.getConfirmationStatement(CONFIRMATION_STATEMENT_ID)).thenReturn(opt);

        FilingApi filing = filingService.generateConfirmationFiling(CONFIRMATION_STATEMENT_ID, transaction);

        assertEquals("Confirmation statement made on 1 October 2024 with no updates", filing.getDescription());
        assertEquals(confirmationStatementSubmissionJson.getData().getMadeUpToDate(), filing.getData().get("confirmation_statement_date"));
        assertTrue((Boolean) filing.getData().get("accept_lawful_purpose_statement"));
        assertEquals("payment-method", filing.getData().get("payment_method"));
        assertEquals("reference", filing.getData().get("payment_reference"));
    }

    @Test
    void testFilingDataForLpJourney() throws SubmissionNotFoundException, ServiceException, URIValidationException, ApiErrorResponseException, CompanyNotFoundException {
        paymentGetMocks();
        getTransactionPaymentLinkMock();
        ConfirmationStatementSubmissionJson confirmationStatementSubmissionJson =  buildSubmissionJsonForLpJourney();
        Optional<ConfirmationStatementSubmissionJson> opt = Optional.of(confirmationStatementSubmissionJson);
        ReflectionTestUtils.setField(filingService, "filingDescription", "Confirmation statement made on {made up date} with no updates");
        CompanyProfileApi companyProfileApi = buildLpCompanyProfile();
        confirmationStatementSubmissionJson.getData().setNewConfirmationDate("2025-10-13");
        confirmationStatementSubmissionJson.getData().setSicCodeData(buildSicCodeDataJson());

        when(csService.getConfirmationStatement(CONFIRMATION_STATEMENT_ID)).thenReturn(opt);
        when(companyProfileService.getCompanyProfile(COMPANY_NUMBER)).thenReturn(companyProfileApi);

        FilingApi filing = filingService.generateConfirmationFiling(CONFIRMATION_STATEMENT_ID, transaction);

        assertEquals("Confirmation statement made on 13 October 2025 with no updates", filing.getDescription());
        assertNotEquals(confirmationStatementSubmissionJson.getData().getMadeUpToDate(), filing.getData().get("confirmation_statement_date"));
        assertTrue((Boolean) filing.getData().get("accept_lawful_purpose_statement"));
        assertEquals("payment-method", filing.getData().get("payment_method"));
        assertEquals("reference", filing.getData().get("payment_reference"));
        assertEquals("limited-partnership-confirmation-statement", filing.getKind());
    }

    @ParameterizedTest
    @CsvSource({
        "'70229,71122,74909,01120', '70229,71122,74909,01120', false, ''",
        "'70229,71122,74909,01120', '01120,74909,71122,70229', false, ''",
        "'70229,71122,74909,01120', '70229,01120', true, '70229,01120'",
        "'70229,71122,74909,01120', '74909', true, '74909'",
        "'', '70229,71122', true, '70229,71122'",
        "'01120,70229', '70229,74909', true, '70229,74909'",
        "'71122', '71122,70229,74909,01120', true, '71122,70229,74909,01120'"
    })
    void shouldSetCorrectSicCodeDataInFilingData(String companyProfileSicCodes, String submissionSicCodes, boolean expectedHasDifferences, String expectedFilingSicCodes) throws SubmissionNotFoundException, ServiceException,
            URIValidationException, ApiErrorResponseException, CompanyNotFoundException {
        String[] companyProfileSicCodeList = companyProfileSicCodes.isBlank() ? null : companyProfileSicCodes.split(",");
        List<String> submissionSicCodeList = submissionSicCodes.isBlank() ? null : List.of((submissionSicCodes.split(",")));
        SicCodeDataJson sicCodeDataJson = buildSicCodeDataJson(submissionSicCodeList);
        List<String> expectedFilingSicCodeList = expectedFilingSicCodes.isBlank() ? null : List.of((expectedFilingSicCodes.split(",")));

        paymentGetMocks();
        getTransactionPaymentLinkMock();
        ConfirmationStatementSubmissionJson confirmationStatementSubmissionJson = buildSubmissionJsonForLpJourney();
        Optional<ConfirmationStatementSubmissionJson> opt = Optional.of(confirmationStatementSubmissionJson);
        confirmationStatementSubmissionJson.getData().setSicCodeData(sicCodeDataJson);
        CompanyProfileApi companyProfile = buildLpCompanyProfile();
        companyProfile.setSicCodes(companyProfileSicCodeList);

        when(csService.getConfirmationStatement(CONFIRMATION_STATEMENT_ID)).thenReturn(opt);
        when(companyProfileService.getCompanyProfile(COMPANY_NUMBER)).thenReturn(companyProfile);
        when(sicCodeComparisonService.hasDifferences(sicCodeDataJson.getSicCode(), companyProfileSicCodeList)).thenReturn(expectedHasDifferences);

        FilingApi filing = filingService.generateConfirmationFiling(CONFIRMATION_STATEMENT_ID, transaction);
        assertEquals(expectedFilingSicCodeList, filing.getData().get("sic_codes"));
    }

    @Test 
    void testDetermineFilingTypeReturnsNullWhenCompanyProfileIsNull() { 
        assertNull(filingService.determineFilingType(null)); 
    }

    @Test 
    void testDetermineFilingTypeReturnsNullWhenSubtypeIsNull() {
        CompanyProfileApi companyProfileApi = new CompanyProfileApi();
        companyProfileApi.setCompanyNumber(COMPANY_NUMBER);
        companyProfileApi.setType(LIMITED_PARTNERSHIP_TYPE);
        companyProfileApi.setSubtype(null);
        assertNull(filingService.determineFilingType(null)); 
    }

    @Test 
    void testDetermineFilingTypeReturnsLpcsForLpSubtype() {
        CompanyProfileApi companyProfileApi = new CompanyProfileApi();
        companyProfileApi.setCompanyNumber(COMPANY_NUMBER);
        companyProfileApi.setType(LIMITED_PARTNERSHIP_TYPE);
        companyProfileApi.setSubtype(LIMITED_PARTNERSHIP_LP_TYPE);
        assertEquals(FILING_KIND_LPCS, filingService.determineFilingType(companyProfileApi));
    }

    @Test 
    void testDetermineFilingTypeReturnsLpcsForPflpSubtype() {
        CompanyProfileApi companyProfileApi = new CompanyProfileApi();
        companyProfileApi.setCompanyNumber(COMPANY_NUMBER);
        companyProfileApi.setType(LIMITED_PARTNERSHIP_TYPE);
        companyProfileApi.setSubtype(LIMITED_PARTNERSHIP_PFLP_SUBTYPE);
        assertEquals(FILING_KIND_LPCS, filingService.determineFilingType(companyProfileApi));
    }

    @Test 
    void testDetermineFilingTypeReturnsSlpcsForSlpSubtype() {
        CompanyProfileApi companyProfileApi = new CompanyProfileApi();
        companyProfileApi.setCompanyNumber(COMPANY_NUMBER);
        companyProfileApi.setType(LIMITED_PARTNERSHIP_TYPE);
        companyProfileApi.setSubtype(LIMITED_PARTNERSHIP_SLP_SUBTYPE);
        assertEquals(FILING_KIND_SLPCS, filingService.determineFilingType(companyProfileApi));
    }

    @Test 
    void testDetermineFilingTypeReturnsSlpcsForSpflpSubtype() {
        CompanyProfileApi companyProfileApi = new CompanyProfileApi();
        companyProfileApi.setCompanyNumber(COMPANY_NUMBER);
        companyProfileApi.setType(LIMITED_PARTNERSHIP_TYPE);
        companyProfileApi.setSubtype(LIMITED_PARTNERSHIP_SPFLP_SUBTYPE);
        assertEquals(FILING_KIND_SLPCS, filingService.determineFilingType(companyProfileApi));
    }

    @Test 
    void testDetermineFilingTypeReturnsNullForUnknownSubtype() {
        CompanyProfileApi companyProfileApi = new CompanyProfileApi();
        companyProfileApi.setCompanyNumber(COMPANY_NUMBER);
        companyProfileApi.setType(LIMITED_PARTNERSHIP_TYPE);
        companyProfileApi.setSubtype("UNKNOWN");
        assertNull(filingService.determineFilingType(companyProfileApi));
    }

    @Test
    void testCostIsSetWhenPayable() throws Exception {
        paymentGetMocks();
        getTransactionPaymentLinkMock();

        ReflectionTestUtils.setField(filingService, "costAmount", "34.00");

        ConfirmationStatementSubmissionJson submission = buildSubmissionJson(null, null);
        Optional<ConfirmationStatementSubmissionJson> opt = Optional.of(submission);

        when(csService.getConfirmationStatement(CONFIRMATION_STATEMENT_ID)).thenReturn(opt);

        FilingApi filing = filingService.generateConfirmationFiling(CONFIRMATION_STATEMENT_ID, transaction);

        assertEquals("34.00", filing.getCost(), "Expected costAmount when payment link exists");
    }

    @Test
    void testCostIsZeroWhenNonPayable() throws Exception {
        transaction.getLinks().setPayment(null);

        ReflectionTestUtils.setField(filingService, "costAmount", "34.00");

        ConfirmationStatementSubmissionJson submission = buildSubmissionJson(null, null);
        Optional<ConfirmationStatementSubmissionJson> opt = Optional.of(submission);

        when(csService.getConfirmationStatement(CONFIRMATION_STATEMENT_ID)).thenReturn(opt);

        FilingApi filing = filingService.generateConfirmationFiling(CONFIRMATION_STATEMENT_ID, transaction);

        assertEquals("0", filing.getCost(), "Expected zero cost when no payment is taken");
    }

    @Test
    void testCostIsZeroForLpJourneyWhenNonPayable() throws Exception {
        transaction.getLinks().setPayment(null);

        ReflectionTestUtils.setField(filingService, "costAmount", "34.00");

        ConfirmationStatementSubmissionJson submission = buildSubmissionJsonForLpJourney();
        Optional<ConfirmationStatementSubmissionJson> opt = Optional.of(submission);

        CompanyProfileApi companyProfile = new CompanyProfileApi();
        companyProfile.setCompanyNumber(COMPANY_NUMBER);
        companyProfile.setType(LIMITED_PARTNERSHIP_TYPE);
        companyProfile.setSubtype(LIMITED_PARTNERSHIP_LP_TYPE);

        when(csService.getConfirmationStatement(CONFIRMATION_STATEMENT_ID)).thenReturn(opt);
        when(companyProfileService.getCompanyProfile(COMPANY_NUMBER)).thenReturn(companyProfile);

        FilingApi filing = filingService.generateConfirmationFiling(CONFIRMATION_STATEMENT_ID, transaction);

        assertEquals("0", filing.getCost(), "LP journey should also return zero cost when no payment is taken");
        assertEquals("limited-partnership-confirmation-statement", filing.getKind());
    }

    @Test
    void testCostIsSetForLpJourneyWhenPayable() throws Exception {

        paymentGetMocks();
        getTransactionPaymentLinkMock();

        ReflectionTestUtils.setField(filingService, "costAmount", "34.00");

        ConfirmationStatementSubmissionJson submission = buildSubmissionJsonForLpJourney();
        Optional<ConfirmationStatementSubmissionJson> opt = Optional.of(submission);

        CompanyProfileApi companyProfile = new CompanyProfileApi();
        companyProfile.setCompanyNumber(COMPANY_NUMBER);
        companyProfile.setType(LIMITED_PARTNERSHIP_TYPE);
        companyProfile.setSubtype(LIMITED_PARTNERSHIP_LP_TYPE);

        when(csService.getConfirmationStatement(CONFIRMATION_STATEMENT_ID)).thenReturn(opt);
        when(companyProfileService.getCompanyProfile(COMPANY_NUMBER)).thenReturn(companyProfile);

        FilingApi filing = filingService.generateConfirmationFiling(CONFIRMATION_STATEMENT_ID, transaction);

        assertEquals("34.00", filing.getCost());
    }

    private static CompanyProfileApi buildLpCompanyProfile() {
        CompanyProfileApi companyProfileApi = new CompanyProfileApi();
        companyProfileApi.setCompanyNumber(COMPANY_NUMBER);
        companyProfileApi.setType(LIMITED_PARTNERSHIP_TYPE);
        companyProfileApi.setSubtype(LIMITED_PARTNERSHIP_TYPE);

        return companyProfileApi;
    }

    private static ConfirmationStatementSubmissionJson buildSubmissionJsonForLpJourney() {
        ConfirmationStatementSubmissionJson confirmationStatementSubmissionJson = new ConfirmationStatementSubmissionJson();
        ConfirmationStatementSubmissionDataJson confirmationStatementSubmissionDataJson = new ConfirmationStatementSubmissionDataJson();

        confirmationStatementSubmissionDataJson.setMadeUpToDate(LocalDate.of(2024, 10, 1));
        confirmationStatementSubmissionDataJson.setAcceptLawfulPurposeStatement(Boolean.TRUE);
        confirmationStatementSubmissionDataJson.setNewConfirmationDate("2025-10-01");

        confirmationStatementSubmissionJson.setData(confirmationStatementSubmissionDataJson);

        return confirmationStatementSubmissionJson;
    }

    private static ConfirmationStatementSubmissionJson buildSubmissionJson(String initialRea, String confirmedRea) {
        ConfirmationStatementSubmissionJson confirmationStatementSubmissionJson = new ConfirmationStatementSubmissionJson();

        ConfirmationStatementSubmissionDataJson confirmationStatementSubmissionDataJson = new ConfirmationStatementSubmissionDataJson();

        confirmationStatementSubmissionDataJson.setMadeUpToDate(LocalDate.of(2021, 6, 1));

        TradingStatusDataJson tradingStatus = new TradingStatusDataJson();
        tradingStatus.setTradingStatusAnswer(true);
        confirmationStatementSubmissionDataJson.setTradingStatusData(tradingStatus);

        RegisteredEmailAddressDataJson registeredEmailAddressDataJson = new RegisteredEmailAddressDataJson();
        if (!StringUtils.isBlank(initialRea)) {
            registeredEmailAddressDataJson.setSectionStatus(SectionStatus.INITIAL_FILING);
            registeredEmailAddressDataJson.setRegisteredEmailAddress(initialRea);
            confirmationStatementSubmissionDataJson.setRegisteredEmailAddressData(registeredEmailAddressDataJson);
        } else if (!StringUtils.isBlank(confirmedRea)) {
            registeredEmailAddressDataJson.setSectionStatus(SectionStatus.CONFIRMED);
            confirmationStatementSubmissionDataJson.setRegisteredEmailAddressData(registeredEmailAddressDataJson);
        }

        confirmationStatementSubmissionDataJson.setAcceptLawfulPurposeStatement(Boolean.TRUE);

        confirmationStatementSubmissionJson.setData(confirmationStatementSubmissionDataJson);

        return confirmationStatementSubmissionJson;
    }

    private static SicCodeDataJson buildSicCodeDataJson(List<String> sicCodeList) {
        List<SicCodeJson> sicCodeJsonList = new ArrayList<>();
        sicCodeList
                .stream()
                .forEach(sicCode -> {
                    SicCodeJson sicCodeJson = new SicCodeJson();
                    sicCodeJson.setCode(sicCode);
                    sicCodeJsonList.add(sicCodeJson);
                });

        SicCodeDataJson sicCodeDataJson = new SicCodeDataJson();
        sicCodeDataJson.setSicCode(sicCodeJsonList);

        return sicCodeDataJson;
    }

    private static SicCodeDataJson buildSicCodeDataJson() {
        List<String> sicCodeList = List.of("70229", "71122", "74909", "01120");
        return buildSicCodeDataJson(sicCodeList);
    }


}
