package uk.gov.companieshouse.confirmationstatementapi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.exception.CompanyNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.model.ConfirmationStatementSubmission;
import uk.gov.companieshouse.confirmationstatementapi.model.json.ConfirmationStatementSubmissionJson;
import uk.gov.companieshouse.confirmationstatementapi.model.response.CompanyValidationResponse;
import uk.gov.companieshouse.confirmationstatementapi.repository.ConfirmationStatementSubmissionsRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfirmationStatementServiceTest {

    private static final String COMPANY_NUMBER = "12345678";
    private static final String PASSTHROUGH = "13456";
    private static final String SUBMISSION_ID = "abcdefg";

    @Mock
    private CompanyProfileService companyProfileService;

    @Mock
    private EligibilityService eligibilityService;

    @Mock
    private ConfirmationStatementSubmissionsRepository confirmationStatementSubmissionsRepository;

    @Mock
    private TransactionService transactionService;

    private ConfirmationStatementService confirmationStatementService;

    private ConfirmationStatementSubmissionJson confirmationStatementSubmissionJson;

    @BeforeEach
    void init() {
        confirmationStatementService =
                new ConfirmationStatementService(companyProfileService, eligibilityService,
                        confirmationStatementSubmissionsRepository, transactionService);

        confirmationStatementSubmissionJson = new ConfirmationStatementSubmissionJson();
        confirmationStatementSubmissionJson.setId(SUBMISSION_ID);
    }

    @Test
    void createConfirmationStatement() throws ServiceException, CompanyNotFoundException {
        Transaction transaction = new Transaction();
        transaction.setCompanyNumber(COMPANY_NUMBER);
        CompanyProfileApi companyProfileApi = new CompanyProfileApi();
        companyProfileApi.setCompanyStatus("AcceptValue");
        var eligibilityResponse = new CompanyValidationResponse();
        eligibilityResponse.setEligibilityStatusCode(EligibilityStatusCode.COMPANY_VALID_FOR_SERVICE);
        var confirmationStatementSubmission = new ConfirmationStatementSubmission();
        confirmationStatementSubmission.setId("ID");

        when(companyProfileService.getCompanyProfile(COMPANY_NUMBER)).thenReturn(companyProfileApi);
        when(eligibilityService.checkCompanyEligibility(companyProfileApi)).thenReturn(eligibilityResponse);
        when(confirmationStatementSubmissionsRepository.insert(any(ConfirmationStatementSubmission.class))).thenReturn(confirmationStatementSubmission);
        when(confirmationStatementSubmissionsRepository.save(any(ConfirmationStatementSubmission.class))).thenReturn(confirmationStatementSubmission);

        var response = this.confirmationStatementService.createConfirmationStatement(transaction, PASSTHROUGH);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
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

        var response = this.confirmationStatementService.createConfirmationStatement(transaction, PASSTHROUGH);
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

        assertThrows(ServiceException.class, () -> {
            this.confirmationStatementService.createConfirmationStatement(transaction, PASSTHROUGH);
        });
    }

    @Test
    void updateConfirmationSubmission() {
        var confirmationStatementSubmission = new ConfirmationStatementSubmission();
        confirmationStatementSubmission.setId(SUBMISSION_ID);

        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.of(confirmationStatementSubmission));
        when(confirmationStatementSubmissionsRepository.save(any(ConfirmationStatementSubmission.class))).thenReturn(confirmationStatementSubmission);
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
        var confirmationStatementSubmission = new ConfirmationStatementSubmission();
        confirmationStatementSubmission.setId(SUBMISSION_ID);

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
}