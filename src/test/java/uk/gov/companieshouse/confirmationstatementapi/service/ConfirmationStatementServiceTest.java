package uk.gov.companieshouse.confirmationstatementapi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.exception.SubmissionNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.model.SectionStatus;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.ConfirmationStatementSubmissionDao;
import uk.gov.companieshouse.confirmationstatementapi.model.MockConfirmationStatementSubmissionData;
import uk.gov.companieshouse.confirmationstatementapi.model.json.ConfirmationStatementSubmissionDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.ConfirmationStatementSubmissionJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.NextMadeUpToDateJson;
import uk.gov.companieshouse.confirmationstatementapi.model.mapping.ConfirmationStatementJsonDaoMapper;
import uk.gov.companieshouse.confirmationstatementapi.model.response.CompanyValidationResponse;
import uk.gov.companieshouse.confirmationstatementapi.repository.ConfirmationStatementSubmissionsRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfirmationStatementServiceTest {

    private static final String COMPANY_NUMBER = "12345678";
    private static final String PASS_THROUGH = "13456";
    private static final String SUBMISSION_ID = "abcdefg";
    private static final LocalDate NEXT_MADE_UP_TO_DATE = LocalDate.of(2022, 2, 27);
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

    @BeforeEach
    void init() {
        ConfirmationStatementSubmissionDataJson confirmationStatementSubmissionDataJson =
                MockConfirmationStatementSubmissionData.getMockJsonData();

        confirmationStatementSubmissionJson = new ConfirmationStatementSubmissionJson();
        confirmationStatementSubmissionJson.setId(SUBMISSION_ID);
        confirmationStatementSubmissionJson.setData(confirmationStatementSubmissionDataJson);
        ReflectionTestUtils.setField(confirmationStatementService, "isPaymentCheckFeatureEnabled", true);
        ReflectionTestUtils.setField(confirmationStatementService, "ecctStartDateStr", "20180205");
    }

    @Test
    void createConfirmationStatement() throws ServiceException, CompanyNotFoundException {
        ReflectionTestUtils.setField(confirmationStatementService, "isValidationStatusEnabled", true);
        Transaction transaction = new Transaction();
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
        when(oracleQueryClient.isConfirmationStatementPaid(COMPANY_NUMBER, "2021-04-14")).thenReturn(true);
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
        Transaction transaction = new Transaction();
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

        var response = this.confirmationStatementService.createConfirmationStatement(transaction, PASS_THROUGH);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(transactionService, times(1)).updateTransaction(transactionCaptor.capture(), any());

        Transaction transactionSent = transactionCaptor.getValue();
        Map<String, String> links = transactionSent.getResources().get("/transactions/abc/confirmation-statement/ID").getLinks();
        String costs = links.get("costs");
        assertEquals("/transactions/abc/confirmation-statement/ID/costs", costs);

        verify(confirmationStatementSubmissionsRepository).save(submissionCaptor.capture());
        var confirmationStatementSubmissionDao = submissionCaptor.getValue();
        assertEquals(NEXT_MADE_UP_TO_DATE, confirmationStatementSubmissionDao.getData().getMadeUpToDate());
    }

    @Test
    void doesNotCheckPaymentWhenFeatureFlaggedOff() throws ServiceException, CompanyNotFoundException {
        ReflectionTestUtils.setField(confirmationStatementService, "isPaymentCheckFeatureEnabled", false);
        Transaction transaction = new Transaction();
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

        var response = this.confirmationStatementService.createConfirmationStatement(transaction, PASS_THROUGH);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(transactionService, times(1)).updateTransaction(any(), any());
        verify(oracleQueryClient, times(0)).isConfirmationStatementPaid(any(),any());
    }

    @Test
    void createConfirmationStatementFailingStatusValidation() throws ServiceException, CompanyNotFoundException {
        Transaction transaction = new Transaction();
        transaction.setCompanyNumber(COMPANY_NUMBER);
        CompanyProfileApi companyProfileApi = new CompanyProfileApi();
        companyProfileApi.setCompanyStatus("FailureValue");

        when(companyProfileService.getCompanyProfile(COMPANY_NUMBER)).thenReturn(companyProfileApi);

        CompanyValidationResponse companyValidationResponse = new CompanyValidationResponse();
        companyValidationResponse.setEligibilityStatusCode(EligibilityStatusCode.INVALID_COMPANY_STATUS);
        when(eligibilityService.checkCompanyEligibility(companyProfileApi))
                .thenReturn(companyValidationResponse);

        var response = this.confirmationStatementService.createConfirmationStatement(transaction, PASS_THROUGH);
        CompanyValidationResponse responseBody = (CompanyValidationResponse)response.getBody();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(responseBody);
        assertEquals(EligibilityStatusCode.INVALID_COMPANY_STATUS, responseBody.getEligibilityStatusCode());
    }

    @Test
    void createConfirmationStatementExistingStatementError() throws ServiceException, CompanyNotFoundException {
        Transaction transaction = new Transaction();
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

        var response = this.confirmationStatementService.createConfirmationStatement(transaction, PASS_THROUGH);
        var responseBody = response.getBody();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(responseBody);
    }

    @Test
    void createConfirmationStatementCompanyNotFound() throws ServiceException, CompanyNotFoundException {
        Transaction transaction = new Transaction();
        transaction.setCompanyNumber(COMPANY_NUMBER);

        when(companyProfileService.getCompanyProfile(COMPANY_NUMBER)).thenThrow(new CompanyNotFoundException());

        assertThrows(ServiceException.class, () -> this.confirmationStatementService.createConfirmationStatement(transaction, PASS_THROUGH));
    }

    @Test
    void updateConfirmationSubmission() {
        var confirmationStatementSubmission = new ConfirmationStatementSubmissionDao();
        confirmationStatementSubmission.setId(SUBMISSION_ID);

        when(confirmationStatementJsonDaoMapper.jsonToDao(confirmationStatementSubmissionJson)).thenReturn(confirmationStatementSubmission);
        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.of(confirmationStatementSubmission));
        when(confirmationStatementSubmissionsRepository.save(any(ConfirmationStatementSubmissionDao.class))).thenReturn(confirmationStatementSubmission);
        var result = confirmationStatementService
                .updateConfirmationStatement(SUBMISSION_ID, confirmationStatementSubmissionJson);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void updateConfirmationSubmissionNotFound() {

        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.empty());
        var result = confirmationStatementService
                .updateConfirmationStatement(SUBMISSION_ID, confirmationStatementSubmissionJson);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    void getConfirmationSubmission() {
        var confirmationStatementSubmission = new ConfirmationStatementSubmissionDao();
        confirmationStatementSubmission.setId(SUBMISSION_ID);
        when(confirmationStatementJsonDaoMapper.daoToJson(confirmationStatementSubmission)).thenReturn(confirmationStatementSubmissionJson);
        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.of(confirmationStatementSubmission));
        var result = confirmationStatementService.getConfirmationStatement(SUBMISSION_ID);

        assertTrue(result.isPresent());
    }

    @Test
    void getConfirmationSubmissionNotFound() {
        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.empty());
        var result = confirmationStatementService.getConfirmationStatement(SUBMISSION_ID);

        assertFalse(result.isPresent());
    }

    @Test
    void areTasksComplete() throws SubmissionNotFoundException {
        makeAllMockTasksConfirmed();
        var confirmationStatementSubmission = new ConfirmationStatementSubmissionDao();
        confirmationStatementSubmission.setId(SUBMISSION_ID);
        when(confirmationStatementJsonDaoMapper.daoToJson(confirmationStatementSubmission)).thenReturn(confirmationStatementSubmissionJson);
        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.of(confirmationStatementSubmission));
        when(localDateSupplier.get()).thenReturn(LocalDate.of(2021, 10, 12));
        ValidationStatusResponse validationStatusResponse = confirmationStatementService.isValid(SUBMISSION_ID);
        assertTrue(validationStatusResponse.isValid());
    }

    @Test
    void areTasksCompleteWithSomeNotConfirmed() throws SubmissionNotFoundException {
        var confirmationStatementSubmission = new ConfirmationStatementSubmissionDao();
        confirmationStatementSubmission.setId(SUBMISSION_ID);
        when(confirmationStatementJsonDaoMapper.daoToJson(confirmationStatementSubmission)).thenReturn(confirmationStatementSubmissionJson);
        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.of(confirmationStatementSubmission));
        ValidationStatusResponse validationStatusResponse = confirmationStatementService.isValid(SUBMISSION_ID);
        assertFalse(validationStatusResponse.isValid());
    }

    @Test
    void areTasksCompleteWithSomeRecentFiling() throws SubmissionNotFoundException {
        // GIVEN

        makeAllMockTasksConfirmed();
        confirmationStatementSubmissionJson.getData().getPersonsSignificantControlData().setSectionStatus(SectionStatus.RECENT_FILING);
        confirmationStatementSubmissionJson.getData().getRegisteredEmailAddressData().setSectionStatus(SectionStatus.RECENT_FILING);
        var confirmationStatementSubmission = new ConfirmationStatementSubmissionDao();
        confirmationStatementSubmission.setId(SUBMISSION_ID);

        // WHEN

        when(confirmationStatementJsonDaoMapper.daoToJson(confirmationStatementSubmission)).thenReturn(confirmationStatementSubmissionJson);
        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.of(confirmationStatementSubmission));
        when(localDateSupplier.get()).thenReturn(LocalDate.of(2021, 10, 12));

        ValidationStatusResponse validationStatusResponse = confirmationStatementService.isValid(SUBMISSION_ID);

        // THEN

        assertTrue(validationStatusResponse.isValid());
    }

    @Test
    void areTasksCompleteWithREANotConfirmed_madeUpBeforeDayOne() throws SubmissionNotFoundException {
        // GIVEN

        var ecctStartDateStr = ReflectionTestUtils.getField(confirmationStatementService, "ecctStartDateStr");
        var ecctStartDate = LocalDate.parse((String) ecctStartDateStr, ConfirmationStatementService.ECCT_START_DATE_FORMATTER);
        var madeUpDate = ecctStartDate.minusDays(1);

        makeAllMockTasksConfirmed();
        confirmationStatementSubmissionJson.getData().setMadeUpToDate(madeUpDate);
        confirmationStatementSubmissionJson.getData().getRegisteredEmailAddressData().setSectionStatus(SectionStatus.NOT_CONFIRMED);
        var confirmationStatementSubmission = new ConfirmationStatementSubmissionDao();
        confirmationStatementSubmission.setId(SUBMISSION_ID);

        // WHEN

        when(confirmationStatementJsonDaoMapper.daoToJson(confirmationStatementSubmission)).thenReturn(confirmationStatementSubmissionJson);
        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.of(confirmationStatementSubmission));
        when(localDateSupplier.get()).thenReturn(LocalDate.of(2021, 10, 12));

        ValidationStatusResponse validationStatusResponse = confirmationStatementService.isValid(SUBMISSION_ID);

        // THEN

        assertTrue(validationStatusResponse.isValid());
    }

    @Test
    void areTasksCompleteWithREANotConfirmed_madeUpOnDayOne() throws SubmissionNotFoundException {
        // GIVEN

        var ecctStartDateStr = ReflectionTestUtils.getField(confirmationStatementService, "ecctStartDateStr");
        var ecctStartDate = LocalDate.parse((String) ecctStartDateStr, ConfirmationStatementService.ECCT_START_DATE_FORMATTER);
        var madeUpDate = ecctStartDate;

        makeAllMockTasksConfirmed();
        confirmationStatementSubmissionJson.getData().setMadeUpToDate(madeUpDate);
        confirmationStatementSubmissionJson.getData().getRegisteredEmailAddressData().setSectionStatus(SectionStatus.NOT_CONFIRMED);
        var confirmationStatementSubmission = new ConfirmationStatementSubmissionDao();
        confirmationStatementSubmission.setId(SUBMISSION_ID);

        // WHEN

        when(confirmationStatementJsonDaoMapper.daoToJson(confirmationStatementSubmission)).thenReturn(confirmationStatementSubmissionJson);
        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.of(confirmationStatementSubmission));

        ValidationStatusResponse validationStatusResponse = confirmationStatementService.isValid(SUBMISSION_ID);

        // THEN

        assertFalse(validationStatusResponse.isValid());
    }

    @Test
    void areTasksCompleteWithREANotConfirmed_madeUpAfterDayOne() throws SubmissionNotFoundException {
        // GIVEN

        var ecctStartDateStr = ReflectionTestUtils.getField(confirmationStatementService, "ecctStartDateStr");
        var ecctStartDate = LocalDate.parse((String) ecctStartDateStr, ConfirmationStatementService.ECCT_START_DATE_FORMATTER);
        var madeUpDate = ecctStartDate.plusDays(1);

        makeAllMockTasksConfirmed();
        confirmationStatementSubmissionJson.getData().setMadeUpToDate(madeUpDate);
        confirmationStatementSubmissionJson.getData().getRegisteredEmailAddressData().setSectionStatus(SectionStatus.NOT_CONFIRMED);
        var confirmationStatementSubmission = new ConfirmationStatementSubmissionDao();
        confirmationStatementSubmission.setId(SUBMISSION_ID);

        // WHEN

        when(confirmationStatementJsonDaoMapper.daoToJson(confirmationStatementSubmission)).thenReturn(confirmationStatementSubmissionJson);
        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.of(confirmationStatementSubmission));

        ValidationStatusResponse validationStatusResponse = confirmationStatementService.isValid(SUBMISSION_ID);

        // THEN

        assertFalse(validationStatusResponse.isValid());
    }

    @Test
    void areTasksCompleteWithREAInitialFiling() throws SubmissionNotFoundException {
        // GIVEN

        makeAllMockTasksConfirmed();
        confirmationStatementSubmissionJson.getData().getRegisteredEmailAddressData().setSectionStatus(SectionStatus.INITIAL_FILING);
        var confirmationStatementSubmission = new ConfirmationStatementSubmissionDao();
        confirmationStatementSubmission.setId(SUBMISSION_ID);

        // WHEN

        when(confirmationStatementJsonDaoMapper.daoToJson(confirmationStatementSubmission)).thenReturn(confirmationStatementSubmissionJson);
        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.of(confirmationStatementSubmission));
        when(localDateSupplier.get()).thenReturn(LocalDate.of(2021, 10, 12));
        ValidationStatusResponse validationStatusResponse = confirmationStatementService.isValid(SUBMISSION_ID);

        // THEN

        assertTrue(validationStatusResponse.isValid());
    }

    @Test
    void areTasksCompleteWithSomeNotPresent() throws SubmissionNotFoundException {
        makeAllMockTasksConfirmed();
        confirmationStatementSubmissionJson.getData().setActiveOfficerDetailsData(null);
        var confirmationStatementSubmission = new ConfirmationStatementSubmissionDao();
        confirmationStatementSubmission.setId(SUBMISSION_ID);
        when(confirmationStatementJsonDaoMapper.daoToJson(confirmationStatementSubmission)).thenReturn(confirmationStatementSubmissionJson);
        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.of(confirmationStatementSubmission));
        ValidationStatusResponse validationStatusResponse = confirmationStatementService.isValid(SUBMISSION_ID);
        assertFalse(validationStatusResponse.isValid());
    }

    @Test
    void areTasksCompleteWithREANotPresent() throws SubmissionNotFoundException {
        // GIVEN

        makeAllMockTasksConfirmed();
        confirmationStatementSubmissionJson.getData().setRegisteredEmailAddressData(null);
        var confirmationStatementSubmission = new ConfirmationStatementSubmissionDao();
        confirmationStatementSubmission.setId(SUBMISSION_ID);

        // WHEN

        when(confirmationStatementJsonDaoMapper.daoToJson(confirmationStatementSubmission)).thenReturn(confirmationStatementSubmissionJson);
        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.of(confirmationStatementSubmission));

        ValidationStatusResponse validationStatusResponse = confirmationStatementService.isValid(SUBMISSION_ID);

        // THEN

        assertFalse(validationStatusResponse.isValid());
    }

    @Test
    void areTasksCompleteWithNoSubmissionData() throws SubmissionNotFoundException {
        var confirmationStatementSubmission = new ConfirmationStatementSubmissionDao();
        confirmationStatementSubmission.setId(SUBMISSION_ID);
        confirmationStatementSubmissionJson.setData(null);
        when(confirmationStatementJsonDaoMapper.daoToJson(confirmationStatementSubmission)).thenReturn(confirmationStatementSubmissionJson);
        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.of(confirmationStatementSubmission));
        ValidationStatusResponse validationStatusResponse = confirmationStatementService.isValid(SUBMISSION_ID);
        assertFalse(validationStatusResponse.isValid());
    }

    @Test
    void areTasksCompleteWithTradingStatusAnswerFalse() throws SubmissionNotFoundException {
        makeAllMockTasksConfirmed();
        confirmationStatementSubmissionJson.getData().getPersonsSignificantControlData().setSectionStatus(SectionStatus.RECENT_FILING);
        confirmationStatementSubmissionJson.getData().getTradingStatusData().setTradingStatusAnswer(false);
        var confirmationStatementSubmission = new ConfirmationStatementSubmissionDao();
        confirmationStatementSubmission.setId(SUBMISSION_ID);
        when(confirmationStatementJsonDaoMapper.daoToJson(confirmationStatementSubmission)).thenReturn(confirmationStatementSubmissionJson);
        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.of(confirmationStatementSubmission));
        ValidationStatusResponse validationStatusResponse = confirmationStatementService.isValid(SUBMISSION_ID);
        assertFalse(validationStatusResponse.isValid());
    }

    @Test
    void areTasksCompleteWithMadeUpToDateEqual() throws SubmissionNotFoundException {
        makeAllMockTasksConfirmed();
        var confirmationStatementSubmission = new ConfirmationStatementSubmissionDao();
        confirmationStatementSubmission.setId(SUBMISSION_ID);
        when(confirmationStatementJsonDaoMapper.daoToJson(confirmationStatementSubmission)).thenReturn(confirmationStatementSubmissionJson);
        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.of(confirmationStatementSubmission));
        when(localDateSupplier.get()).thenReturn(LocalDate.of(2021, 9, 12));
        ValidationStatusResponse validationStatusResponse = confirmationStatementService.isValid(SUBMISSION_ID);
        assertTrue(validationStatusResponse.isValid());
    }

    @Test
    void areTasksCompleteWithFutureMadeUpToDate() throws SubmissionNotFoundException {
        makeAllMockTasksConfirmed();
        var confirmationStatementSubmission = new ConfirmationStatementSubmissionDao();
        confirmationStatementSubmission.setId(SUBMISSION_ID);
        when(confirmationStatementJsonDaoMapper.daoToJson(confirmationStatementSubmission)).thenReturn(confirmationStatementSubmissionJson);
        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.of(confirmationStatementSubmission));
        when(localDateSupplier.get()).thenReturn(LocalDate.of(2021, 4, 12));
        ValidationStatusResponse validationStatusResponse = confirmationStatementService.isValid(SUBMISSION_ID);
        assertFalse(validationStatusResponse.isValid());
    }

    @Test
    void areTasksCompleteWithNullLocalDate() throws SubmissionNotFoundException {
        makeAllMockTasksConfirmed();
        var confirmationStatementSubmission = new ConfirmationStatementSubmissionDao();
        confirmationStatementSubmission.setId(SUBMISSION_ID);
        when(confirmationStatementJsonDaoMapper.daoToJson(confirmationStatementSubmission)).thenReturn(confirmationStatementSubmissionJson);
        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.of(confirmationStatementSubmission));
        when(localDateSupplier.get()).thenReturn(null);
        ValidationStatusResponse validationStatusResponse = confirmationStatementService.isValid(SUBMISSION_ID);
        assertFalse(validationStatusResponse.isValid());
    }

    @Test
    void areTasksCompleteWithNullMadeUpToDate() throws SubmissionNotFoundException {
        makeAllMockTasksConfirmed();
        var confirmationStatementSubmission = new ConfirmationStatementSubmissionDao();
        confirmationStatementSubmission.setId(SUBMISSION_ID);
        confirmationStatementSubmissionJson.getData().setMadeUpToDate(null);
        when(confirmationStatementJsonDaoMapper.daoToJson(confirmationStatementSubmission)).thenReturn(confirmationStatementSubmissionJson);
        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.of(confirmationStatementSubmission));
        when(localDateSupplier.get()).thenReturn(LocalDate.of(2021, 9, 12));
        ValidationStatusResponse validationStatusResponse = confirmationStatementService.isValid(SUBMISSION_ID);
        assertFalse(validationStatusResponse.isValid());
    }

    @Test
    void areTasksCompleteNoSubmission() {
        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.empty());
        assertThrows(SubmissionNotFoundException.class, () -> confirmationStatementService.isValid(SUBMISSION_ID));
    }

    @Test
    void getNextMadeUpToDateWhenFilingEarly() throws CompanyNotFoundException, ServiceException {
        CompanyProfileApi companyProfileApi = getTestCompanyProfileApi();
        when(companyProfileService.getCompanyProfile(COMPANY_NUMBER)).thenReturn(companyProfileApi);
        LocalDate beforeDate = LocalDate.of(2021, 1, 1);
        when(localDateSupplier.get()).thenReturn(beforeDate);

        NextMadeUpToDateJson nextMadeUpToDateJson = confirmationStatementService.getNextMadeUpToDate(COMPANY_NUMBER);

        assertEquals(LocalDate.parse("2022-02-27", DateTimeFormatter.ISO_DATE), nextMadeUpToDateJson.getCurrentNextMadeUpToDate());
        assertFalse(nextMadeUpToDateJson.isDue());
        assertEquals(LocalDate.parse("2021-01-01", DateTimeFormatter.ISO_DATE), nextMadeUpToDateJson.getNewNextMadeUpToDate());
    }

    @Test
    void getNextMadeUpToDateWhenFilingLate() throws CompanyNotFoundException, ServiceException {
        CompanyProfileApi companyProfileApi = getTestCompanyProfileApi();
        when(companyProfileService.getCompanyProfile(COMPANY_NUMBER)).thenReturn(companyProfileApi);
        LocalDate afterDate = LocalDate.of(2022, 2, 28);
        when(localDateSupplier.get()).thenReturn(afterDate);

        NextMadeUpToDateJson nextMadeUpToDateJson = confirmationStatementService.getNextMadeUpToDate(COMPANY_NUMBER);

        assertEquals(LocalDate.parse("2022-02-27", DateTimeFormatter.ISO_DATE), nextMadeUpToDateJson.getCurrentNextMadeUpToDate());
        assertTrue(nextMadeUpToDateJson.isDue());
        assertNull(nextMadeUpToDateJson.getNewNextMadeUpToDate());
    }

    @Test
    void getNextMadeUpToDateWhenFilingOnNextMadeUpToDate() throws CompanyNotFoundException, ServiceException {
        CompanyProfileApi companyProfileApi = getTestCompanyProfileApi();
        when(companyProfileService.getCompanyProfile(COMPANY_NUMBER)).thenReturn(companyProfileApi);
        LocalDate sameDate = LocalDate.of(2022, 2, 27);
        when(localDateSupplier.get()).thenReturn(sameDate);

        NextMadeUpToDateJson nextMadeUpToDateJson = confirmationStatementService.getNextMadeUpToDate(COMPANY_NUMBER);

        assertEquals(LocalDate.parse("2022-02-27", DateTimeFormatter.ISO_DATE), nextMadeUpToDateJson.getCurrentNextMadeUpToDate());
        assertTrue(nextMadeUpToDateJson.isDue());
        assertNull(nextMadeUpToDateJson.getNewNextMadeUpToDate());
    }

    @Test
    void getNextMadeUpToDateWhenCompanyProfileConfirmationStatementIsNull() throws CompanyNotFoundException, ServiceException {
        CompanyProfileApi companyProfileApi = getTestCompanyProfileApi();
        companyProfileApi.setConfirmationStatement(null);
        when(companyProfileService.getCompanyProfile(COMPANY_NUMBER)).thenReturn(companyProfileApi);

        NextMadeUpToDateJson nextMadeUpToDateJson = confirmationStatementService.getNextMadeUpToDate(COMPANY_NUMBER);

        assertNull(nextMadeUpToDateJson.getCurrentNextMadeUpToDate());
        assertNull(nextMadeUpToDateJson.isDue());
        assertNull(nextMadeUpToDateJson.getNewNextMadeUpToDate());
    }

    @Test
    void getNextMadeUpToDateWhenCompanyProfileNextMadeUpToDateIsNull() throws CompanyNotFoundException, ServiceException {
        CompanyProfileApi companyProfileApi = getTestCompanyProfileApi();
        companyProfileApi.getConfirmationStatement().setNextMadeUpTo(null);
        when(companyProfileService.getCompanyProfile(COMPANY_NUMBER)).thenReturn(companyProfileApi);

        NextMadeUpToDateJson nextMadeUpToDateJson = confirmationStatementService.getNextMadeUpToDate(COMPANY_NUMBER);

        assertNull(nextMadeUpToDateJson.getCurrentNextMadeUpToDate());
        assertNull(nextMadeUpToDateJson.isDue());
        assertNull(nextMadeUpToDateJson.getNewNextMadeUpToDate());
    }

    @Test
    void getNextMadeUpToDateThrowsExceptionWhenCompanyProfileNotFound() throws CompanyNotFoundException, ServiceException {
        when(companyProfileService.getCompanyProfile(COMPANY_NUMBER)).thenThrow(new CompanyNotFoundException());

        assertThrows(CompanyNotFoundException.class, () -> this.confirmationStatementService.getNextMadeUpToDate(COMPANY_NUMBER));
    }

    @Test
    void getNextMadeUpToDateThrowsExceptionWhenCompanyProfileIsNull() throws CompanyNotFoundException, ServiceException {
        when(companyProfileService.getCompanyProfile(COMPANY_NUMBER)).thenReturn(null);

        assertThrows(ServiceException.class, () -> this.confirmationStatementService.getNextMadeUpToDate(COMPANY_NUMBER));
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
        data.getShareholdersData() .setSectionStatus(SectionStatus.CONFIRMED);
        data.getRegisteredOfficeAddressData().setSectionStatus(SectionStatus.CONFIRMED);
        data.getRegisteredEmailAddressData().setSectionStatus(SectionStatus.CONFIRMED);
        data.getPersonsSignificantControlData().setSectionStatus(SectionStatus.CONFIRMED);
        data.getRegisterLocationsData().setSectionStatus(SectionStatus.CONFIRMED);
    }
}
