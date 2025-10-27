package uk.gov.companieshouse.confirmationstatementapi.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.LIMITED_PARTNERSHIP_TYPE;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.company.ConfirmationStatementApi;
import uk.gov.companieshouse.api.model.transaction.Resource;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.validationstatus.ValidationStatusResponse;
import uk.gov.companieshouse.confirmationstatementapi.client.OracleQueryClient;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.exception.CompanyNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.exception.NewConfirmationDateInvalidException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.exception.SicCodeInvalidException;
import uk.gov.companieshouse.confirmationstatementapi.exception.SubmissionNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.model.MockConfirmationStatementSubmissionData;
import uk.gov.companieshouse.confirmationstatementapi.model.SectionStatus;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.ConfirmationStatementSubmissionDao;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.ConfirmationStatementSubmissionDataDao;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.siccode.SicCodeDao;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.siccode.SicCodeDataDao;
import uk.gov.companieshouse.confirmationstatementapi.model.json.ConfirmationStatementSubmissionDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.ConfirmationStatementSubmissionJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.NextMadeUpToDateJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.siccode.SicCodeDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.siccode.SicCodeJson;
import uk.gov.companieshouse.confirmationstatementapi.model.mapping.ConfirmationStatementJsonDaoMapper;
import uk.gov.companieshouse.confirmationstatementapi.model.response.CompanyValidationResponse;
import uk.gov.companieshouse.confirmationstatementapi.repository.ConfirmationStatementSubmissionsRepository;

@ExtendWith(MockitoExtension.class)
class ConfirmationStatementServiceTest {

    private static final String COMPANY_NUMBER = "12345678";
    private static final String PASS_THROUGH = "13456";
    private static final String SUBMISSION_ID = "abcdefg";
    private static final LocalDate NEXT_MADE_UP_TO_DATE = LocalDate.of(2022, 2, 27);
    private static final LocalDate LAST_MADE_UP_TO_DATE = LocalDate.of(2021, 2, 27);
    private static final String NEXT_MADE_UP_TO_DATE_STRING = NEXT_MADE_UP_TO_DATE.format(DateTimeFormatter.ISO_DATE);

    @Mock
    private CompanyProfileService companyProfileService;

    @Mock
    private EligibilityService eligibilityService;

    @Mock
    private ConfirmationStatementSubmissionsRepository confirmationStatementSubmissionsRepository;

    @Mock
    private TransactionService transactionService;

    @Mock
    private ConfirmationStatementJsonDaoMapper confirmationStatementJsonDaoMapper;

    @Mock
    private OracleQueryClient oracleQueryClient;

    @Mock
    private Supplier<LocalDate> localDateSupplier;

    @Captor
    private ArgumentCaptor<Transaction> transactionCaptor;

    @Captor
    private ArgumentCaptor<ConfirmationStatementSubmissionDao> submissionCaptor;

    @InjectMocks
    private ConfirmationStatementService confirmationStatementService;

    private ConfirmationStatementSubmissionJson confirmationStatementSubmissionJson;

    private Transaction transaction;

    @BeforeEach
    void init() {
        ConfirmationStatementSubmissionDataJson confirmationStatementSubmissionDataJson =
                MockConfirmationStatementSubmissionData.getMockJsonData();

        transaction = new Transaction();
        transaction.setCompanyNumber(COMPANY_NUMBER);
        confirmationStatementSubmissionJson = new ConfirmationStatementSubmissionJson();
        confirmationStatementSubmissionJson.setId(SUBMISSION_ID);
        confirmationStatementSubmissionJson.setData(confirmationStatementSubmissionDataJson);
        ReflectionTestUtils.setField(confirmationStatementService, "isPaymentCheckFeatureEnabled", true);
        ReflectionTestUtils.setField(confirmationStatementService, "ecctStartDateStr", "2018-02-05");
    }

    @Test
    void createConfirmationStatement() throws ServiceException, CompanyNotFoundException {
        ReflectionTestUtils.setField(confirmationStatementService, "isValidationStatusEnabled", true);
        transaction = new Transaction();
        transaction.setId("abc");
        transaction.setCompanyNumber(COMPANY_NUMBER);
        CompanyProfileApi companyProfileApi = getTestCompanyProfileApi();
        var eligibilityResponse = new CompanyValidationResponse();
        eligibilityResponse.setEligibilityStatusCode(EligibilityStatusCode.COMPANY_VALID_FOR_SERVICE);
        var confirmationStatementSubmission = new ConfirmationStatementSubmissionDao();
        confirmationStatementSubmission.setId("ID");

        when(companyProfileService.getCompanyProfile(COMPANY_NUMBER)).thenReturn(companyProfileApi);
        when(eligibilityService.checkCompanyEligibility(companyProfileApi)).thenReturn(eligibilityResponse);
        when(confirmationStatementSubmissionsRepository.insert(any(ConfirmationStatementSubmissionDao.class))).thenReturn(confirmationStatementSubmission);
        when(confirmationStatementSubmissionsRepository.save(any(ConfirmationStatementSubmissionDao.class))).thenReturn(confirmationStatementSubmission);
        lenient().when(oracleQueryClient.isConfirmationStatementPaid(COMPANY_NUMBER, "2021-04-14")).thenReturn(true);
        LocalDate today = LocalDate.of(2021, 04, 14);
        when(localDateSupplier.get()).thenReturn(today);

        var response = this.confirmationStatementService.createConfirmationStatement(transaction, PASS_THROUGH);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(transactionService, times(1)).updateTransaction(transactionCaptor.capture(), any());

        Transaction transactionSent = transactionCaptor.getValue();
        Map<String, String> links = transactionSent.getResources().get("/transactions/abc/confirmation-statement/ID").getLinks();
        String validationStatus = links.get("validation_status");
        assertEquals("/transactions/abc/confirmation-statement/ID/validation-status", validationStatus);
        String costs = links.get("costs");
        assertNull(costs);
    }

