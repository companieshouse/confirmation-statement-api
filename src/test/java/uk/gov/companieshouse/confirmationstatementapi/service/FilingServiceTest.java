package uk.gov.companieshouse.confirmationstatementapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import uk.gov.companieshouse.api.model.filinggenerator.FilingApi;
import uk.gov.companieshouse.api.model.payment.PaymentApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.transaction.TransactionLinks;
import uk.gov.companieshouse.api.model.transaction.TransactionPayment;
import uk.gov.companieshouse.confirmationstatementapi.client.ApiClientService;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.exception.SubmissionNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.model.SectionStatus;
import uk.gov.companieshouse.confirmationstatementapi.model.json.ConfirmationStatementSubmissionDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.ConfirmationStatementSubmissionJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.TradingStatusDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.registeredemailaddress.RegisteredEmailAddressDataJson;

@ExtendWith(MockitoExtension.class)
class FilingServiceTest {

    private static final String CONFIRMATION_STATEMENT_ID = "abc123";

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

    private Transaction transaction;

    @BeforeEach
    void init() {
        transaction = new Transaction();
        var transactionLinks = new TransactionLinks();
        transactionLinks.setPayment("/12345678/payment");
        transaction.setLinks(transactionLinks);
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
    void testWhenPayableSubmissionIsReturnedSuccessfully() throws SubmissionNotFoundException, ServiceException, URIValidationException, ApiErrorResponseException {
        paymentGetMocks();
        getTransactionPaymentLinkMock();
        ConfirmationStatementSubmissionJson confirmationStatementSubmissionJson =  buildSubmissionJson(null);
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
    void testWhenPayableSubmissionWithREAInitialFilingIsReturnedSuccessfully() throws SubmissionNotFoundException, ServiceException, URIValidationException, ApiErrorResponseException {
        // GIVEN

        paymentGetMocks();
        getTransactionPaymentLinkMock();
        String initialRea = "initial.rea@acme.com";
        ConfirmationStatementSubmissionJson confirmationStatementSubmissionJson =  buildSubmissionJson(initialRea);
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
    void testWhenNonPayableSubmissionIsReturnedSuccessfully() throws SubmissionNotFoundException, ServiceException, URIValidationException, ApiErrorResponseException {
        transaction.getLinks().setPayment(null);
        ConfirmationStatementSubmissionJson confirmationStatementSubmissionJson =  buildSubmissionJson(null);
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
        ConfirmationStatementSubmissionJson confirmationStatementSubmissionJson =  buildSubmissionJson(null);
        confirmationStatementSubmissionJson.setData(null);
        when(csService.getConfirmationStatement(CONFIRMATION_STATEMENT_ID)).thenReturn(Optional.of(confirmationStatementSubmissionJson));
        var submissionNotFoundException = assertThrows(SubmissionNotFoundException.class, () -> filingService.generateConfirmationFiling(CONFIRMATION_STATEMENT_ID, transaction));
        assertTrue(submissionNotFoundException.getMessage().contains("Submission contains no data " + CONFIRMATION_STATEMENT_ID));
    }

    private static ConfirmationStatementSubmissionJson buildSubmissionJson(String initialRea) {
        ConfirmationStatementSubmissionJson confirmationStatementSubmissionJson = new ConfirmationStatementSubmissionJson();

        ConfirmationStatementSubmissionDataJson confirmationStatementSubmissionDataJson = new ConfirmationStatementSubmissionDataJson();

        confirmationStatementSubmissionDataJson.setMadeUpToDate(LocalDate.of(2021, 6, 1));

        TradingStatusDataJson tradingStatus = new TradingStatusDataJson();
        tradingStatus.setTradingStatusAnswer(true);
        confirmationStatementSubmissionDataJson.setTradingStatusData(tradingStatus);

        RegisteredEmailAddressDataJson registeredEmailAddressDataJson = new RegisteredEmailAddressDataJson();
        if (initialRea != null) {
            registeredEmailAddressDataJson.setSectionStatus(SectionStatus.INITIAL_FILING);
            registeredEmailAddressDataJson.setRegisteredEmailAddress(initialRea);
        } else {
            registeredEmailAddressDataJson.setSectionStatus(SectionStatus.CONFIRMED);
            registeredEmailAddressDataJson.setRegisteredEmailAddress("rea@acme.com");
        }
        confirmationStatementSubmissionDataJson.setRegisteredEmailAddressData(registeredEmailAddressDataJson);

        confirmationStatementSubmissionDataJson.setAcceptLawfulPurposeStatement(Boolean.TRUE);

        confirmationStatementSubmissionJson.setData(confirmationStatementSubmissionDataJson);

        return confirmationStatementSubmissionJson;
    }
}
