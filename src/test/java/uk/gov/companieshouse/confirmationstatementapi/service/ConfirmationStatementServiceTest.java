package uk.gov.companieshouse.confirmationstatementapi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.company.ConfirmationStatementApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.confirmationstatementapi.client.OracleQueryClient;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.exception.CompanyNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.ConfirmationStatementSubmissionDao;
import uk.gov.companieshouse.confirmationstatementapi.model.MockConfirmationStatementSubmissionData;
import uk.gov.companieshouse.confirmationstatementapi.model.json.ConfirmationStatementSubmissionDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.ConfirmationStatementSubmissionJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.payment.ConfirmationStatementPaymentJson;
import uk.gov.companieshouse.confirmationstatementapi.model.mapping.ConfirmationStatementJsonDaoMapper;
import uk.gov.companieshouse.confirmationstatementapi.model.response.CompanyValidationResponse;
import uk.gov.companieshouse.confirmationstatementapi.repository.ConfirmationStatementSubmissionsRepository;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

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

    @Captor
    private ArgumentCaptor<Transaction> transactionCaptor;

    private ConfirmationStatementService confirmationStatementService;

    private ConfirmationStatementSubmissionJson confirmationStatementSubmissionJson;

    @BeforeEach
    void init() {
        ConfirmationStatementSubmissionDataJson confirmationStatementSubmissionDataJson =
                MockConfirmationStatementSubmissionData.getMockJsonData();
        confirmationStatementService =
                new ConfirmationStatementService(companyProfileService, eligibilityService,
                        confirmationStatementSubmissionsRepository, transactionService,
                        confirmationStatementJsonDaoMapper,
                        oracleQueryClient);

        confirmationStatementSubmissionJson = new ConfirmationStatementSubmissionJson();
        confirmationStatementSubmissionJson.setId(SUBMISSION_ID);
        confirmationStatementSubmissionJson.setData(confirmationStatementSubmissionDataJson);
    }

    @Test
    void createConfirmationStatement() throws ServiceException, CompanyNotFoundException {
        Transaction transaction = new Transaction();
        transaction.setId("abc");
        transaction.setCompanyNumber(COMPANY_NUMBER);
        CompanyProfileApi companyProfileApi = getTestCompanyProfileApi();
        var eligibilityResponse = new CompanyValidationResponse();
        eligibilityResponse.setEligibilityStatusCode(EligibilityStatusCode.COMPANY_VALID_FOR_SERVICE);
        var confirmationStatementSubmission = new ConfirmationStatementSubmissionDao();
        confirmationStatementSubmission.setId("ID");

        ConfirmationStatementPaymentJson confirmationStatementPaymentJson = new ConfirmationStatementPaymentJson();
        confirmationStatementPaymentJson.setPaid(Boolean.TRUE);

        when(companyProfileService.getCompanyProfile(COMPANY_NUMBER)).thenReturn(companyProfileApi);
        when(eligibilityService.checkCompanyEligibility(companyProfileApi)).thenReturn(eligibilityResponse);
        when(confirmationStatementSubmissionsRepository.insert(any(ConfirmationStatementSubmissionDao.class))).thenReturn(confirmationStatementSubmission);
        when(confirmationStatementSubmissionsRepository.save(any(ConfirmationStatementSubmissionDao.class))).thenReturn(confirmationStatementSubmission);
        when(oracleQueryClient.isConfirmationStatementPaid(COMPANY_NUMBER, "2022-01-01")).thenReturn(confirmationStatementPaymentJson);

        var response = this.confirmationStatementService.createConfirmationStatement(transaction, PASS_THROUGH);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(transactionService, times(1)).updateTransaction(transactionCaptor.capture(), any());

        Transaction transactionSent = transactionCaptor.getValue();
        Map<String, String> links = transactionSent.getResources().get("/transactions/abc/confirmation-statement/ID").getLinks();
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

        ConfirmationStatementPaymentJson confirmationStatementPaymentJson = new ConfirmationStatementPaymentJson();
        confirmationStatementPaymentJson.setPaid(Boolean.FALSE);

        when(companyProfileService.getCompanyProfile(COMPANY_NUMBER)).thenReturn(companyProfileApi);
        when(eligibilityService.checkCompanyEligibility(companyProfileApi)).thenReturn(eligibilityResponse);
        when(confirmationStatementSubmissionsRepository.insert(any(ConfirmationStatementSubmissionDao.class))).thenReturn(confirmationStatementSubmission);
        when(confirmationStatementSubmissionsRepository.save(any(ConfirmationStatementSubmissionDao.class))).thenReturn(confirmationStatementSubmission);
        when(oracleQueryClient.isConfirmationStatementPaid(COMPANY_NUMBER, "2022-01-01")).thenReturn(confirmationStatementPaymentJson);

        var response = this.confirmationStatementService.createConfirmationStatement(transaction, PASS_THROUGH);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(transactionService, times(1)).updateTransaction(transactionCaptor.capture(), any());

        Transaction transactionSent = transactionCaptor.getValue();
        Map<String, String> links = transactionSent.getResources().get("/transactions/abc/confirmation-statement/ID").getLinks();
        String costs = links.get("costs");
        assertEquals("/confirmation-statement/ID/costs", costs);
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

    private CompanyProfileApi getTestCompanyProfileApi() {
        CompanyProfileApi companyProfileApi = new CompanyProfileApi();
        companyProfileApi.setCompanyNumber(COMPANY_NUMBER);
        companyProfileApi.setCompanyStatus("AcceptValue");
        ConfirmationStatementApi confirmationStatement = new ConfirmationStatementApi();
        confirmationStatement.setNextDue(LocalDate.of(2022,1,1));
        companyProfileApi.setConfirmationStatement(confirmationStatement);
        return companyProfileApi;
    }
}