    @Test
    void createPayableResourceConfirmationStatement() throws ServiceException, CompanyNotFoundException {
        // GIVEN
        transaction = new Transaction();
        transaction.setId("abc");
        transaction.setCompanyNumber(COMPANY_NUMBER);
        CompanyProfileApi companyProfileApi = getTestCompanyProfileApi();
        var eligibilityResponse = new CompanyValidationResponse();
        eligibilityResponse.setEligibilityStatusCode(EligibilityStatusCode.COMPANY_VALID_FOR_SERVICE);
        var confirmationStatementSubmission = new ConfirmationStatementSubmissionDao();
        confirmationStatementSubmission.setId("ID");

        when(companyProfileService.getCompanyProfile(COMPANY_NUMBER)).thenReturn(companyProfileApi);
        when(eligibilityService.checkCompanyEligibility(companyProfileApi)).thenReturn(eligibilityResponse);
        when(confirmationStatementSubmissionsRepository.insert(any(ConfirmationStatementSubmissionDao.class))).thenReturn(confirmationStatementSubmission);
        when(confirmationStatementSubmissionsRepository.save(any(ConfirmationStatementSubmissionDao.class))).thenReturn(confirmationStatementSubmission);
        when(oracleQueryClient.isConfirmationStatementPaid(COMPANY_NUMBER, NEXT_MADE_UP_TO_DATE_STRING)).thenReturn(false);
        LocalDate today = LocalDate.of(2022, 04, 14);
        when(localDateSupplier.get()).thenReturn(today);

// WHEN
        var response = this.confirmationStatementService.createConfirmationStatement(transaction, PASS_THROUGH);

// THEN
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(transactionService, times(1)).updateTransaction(transactionCaptor.capture(), any());

        Transaction transactionSent = transactionCaptor.getValue();
        Resource resource = transactionSent.getResources().get("/transactions/abc/confirmation-statement/ID");
        assertNotNull(resource, "Resource should not be null");
        Map<String, String> links = resource.getLinks();
        assertNotNull(links, "Links should not be null");
        String costs = links.get("costs");
        assertEquals("/transactions/abc/confirmation-statement/ID/costs", costs);

        verify(confirmationStatementSubmissionsRepository).save(submissionCaptor.capture());
        var confirmationStatementSubmissionDao = submissionCaptor.getValue();
        assertEquals(NEXT_MADE_UP_TO_DATE, confirmationStatementSubmissionDao.getData().getMadeUpToDate());
    }

    @Test
    void doesNotCheckPaymentWhenFeatureFlaggedOff() throws ServiceException, CompanyNotFoundException {
        // GIVEN
        ReflectionTestUtils.setField(confirmationStatementService, "isPaymentCheckFeatureEnabled", false);
        transaction = new Transaction();
        transaction.setId("abc");
        transaction.setCompanyNumber(COMPANY_NUMBER);
        CompanyProfileApi companyProfileApi = getTestCompanyProfileApi();
        var eligibilityResponse = new CompanyValidationResponse();
        eligibilityResponse.setEligibilityStatusCode(EligibilityStatusCode.COMPANY_VALID_FOR_SERVICE);
        var confirmationStatementSubmission = new ConfirmationStatementSubmissionDao();
        confirmationStatementSubmission.setId("ID");

        when(companyProfileService.getCompanyProfile(COMPANY_NUMBER)).thenReturn(companyProfileApi);
        when(eligibilityService.checkCompanyEligibility(companyProfileApi)).thenReturn(eligibilityResponse);
        when(confirmationStatementSubmissionsRepository.insert(any(ConfirmationStatementSubmissionDao.class))).thenReturn(confirmationStatementSubmission);
        when(confirmationStatementSubmissionsRepository.save(any(ConfirmationStatementSubmissionDao.class))).thenReturn(confirmationStatementSubmission);
        LocalDate today = LocalDate.of(2021, 04, 14);
        when(localDateSupplier.get()).thenReturn(today);

// WHEN
        var response = this.confirmationStatementService.createConfirmationStatement(transaction, PASS_THROUGH);

// THEN
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(transactionService, times(1)).updateTransaction(any(), any());
        verify(oracleQueryClient, times(0)).isConfirmationStatementPaid(any(), any());
    }

    @Test
    void createConfirmationStatementFailingStatusValidation() throws ServiceException, CompanyNotFoundException {
        // GIVEN
        transaction = new Transaction();
        transaction.setCompanyNumber(COMPANY_NUMBER);
        CompanyProfileApi companyProfileApi = new CompanyProfileApi();
        companyProfileApi.setCompanyStatus("FailureValue");

        when(companyProfileService.getCompanyProfile(COMPANY_NUMBER)).thenReturn(companyProfileApi);

        CompanyValidationResponse companyValidationResponse = new CompanyValidationResponse();
        companyValidationResponse.setEligibilityStatusCode(EligibilityStatusCode.INVALID_COMPANY_STATUS);
        when(eligibilityService.checkCompanyEligibility(companyProfileApi))
                .thenReturn(companyValidationResponse);

        // WHEN
        var response = this.confirmationStatementService.createConfirmationStatement(transaction, PASS_THROUGH);

        // THEN
        CompanyValidationResponse responseBody = (CompanyValidationResponse) response.getBody();
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(responseBody);
        assertEquals(EligibilityStatusCode.INVALID_COMPANY_STATUS, responseBody.getEligibilityStatusCode());
    }

    @Test
    void createConfirmationStatementExistingStatementError() throws ServiceException, CompanyNotFoundException {
        // GIVEN
        transaction = new Transaction();
        transaction.setCompanyNumber(COMPANY_NUMBER);
        transaction.setId("abc");
        Resource resource = new Resource();
        resource.setKind("confirmation-statement");
        Map<String, Resource> resourceMap = new HashMap<>();
        resourceMap.put("/transactions/abc/confirmation-statement/ID", resource);
        transaction.setResources(resourceMap);
        CompanyProfileApi companyProfileApi = getTestCompanyProfileApi();
        var eligibilityResponse = new CompanyValidationResponse();
        eligibilityResponse.setEligibilityStatusCode(EligibilityStatusCode.COMPANY_VALID_FOR_SERVICE);

        when(companyProfileService.getCompanyProfile(COMPANY_NUMBER)).thenReturn(companyProfileApi);
        when(eligibilityService.checkCompanyEligibility(companyProfileApi)).thenReturn(eligibilityResponse);

        // WHEN
        var response = this.confirmationStatementService.createConfirmationStatement(transaction, PASS_THROUGH);

        // THEN
        var responseBody = response.getBody();
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(responseBody);
    }

    @Test
    void createConfirmationStatementCompanyNotFound() throws ServiceException, CompanyNotFoundException {
        // GIVEN
        transaction = new Transaction();
        transaction.setCompanyNumber(COMPANY_NUMBER);
        when(companyProfileService.getCompanyProfile(COMPANY_NUMBER)).thenThrow(new CompanyNotFoundException());

        // THEN
        assertThrows(ServiceException.class, () -> this.confirmationStatementService.createConfirmationStatement(transaction, PASS_THROUGH));
    }

    @Test
    void updateConfirmationSubmission() throws ServiceException, NewConfirmationDateInvalidException, SicCodeInvalidException {
        // GIVEN
        var sicCodeDataDao = new SicCodeDataDao();
        sicCodeDataDao.setSectionStatus(SectionStatus.CONFIRMED);
        var dataDao = new ConfirmationStatementSubmissionDataDao();
        dataDao.setSicCodeData(sicCodeDataDao);

        var confirmationStatementSubmission = new ConfirmationStatementSubmissionDao();
        confirmationStatementSubmission.setData(new ConfirmationStatementSubmissionDataDao());
        confirmationStatementSubmission.setId(SUBMISSION_ID);

        when(confirmationStatementJsonDaoMapper.jsonToDao(confirmationStatementSubmissionJson)).thenReturn(confirmationStatementSubmission);
        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.of(confirmationStatementSubmission));
        when(confirmationStatementSubmissionsRepository.save(any(ConfirmationStatementSubmissionDao.class))).thenReturn(confirmationStatementSubmission);

        // WHEN
        var result = confirmationStatementService
                .updateConfirmationStatement(transaction, SUBMISSION_ID, confirmationStatementSubmissionJson);

        // THEN
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }


    @ParameterizedTest
    @CsvSource({
            "2025-09-01",
            "2025-2-3",
            "2025-03-2",
            "2025-5-02",
    })
    void updateConfirmationSubmissionWithNewConfirmationStatementDate(String inputStringDate) throws ServiceException, NewConfirmationDateInvalidException, CompanyNotFoundException, SicCodeInvalidException {
        // GIVEN
        CompanyProfileApi companyProfileApi = getTestCompanyProfileApi();
        confirmationStatementSubmissionJson.getData().setNewConfirmationDate(inputStringDate);
        var confirmationStatementSubmission = new ConfirmationStatementSubmissionDao();
        confirmationStatementSubmission.setData(new ConfirmationStatementSubmissionDataDao());

        // WHEN
        when(companyProfileService.getCompanyProfile(COMPANY_NUMBER)).thenReturn(companyProfileApi);
        when(confirmationStatementJsonDaoMapper.jsonToDao(confirmationStatementSubmissionJson)).thenReturn(confirmationStatementSubmission);
        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.of(confirmationStatementSubmission));
        when(confirmationStatementSubmissionsRepository.save(any(ConfirmationStatementSubmissionDao.class))).thenReturn(confirmationStatementSubmission);

        // WHEN
        var result = confirmationStatementService
                .updateConfirmationStatement(transaction, SUBMISSION_ID, confirmationStatementSubmissionJson);

        // THEN
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @ParameterizedTest
    @CsvSource({
            "2025-09-01a, Confirmation statement date must be a real date",
            "2025-2-31, Confirmation statement date must be a real date",
            "2099-1-1, Confirmation statement date must be today or in the past",
            "2020-3-13, The date you enter must be after the date of the last confirmation statement",
            "2022-2-27, A confirmation statement has already been filed for the date you’ve entered",
    })
    void updateConfirmationSubmissionWithNewConfirmationDateInvalidException(String inputNewCsDateString, String errorMessage) throws ServiceException, CompanyNotFoundException {
        // GIVEN
        CompanyProfileApi companyProfileApi = getTestCompanyProfileApi();
        companyProfileApi.getConfirmationStatement().setLastMadeUpTo(LAST_MADE_UP_TO_DATE);
        confirmationStatementSubmissionJson.getData().setNewConfirmationDate(inputNewCsDateString);
        var confirmationStatementSubmission = new ConfirmationStatementSubmissionDao();

        // WHEN
        when(companyProfileService.getCompanyProfile(COMPANY_NUMBER)).thenReturn(companyProfileApi);
        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.of(confirmationStatementSubmission));

        // THEN
        NewConfirmationDateInvalidException exception = assertThrows(NewConfirmationDateInvalidException.class, () -> confirmationStatementService.updateConfirmationStatement(transaction, SUBMISSION_ID, confirmationStatementSubmissionJson));
        assertEquals(errorMessage, exception.getMessage());

    }

    @Test
    void updateConfirmationSubmissionNotFound() throws ServiceException, NewConfirmationDateInvalidException, SicCodeInvalidException  {
        // GIVEN
        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.empty());

        // WHEN
        var result = confirmationStatementService
                .updateConfirmationStatement(transaction, SUBMISSION_ID, confirmationStatementSubmissionJson);

        // THEN
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    void getConfirmationSubmission() {
        // GIVEN
        var confirmationStatementSubmission = new ConfirmationStatementSubmissionDao();
        confirmationStatementSubmission.setId(SUBMISSION_ID);
        when(confirmationStatementJsonDaoMapper.daoToJson(confirmationStatementSubmission)).thenReturn(confirmationStatementSubmissionJson);
        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.of(confirmationStatementSubmission));

        // WHEN
        var result = confirmationStatementService.getConfirmationStatement(SUBMISSION_ID);

        // THEN
        assertTrue(result.isPresent());
    }

    @Test
    void getConfirmationSubmissionNotFound() {
        //GIVEN
        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.empty());

        // WHEN
        var result = confirmationStatementService.getConfirmationStatement(SUBMISSION_ID);

        //THEN
        assertFalse(result.isPresent());
    }

    @Test
    void areTasksComplete() throws ServiceException, SubmissionNotFoundException {
        // GIVEN
        makeAllMockTasksConfirmed();
        var confirmationStatementSubmission = new ConfirmationStatementSubmissionDao();
        confirmationStatementSubmission.setId(SUBMISSION_ID);
        when(confirmationStatementJsonDaoMapper.daoToJson(confirmationStatementSubmission)).thenReturn(confirmationStatementSubmissionJson);
        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.of(confirmationStatementSubmission));
        when(localDateSupplier.get()).thenReturn(LocalDate.of(2021, 10, 12));

        // WHEN
        ValidationStatusResponse validationStatusResponse = confirmationStatementService.isValid(transaction, SUBMISSION_ID);

        // THEN
        assertTrue(validationStatusResponse.isValid());
    }

    @Test
    void areTasksCompleteWithSomeNotConfirmed() throws ServiceException, SubmissionNotFoundException {
        // GIVEN
        var confirmationStatementSubmission = new ConfirmationStatementSubmissionDao();
        confirmationStatementSubmission.setId(SUBMISSION_ID);
        when(confirmationStatementJsonDaoMapper.daoToJson(confirmationStatementSubmission)).thenReturn(confirmationStatementSubmissionJson);
        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.of(confirmationStatementSubmission));

        // WHEN
        ValidationStatusResponse validationStatusResponse = confirmationStatementService.isValid(transaction, SUBMISSION_ID);

        // THEN
        assertFalse(validationStatusResponse.isValid());
        verify(localDateSupplier, times(0)).get(); //check that this isn't the reason validation fails

    }

    @Test
    void areTasksCompleteWithSomeRecentFiling() throws ServiceException, SubmissionNotFoundException {
        // GIVEN
        makeAllMockTasksConfirmed();
        confirmationStatementSubmissionJson.getData().getPersonsSignificantControlData().setSectionStatus(SectionStatus.RECENT_FILING);
        confirmationStatementSubmissionJson.getData().getRegisteredEmailAddressData().setSectionStatus(SectionStatus.RECENT_FILING);
        var confirmationStatementSubmission = new ConfirmationStatementSubmissionDao();
        confirmationStatementSubmission.setId(SUBMISSION_ID);

        when(confirmationStatementJsonDaoMapper.daoToJson(confirmationStatementSubmission)).thenReturn(confirmationStatementSubmissionJson);
        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.of(confirmationStatementSubmission));
        when(localDateSupplier.get()).thenReturn(LocalDate.of(2021, 10, 12));

        // WHEN
        ValidationStatusResponse validationStatusResponse = confirmationStatementService.isValid(transaction, SUBMISSION_ID);

        // THEN
        assertTrue(validationStatusResponse.isValid());
    }

    @Test
    void areTasksCompleteWithREANotConfirmed_madeUpBeforeDayOne() throws ServiceException, SubmissionNotFoundException {
        // GIVEN
        var ecctStartDateStr = ReflectionTestUtils.getField(confirmationStatementService, "ecctStartDateStr");
        var ecctStartDate = LocalDate.parse(String.valueOf(ecctStartDateStr), ConfirmationStatementService.DATE_TIME_FORMATTER);
        var madeUpDate = ecctStartDate.minusDays(1);

        makeAllMockTasksConfirmed();
        confirmationStatementSubmissionJson.getData().setMadeUpToDate(madeUpDate);
        confirmationStatementSubmissionJson.getData().getRegisteredEmailAddressData().setSectionStatus(SectionStatus.NOT_CONFIRMED);
        var confirmationStatementSubmission = new ConfirmationStatementSubmissionDao();
        confirmationStatementSubmission.setId(SUBMISSION_ID);

        when(confirmationStatementJsonDaoMapper.daoToJson(confirmationStatementSubmission)).thenReturn(confirmationStatementSubmissionJson);
        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.of(confirmationStatementSubmission));
        when(localDateSupplier.get()).thenReturn(LocalDate.of(2021, 10, 12));

        // WHEN
        ValidationStatusResponse validationStatusResponse = confirmationStatementService.isValid(transaction, SUBMISSION_ID);

        // THEN
        assertTrue(validationStatusResponse.isValid());
    }

    @Test
    void areTasksCompleteWithREANotConfirmed_madeUpOnDayOne() throws ServiceException, SubmissionNotFoundException {
        // GIVEN

        var ecctStartDateStr = ReflectionTestUtils.getField(confirmationStatementService, "ecctStartDateStr");
        var ecctStartDate = LocalDate.parse(String.valueOf(ecctStartDateStr), ConfirmationStatementService.DATE_TIME_FORMATTER);

        makeAllMockTasksConfirmed();
        confirmationStatementSubmissionJson.getData().setMadeUpToDate(ecctStartDate);
        confirmationStatementSubmissionJson.getData().getRegisteredEmailAddressData().setSectionStatus(SectionStatus.NOT_CONFIRMED);
        var confirmationStatementSubmission = new ConfirmationStatementSubmissionDao();
        confirmationStatementSubmission.setId(SUBMISSION_ID);

        when(confirmationStatementJsonDaoMapper.daoToJson(confirmationStatementSubmission)).thenReturn(confirmationStatementSubmissionJson);
        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.of(confirmationStatementSubmission));

        // WHEN
        ValidationStatusResponse validationStatusResponse = confirmationStatementService.isValid(transaction, SUBMISSION_ID);

        // THEN
        assertFalse(validationStatusResponse.isValid());
        verify(localDateSupplier, times(0)).get(); //check that this isn't the reason validation fails

    }

    @Test
    void areTasksCompleteWithREANotConfirmed_madeUpAfterDayOne() throws ServiceException, SubmissionNotFoundException {
        // GIVEN
        var ecctStartDateStr = ReflectionTestUtils.getField(confirmationStatementService, "ecctStartDateStr");
        var ecctStartDate = LocalDate.parse(String.valueOf(ecctStartDateStr), ConfirmationStatementService.DATE_TIME_FORMATTER);
        var madeUpDate = ecctStartDate.plusDays(1);

        makeAllMockTasksConfirmed();
        confirmationStatementSubmissionJson.getData().setMadeUpToDate(madeUpDate);
        confirmationStatementSubmissionJson.getData().getRegisteredEmailAddressData().setSectionStatus(SectionStatus.NOT_CONFIRMED);
        var confirmationStatementSubmission = new ConfirmationStatementSubmissionDao();
        confirmationStatementSubmission.setId(SUBMISSION_ID);

        when(confirmationStatementJsonDaoMapper.daoToJson(confirmationStatementSubmission)).thenReturn(confirmationStatementSubmissionJson);
        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.of(confirmationStatementSubmission));

        // WHEN
        ValidationStatusResponse validationStatusResponse = confirmationStatementService.isValid(transaction, SUBMISSION_ID);

        // THEN
        assertFalse(validationStatusResponse.isValid());
        verify(localDateSupplier, times(0)).get(); //check that this isn't the reason validation fails
    }

    private static Stream<Arguments> provideArgumentsForAcceptLawfulPurpose() {
        var today = LocalDate.of(2021, 10, 12);

        final int madeUpDateBeforeEcct = -1;
        final int madeUpDateOnEcct = 0;
        final int madeUpDateAfterEcct = 1;

        return Stream.of(
                Arguments.of(madeUpDateBeforeEcct, null, today, true),
                Arguments.of(madeUpDateBeforeEcct, false, today, true),
                Arguments.of(madeUpDateBeforeEcct, true, today, true),

                Arguments.of(madeUpDateOnEcct, null, null, false),
                Arguments.of(madeUpDateOnEcct, false, null, false),
                Arguments.of(madeUpDateOnEcct, true, today, true),

                Arguments.of(madeUpDateAfterEcct, null, null, false),
                Arguments.of(madeUpDateAfterEcct, false, null, false),
                Arguments.of(madeUpDateAfterEcct, true, today, true)
        );
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsForAcceptLawfulPurpose")
    void areTasksCompleteWithAcceptLawfulPurposeStatementNotPresent(int madeUpDateOffset, Boolean acceptLawfulPurposeStatement, LocalDate today, boolean valid) throws ServiceException, SubmissionNotFoundException {
        // GIVEN
        var ecctStartDateStr = ReflectionTestUtils.getField(confirmationStatementService, "ecctStartDateStr");
        var ecctStartDate = LocalDate.parse((String) ecctStartDateStr, ConfirmationStatementService.DATE_TIME_FORMATTER);
        var madeUpDate = ecctStartDate.plusDays(madeUpDateOffset);

        makeAllMockTasksConfirmed();
        confirmationStatementSubmissionJson.getData().setAcceptLawfulPurposeStatement(acceptLawfulPurposeStatement);
        confirmationStatementSubmissionJson.getData().setMadeUpToDate(madeUpDate);
        var confirmationStatementSubmission = new ConfirmationStatementSubmissionDao();
        confirmationStatementSubmission.setId(SUBMISSION_ID);

        when(confirmationStatementJsonDaoMapper.daoToJson(confirmationStatementSubmission)).thenReturn(confirmationStatementSubmissionJson);
        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.of(confirmationStatementSubmission));
        if (today != null) {
            when(localDateSupplier.get()).thenReturn(today);
        }

        // WHEN
        ValidationStatusResponse validationStatusResponse = confirmationStatementService.isValid(transaction, SUBMISSION_ID);

        // THEN
        assertEquals(valid, validationStatusResponse.isValid());
    }

    @Test
    void areTasksIncompleteWithREAInitialFiling() throws ServiceException, SubmissionNotFoundException {
        // GIVEN

        makeAllMockTasksConfirmed();
        confirmationStatementSubmissionJson.getData().getRegisteredEmailAddressData().setSectionStatus(SectionStatus.INITIAL_FILING);
        var confirmationStatementSubmission = new ConfirmationStatementSubmissionDao();
        confirmationStatementSubmission.setId(SUBMISSION_ID);

        // WHEN

        when(confirmationStatementJsonDaoMapper.daoToJson(confirmationStatementSubmission)).thenReturn(confirmationStatementSubmissionJson);
        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.of(confirmationStatementSubmission));

        ValidationStatusResponse validationStatusResponse = confirmationStatementService.isValid(transaction, SUBMISSION_ID);

        // THEN

        assertFalse(validationStatusResponse.isValid());
        verify(localDateSupplier, times(0)).get(); //check that this isn't the reason validation fails

    }

    @Test
    void areTasksCompleteWithREAInitialFiling() throws ServiceException, SubmissionNotFoundException {
        // GIVEN
        makeAllMockTasksConfirmed();
        confirmationStatementSubmissionJson.getData().getRegisteredEmailAddressData().setSectionStatus(SectionStatus.INITIAL_FILING);
        confirmationStatementSubmissionJson.getData().getRegisteredEmailAddressData().setRegisteredEmailAddress("info@acme.com");
        var confirmationStatementSubmission = new ConfirmationStatementSubmissionDao();
        confirmationStatementSubmission.setId(SUBMISSION_ID);

        // WHEN
        when(confirmationStatementJsonDaoMapper.daoToJson(confirmationStatementSubmission)).thenReturn(confirmationStatementSubmissionJson);
        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.of(confirmationStatementSubmission));
        when(localDateSupplier.get()).thenReturn(LocalDate.of(2021, 10, 12));

        ValidationStatusResponse validationStatusResponse = confirmationStatementService.isValid(transaction, SUBMISSION_ID);

        // THEN
        assertTrue(validationStatusResponse.isValid());
    }

    @Test
    void areTasksCompleteWithSomeNotPresent() throws ServiceException, SubmissionNotFoundException {
        // GIVEN
        makeAllMockTasksConfirmed();
        confirmationStatementSubmissionJson.getData().setActiveOfficerDetailsData(null);
        var confirmationStatementSubmission = new ConfirmationStatementSubmissionDao();
        confirmationStatementSubmission.setId(SUBMISSION_ID);

        when(confirmationStatementJsonDaoMapper.daoToJson(confirmationStatementSubmission)).thenReturn(confirmationStatementSubmissionJson);
        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.of(confirmationStatementSubmission));

        // WHEN
        ValidationStatusResponse validationStatusResponse = confirmationStatementService.isValid(transaction, SUBMISSION_ID);

        // THEN
        assertFalse(validationStatusResponse.isValid());
        verify(localDateSupplier, times(0)).get(); //check that this isn't the reason validation fails

    }

    @Test
    void areTasksCompleteWithREANotPresent() throws ServiceException, SubmissionNotFoundException {
        // GIVEN
        makeAllMockTasksConfirmed();
        confirmationStatementSubmissionJson.getData().setRegisteredEmailAddressData(null);
        var confirmationStatementSubmission = new ConfirmationStatementSubmissionDao();
        confirmationStatementSubmission.setId(SUBMISSION_ID);

        when(confirmationStatementJsonDaoMapper.daoToJson(confirmationStatementSubmission)).thenReturn(confirmationStatementSubmissionJson);
        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.of(confirmationStatementSubmission));

        // WHEN
        ValidationStatusResponse validationStatusResponse = confirmationStatementService.isValid(transaction, SUBMISSION_ID);

        // THEN

        assertFalse(validationStatusResponse.isValid());
        verify(localDateSupplier, times(0)).get(); //check that this isn't the reason validation fails
    }

    @Test
    void areTasksCompleteWithNoSubmissionData() throws ServiceException, SubmissionNotFoundException {
        // GIVEN
        var confirmationStatementSubmission = new ConfirmationStatementSubmissionDao();
        confirmationStatementSubmission.setId(SUBMISSION_ID);
        confirmationStatementSubmissionJson.setData(null);
        when(confirmationStatementJsonDaoMapper.daoToJson(confirmationStatementSubmission)).thenReturn(confirmationStatementSubmissionJson);
        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.of(confirmationStatementSubmission));

        // WHEN
        ValidationStatusResponse validationStatusResponse = confirmationStatementService.isValid(transaction, SUBMISSION_ID);

        // THEN
        assertFalse(validationStatusResponse.isValid());
        verify(localDateSupplier, times(0)).get(); //check that this isn't the reason validation fails

    }

    @Test
    void areTasksCompleteWithTradingStatusAnswerFalse() throws ServiceException, SubmissionNotFoundException {
        // GIVEN
        makeAllMockTasksConfirmed();
        confirmationStatementSubmissionJson.getData().getPersonsSignificantControlData().setSectionStatus(SectionStatus.RECENT_FILING);
        confirmationStatementSubmissionJson.getData().getTradingStatusData().setTradingStatusAnswer(false);
        var confirmationStatementSubmission = new ConfirmationStatementSubmissionDao();
        confirmationStatementSubmission.setId(SUBMISSION_ID);
        when(confirmationStatementJsonDaoMapper.daoToJson(confirmationStatementSubmission)).thenReturn(confirmationStatementSubmissionJson);
        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.of(confirmationStatementSubmission));

        // WHEN
        ValidationStatusResponse validationStatusResponse = confirmationStatementService.isValid(transaction, SUBMISSION_ID);

        // THEN
        assertFalse(validationStatusResponse.isValid());
        verify(localDateSupplier, times(0)).get(); //check that this isn't the reason validation fails
    }

    @Test
    void areTasksCompleteWithMadeUpToDateEqual() throws ServiceException, SubmissionNotFoundException {
        // GIVEN
        makeAllMockTasksConfirmed();
        var confirmationStatementSubmission = new ConfirmationStatementSubmissionDao();
        confirmationStatementSubmission.setId(SUBMISSION_ID);
        when(confirmationStatementJsonDaoMapper.daoToJson(confirmationStatementSubmission)).thenReturn(confirmationStatementSubmissionJson);
        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.of(confirmationStatementSubmission));
        when(localDateSupplier.get()).thenReturn(LocalDate.of(2021, 9, 12));

        // WHEN
        ValidationStatusResponse validationStatusResponse = confirmationStatementService.isValid(transaction, SUBMISSION_ID);

        // THEN
        assertTrue(validationStatusResponse.isValid());
    }

    @Test
    void areTasksCompleteWithFutureMadeUpToDate() throws ServiceException, SubmissionNotFoundException {
        // GIVEN
        makeAllMockTasksConfirmed();
        var confirmationStatementSubmission = new ConfirmationStatementSubmissionDao();
        confirmationStatementSubmission.setId(SUBMISSION_ID);
        when(confirmationStatementJsonDaoMapper.daoToJson(confirmationStatementSubmission)).thenReturn(confirmationStatementSubmissionJson);
        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.of(confirmationStatementSubmission));
        when(localDateSupplier.get()).thenReturn(LocalDate.of(2021, 4, 12));

        // WHEN
        ValidationStatusResponse validationStatusResponse = confirmationStatementService.isValid(transaction, SUBMISSION_ID);

        // THEN
        assertFalse(validationStatusResponse.isValid());
    }

    @Test
    void areTasksCompleteWithNullLocalDate() throws ServiceException, SubmissionNotFoundException {
        // GIVEN
        makeAllMockTasksConfirmed();
        var confirmationStatementSubmission = new ConfirmationStatementSubmissionDao();
        confirmationStatementSubmission.setId(SUBMISSION_ID);

        when(confirmationStatementJsonDaoMapper.daoToJson(confirmationStatementSubmission)).thenReturn(confirmationStatementSubmissionJson);
        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.of(confirmationStatementSubmission));
        when(localDateSupplier.get()).thenReturn(null);

        // WHEN
        ValidationStatusResponse validationStatusResponse = confirmationStatementService.isValid(transaction, SUBMISSION_ID);

        // THEN
        assertFalse(validationStatusResponse.isValid());
    }

    @Test
    void areTasksCompleteWithNullMadeUpToDate() throws ServiceException, SubmissionNotFoundException {
        // GIVEN
        makeAllMockTasksConfirmed();
        var confirmationStatementSubmission = new ConfirmationStatementSubmissionDao();
        confirmationStatementSubmission.setId(SUBMISSION_ID);
        confirmationStatementSubmissionJson.getData().setMadeUpToDate(null);

        when(confirmationStatementJsonDaoMapper.daoToJson(confirmationStatementSubmission)).thenReturn(confirmationStatementSubmissionJson);
        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.of(confirmationStatementSubmission));
        when(localDateSupplier.get()).thenReturn(LocalDate.of(2021, 9, 12));

        // WHEN
        ValidationStatusResponse validationStatusResponse = confirmationStatementService.isValid(transaction, SUBMISSION_ID);

        // THEN
        assertFalse(validationStatusResponse.isValid());
    }

    @Test
    void areTasksCompleteNoSubmission() {
        // GIVEN
        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.empty());

        // THEN
        assertThrows(SubmissionNotFoundException.class, () -> confirmationStatementService.isValid(transaction, SUBMISSION_ID));
    }

    @Test
    void getNextMadeUpToDateWhenFilingEarly() throws CompanyNotFoundException, ServiceException {
        // GIVEN
        CompanyProfileApi companyProfileApi = getTestCompanyProfileApi();
        when(companyProfileService.getCompanyProfile(COMPANY_NUMBER)).thenReturn(companyProfileApi);
        LocalDate beforeDate = LocalDate.of(2021, 1, 1);
        when(localDateSupplier.get()).thenReturn(beforeDate);

        // WHEN
        NextMadeUpToDateJson nextMadeUpToDateJson = confirmationStatementService.getNextMadeUpToDate(COMPANY_NUMBER);

        // THEN
        assertEquals(LocalDate.parse("2022-02-27", DateTimeFormatter.ISO_DATE), nextMadeUpToDateJson.getCurrentNextMadeUpToDate());
        assertFalse(nextMadeUpToDateJson.isDue());
        assertEquals(LocalDate.parse("2021-01-01", DateTimeFormatter.ISO_DATE), nextMadeUpToDateJson.getNewNextMadeUpToDate());
    }

    @Test
    void getNextMadeUpToDateWhenFilingLate() throws CompanyNotFoundException, ServiceException {
        // GIVEN
        CompanyProfileApi companyProfileApi = getTestCompanyProfileApi();
        when(companyProfileService.getCompanyProfile(COMPANY_NUMBER)).thenReturn(companyProfileApi);
        LocalDate afterDate = LocalDate.of(2022, 2, 28);
        when(localDateSupplier.get()).thenReturn(afterDate);

        // WHEN
        NextMadeUpToDateJson nextMadeUpToDateJson = confirmationStatementService.getNextMadeUpToDate(COMPANY_NUMBER);

        // THEN
        assertEquals(LocalDate.parse("2022-02-27", DateTimeFormatter.ISO_DATE), nextMadeUpToDateJson.getCurrentNextMadeUpToDate());
        assertTrue(nextMadeUpToDateJson.isDue());
        assertNull(nextMadeUpToDateJson.getNewNextMadeUpToDate());
    }

    @Test
    void getNextMadeUpToDateWhenFilingOnNextMadeUpToDate() throws CompanyNotFoundException, ServiceException {
        // GIVEN
        CompanyProfileApi companyProfileApi = getTestCompanyProfileApi();
        when(companyProfileService.getCompanyProfile(COMPANY_NUMBER)).thenReturn(companyProfileApi);
        LocalDate sameDate = LocalDate.of(2022, 2, 27);
        when(localDateSupplier.get()).thenReturn(sameDate);

        // WHEN
        NextMadeUpToDateJson nextMadeUpToDateJson = confirmationStatementService.getNextMadeUpToDate(COMPANY_NUMBER);

        // THEN
        assertEquals(LocalDate.parse("2022-02-27", DateTimeFormatter.ISO_DATE), nextMadeUpToDateJson.getCurrentNextMadeUpToDate());
        assertTrue(nextMadeUpToDateJson.isDue());
        assertNull(nextMadeUpToDateJson.getNewNextMadeUpToDate());
    }

    @Test
    void getNextMadeUpToDateWhenCompanyProfileConfirmationStatementIsNull() throws CompanyNotFoundException, ServiceException {
        // GIVEN
        CompanyProfileApi companyProfileApi = getTestCompanyProfileApi();
        companyProfileApi.setConfirmationStatement(null);
        when(companyProfileService.getCompanyProfile(COMPANY_NUMBER)).thenReturn(companyProfileApi);

        // WHEN
        NextMadeUpToDateJson nextMadeUpToDateJson = confirmationStatementService.getNextMadeUpToDate(COMPANY_NUMBER);

        //THEN
        assertNull(nextMadeUpToDateJson.getCurrentNextMadeUpToDate());
        assertNull(nextMadeUpToDateJson.isDue());
        assertNull(nextMadeUpToDateJson.getNewNextMadeUpToDate());
    }

    @Test
    void getNextMadeUpToDateWhenCompanyProfileNextMadeUpToDateIsNull() throws CompanyNotFoundException, ServiceException {
        // GIVEN
        CompanyProfileApi companyProfileApi = getTestCompanyProfileApi();
        companyProfileApi.getConfirmationStatement().setNextMadeUpTo(null);
        when(companyProfileService.getCompanyProfile(COMPANY_NUMBER)).thenReturn(companyProfileApi);

        // WHEN
        NextMadeUpToDateJson nextMadeUpToDateJson = confirmationStatementService.getNextMadeUpToDate(COMPANY_NUMBER);

        // THEN
        assertNull(nextMadeUpToDateJson.getCurrentNextMadeUpToDate());
        assertNull(nextMadeUpToDateJson.isDue());
        assertNull(nextMadeUpToDateJson.getNewNextMadeUpToDate());
    }

    @Test
    void getNextMadeUpToDateThrowsExceptionWhenCompanyProfileNotFound() throws CompanyNotFoundException, ServiceException {
        // GIVEN
        when(companyProfileService.getCompanyProfile(COMPANY_NUMBER)).thenThrow(new CompanyNotFoundException());

        // THEN
        assertThrows(CompanyNotFoundException.class, () -> this.confirmationStatementService.getNextMadeUpToDate(COMPANY_NUMBER));
    }

    @Test
    void getNextMadeUpToDateThrowsExceptionWhenCompanyProfileIsNull() throws CompanyNotFoundException, ServiceException {
        // GIVEN
        when(companyProfileService.getCompanyProfile(COMPANY_NUMBER)).thenReturn(null);

        //THEN
        assertThrows(ServiceException.class, () -> this.confirmationStatementService.getNextMadeUpToDate(COMPANY_NUMBER));
    }

    @ParameterizedTest
    @CsvSource({
            "true, true",
            "false, false"
    })
    void validateStatusForLimitedPartnershipJourney(boolean acceptLawfulPurposeStatement, boolean expectedResult) throws ServiceException, SubmissionNotFoundException, CompanyNotFoundException {
        // GIVEN
        ConfirmationStatementSubmissionJson lpConfirmationStatementSubmissionJson = new ConfirmationStatementSubmissionJson();
        lpConfirmationStatementSubmissionJson.setData(new ConfirmationStatementSubmissionDataJson());
        lpConfirmationStatementSubmissionJson.getData().setMadeUpToDate(LocalDate.of(2021, 9, 12));
        if (acceptLawfulPurposeStatement) {
            lpConfirmationStatementSubmissionJson.getData().setAcceptLawfulPurposeStatement(acceptLawfulPurposeStatement);
        }
        var confirmationStatementSubmission = new ConfirmationStatementSubmissionDao();
        when(confirmationStatementJsonDaoMapper.daoToJson(confirmationStatementSubmission)).thenReturn(lpConfirmationStatementSubmissionJson);
        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.of(confirmationStatementSubmission));
        CompanyProfileApi companyProfileApi = getTestCompanyProfileApi();
        companyProfileApi.setType(LIMITED_PARTNERSHIP_TYPE);
        when(companyProfileService.getCompanyProfile(COMPANY_NUMBER)).thenReturn(companyProfileApi);

        // WHEN
        ValidationStatusResponse validationStatusResponse = confirmationStatementService.isValid(transaction, SUBMISSION_ID);

        // THEN
        assertEquals(expectedResult, validationStatusResponse.isValid());
    }

    @Test
    void returnFalseIfMissingSubmissionDataWhenValidateLpAndNoChangeJourney(){

        // WHEN
        boolean isValidForNoChangeJourney = confirmationStatementService.isValidForCsNoChangeJourney(null);

        // THEN
        assertFalse(isValidForNoChangeJourney);
    }

    @Test
    void returnFalseIfMissingSubmissionDataWhenValidateLpJourney(){

        // WHEN
        boolean isValidForLpjourney = confirmationStatementService.isValidForLimitedPartnershipJourney(null);

        // THEN
        assertFalse(isValidForLpjourney);
    }

    @Test
    void updateConfirmationStatementUpdateSicCodes() throws ServiceException, NewConfirmationDateInvalidException, SicCodeInvalidException {
        // Given
        var existingDao = new ConfirmationStatementSubmissionDao();
        existingDao.setId(SUBMISSION_ID);

        var updatedDao = new ConfirmationStatementSubmissionDao();
        updatedDao.setId(SUBMISSION_ID);

        var updatedData = MockConfirmationStatementSubmissionData.getMockDaoData();
        updatedData.getSicCodeData().setSicCodes(List.of("12345", "67890"));
        updatedDao.setData(updatedData);

        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.of(existingDao));
        when(confirmationStatementJsonDaoMapper.jsonToDao(confirmationStatementSubmissionJson)).thenReturn(updatedDao);
        when(confirmationStatementSubmissionsRepository.save(any(ConfirmationStatementSubmissionDao.class))).thenReturn(updatedDao);

        // When
        var response = confirmationStatementService.updateConfirmationStatement(transaction, SUBMISSION_ID, confirmationStatementSubmissionJson);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(confirmationStatementSubmissionsRepository).save(submissionCaptor.capture());

        var savedDao = submissionCaptor.getValue();
        assertEquals(SUBMISSION_ID, savedDao.getId());
        assertNotNull(savedDao.getData());
        assertEquals(List.of("12345", "67890"), savedDao.getData().getSicCodeData().getSicCodes());
    }

    @Test
    void shouldPassWithUnqiueSicCodes() {
        var updatedData = MockConfirmationStatementSubmissionData.getMockJsonData();
        List<SicCodeJson> sicCodeJsonList = List.of("12345", "67890", "12121", "21212").stream()
            .map(code -> {
                SicCodeJson sic = new SicCodeJson();
                sic.setCode(code);
                return sic;
            })
            .toList();

        updatedData.getSicCodeData().setSicCode(sicCodeJsonList);
        assertDoesNotThrow(() -> ConfirmationStatementService.isValidSicCodes(updatedData));
    }

    @Test
    void shouldFailWithMoreThanFourSicCodes() {
        var updatedData = MockConfirmationStatementSubmissionData.getMockJsonData();
        List<SicCodeJson> sicCodeJsonList = List.of("12345", "67890", "12121", "21212", "55661").stream()
            .map(code -> {
                SicCodeJson sic = new SicCodeJson();
                sic.setCode(code);
                return sic;
            })
            .toList();

        updatedData.getSicCodeData().setSicCode(sicCodeJsonList);
        assertThrows(SicCodeInvalidException.class, () -> ConfirmationStatementService.isValidSicCodes(updatedData));
    }

    @Test
    void shouldFailWithDuplicateSicCodes() {
        var updatedData = MockConfirmationStatementSubmissionData.getMockJsonData();
        List<SicCodeJson> sicCodeJsonList = List.of("12345", "67890", "12345").stream()
            .map(code -> {
                SicCodeJson sic = new SicCodeJson();
                sic.setCode(code);
                return sic;
            })
            .toList();

        updatedData.getSicCodeData().setSicCode(sicCodeJsonList);
        assertThrows(SicCodeInvalidException.class, () -> ConfirmationStatementService.isValidSicCodes(updatedData));
    }

    @Test
    void testGetDescription() {
        SicCodeDao sicCodeDao = new SicCodeDao();
        sicCodeDao.setDescription("TEST DESCRIPTION");
        assertEquals("TEST DESCRIPTION", sicCodeDao.getDescription());
    }

    @Test
    void testSicCodeJsonGetDescription() {
        SicCodeJson sicCodeJson = new SicCodeJson();
        sicCodeJson.setDescription("TEST DESCRIPTION");

        assertEquals("TEST DESCRIPTION", sicCodeJson.getDescription());
    }
    
    @Test
    void testGetSicCodesFromSicCodeDataDao() {
        SicCodeDataDao sicCodeDataDao = new SicCodeDataDao();
        List<String> codes = List.of("123");
        sicCodeDataDao.setSicCodes(codes);

        assertEquals(codes, sicCodeDataDao.getSicCodes());
    }

    @Test
    void updateConfirmationSubmissionWhenSicCodesPresent() throws ServiceException, NewConfirmationDateInvalidException, SicCodeInvalidException {
        // GIVEN
        var sicCodeJson1 = new SicCodeJson();
        sicCodeJson1.setCode("12345");
        var sicCodeJson2 = new SicCodeJson();
        sicCodeJson2.setCode("67890");

        var sicCodeDataJson = new SicCodeDataJson();
        sicCodeDataJson.setSicCode(List.of(sicCodeJson1, sicCodeJson2));

        var dataJson = confirmationStatementSubmissionJson.getData();
        dataJson.setSicCodeData(sicCodeDataJson);

        var dao = new ConfirmationStatementSubmissionDao();
        dao.setId(SUBMISSION_ID);

        var sicCodeDataDao = new SicCodeDataDao();
        sicCodeDataDao.setSicCodes(List.of("12345", "67890"));
        sicCodeDataDao.setSectionStatus(SectionStatus.CONFIRMED);

        var dataDao = new ConfirmationStatementSubmissionDataDao();
        dataDao.setSicCodeData(sicCodeDataDao);
        dao.setData(dataDao);

        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.of(dao));
        when(confirmationStatementJsonDaoMapper.jsonToDao(confirmationStatementSubmissionJson)).thenReturn(dao);
        when(confirmationStatementSubmissionsRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // WHEN
        var response = confirmationStatementService.updateConfirmationStatement(transaction, SUBMISSION_ID, confirmationStatementSubmissionJson);

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        var savedDao = (ConfirmationStatementSubmissionDao) response.getBody();
        assertNotNull(savedDao);
        assertEquals(SectionStatus.CONFIRMED, savedDao.getData().getSicCodeData().getSectionStatus());
    }

    @Test
    void updateConfirmationSubmissionExistingSicCodeDataAndSetsStatus() throws ServiceException, NewConfirmationDateInvalidException, SicCodeInvalidException {
        // GIVEN: existing submission in DB
        var existingDao = new ConfirmationStatementSubmissionDao();
        existingDao.setId(SUBMISSION_ID);
        existingDao.setData(new ConfirmationStatementSubmissionDataDao());

        // Incoming mapped DAO with SIC codes
        var sicCodeDataDao = new SicCodeDataDao();
        sicCodeDataDao.setSicCodes(List.of("12345", "67890"));
        sicCodeDataDao.setSectionStatus(SectionStatus.CONFIRMED);

        var dataDao = new ConfirmationStatementSubmissionDataDao();
        dataDao.setSicCodeData(sicCodeDataDao);

        var updatedDao = new ConfirmationStatementSubmissionDao();
        updatedDao.setId(SUBMISSION_ID);
        updatedDao.setData(dataDao);

        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.of(existingDao));
        when(confirmationStatementJsonDaoMapper.jsonToDao(confirmationStatementSubmissionJson)).thenReturn(updatedDao);
        when(confirmationStatementSubmissionsRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // WHEN
        var response = confirmationStatementService.updateConfirmationStatement(transaction, SUBMISSION_ID, confirmationStatementSubmissionJson);

        // THEN
        var savedDao = (ConfirmationStatementSubmissionDao) response.getBody();
        assertNotNull(savedDao);
        assertEquals(SectionStatus.CONFIRMED, savedDao.getData().getSicCodeData().getSectionStatus());
    }

    @Test
    void updateConfirmationSubmissionNoSetSectionStatusWhenSicCodesMissing() throws ServiceException, NewConfirmationDateInvalidException, SicCodeInvalidException {
        var sicCodeDataDao = new SicCodeDataDao();
        var dataDao = new ConfirmationStatementSubmissionDataDao();
        dataDao.setSicCodeData(sicCodeDataDao);

        var dao = new ConfirmationStatementSubmissionDao();
        dao.setId(SUBMISSION_ID);
        dao.setData(dataDao);

        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.of(dao));
        when(confirmationStatementJsonDaoMapper.jsonToDao(confirmationStatementSubmissionJson)).thenReturn(dao);
        when(confirmationStatementSubmissionsRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = confirmationStatementService.updateConfirmationStatement(transaction, SUBMISSION_ID, confirmationStatementSubmissionJson);

        var savedDao = (ConfirmationStatementSubmissionDao) response.getBody();
        assertNull(savedDao.getData().getSicCodeData().getSectionStatus());
    }

    
    private CompanyProfileApi getTestCompanyProfileApi() {
        CompanyProfileApi companyProfileApi = new CompanyProfileApi();
        companyProfileApi.setCompanyNumber(COMPANY_NUMBER);
        companyProfileApi.setCompanyStatus("AcceptValue");
        ConfirmationStatementApi confirmationStatement = new ConfirmationStatementApi();
        confirmationStatement.setNextDue(LocalDate.of(2022,1,1));
        confirmationStatement.setNextMadeUpTo(NEXT_MADE_UP_TO_DATE);
        companyProfileApi.setConfirmationStatement(confirmationStatement);
        return companyProfileApi;
    }

    void makeAllMockTasksConfirmed() {
        ConfirmationStatementSubmissionDataJson data = confirmationStatementSubmissionJson.getData();
        data.getActiveOfficerDetailsData().setSectionStatus(SectionStatus.CONFIRMED);
        data.getStatementOfCapitalData().setSectionStatus(SectionStatus.CONFIRMED);
        data.getSicCodeData().setSectionStatus(SectionStatus.CONFIRMED);
        data.getShareholderData() .setSectionStatus(SectionStatus.CONFIRMED);
        data.getRegisteredOfficeAddressData().setSectionStatus(SectionStatus.CONFIRMED);
        data.getRegisteredEmailAddressData().setSectionStatus(SectionStatus.CONFIRMED);
        data.getPersonsSignificantControlData().setSectionStatus(SectionStatus.CONFIRMED);
        data.getRegisterLocationsData().setSectionStatus(SectionStatus.CONFIRMED);
        data.setAcceptLawfulPurposeStatement(true);
    }
}
