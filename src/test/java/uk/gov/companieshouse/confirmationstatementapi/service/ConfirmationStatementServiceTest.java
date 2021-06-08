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
import uk.gov.companieshouse.confirmationstatementapi.model.response.CompanyValidationResponse;
import uk.gov.companieshouse.confirmationstatementapi.repository.ConfirmationStatementSubmissionsRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfirmationStatementServiceTest {

    private static final String COMPANY_NUMBER = "12345678";
    private static final String PASSTHROUGH_HEADER = "abcdefg";

    @Mock
    private CompanyProfileService companyProfileService;

    @Mock
    private EligibilityService eligibilityService;

    @Mock
    private ConfirmationStatementSubmissionsRepository confirmationStatementSubmissionsRepository;

    @Mock
    private TransactionService transactionService;

    private ConfirmationStatementService confirmationStatementService;

    @BeforeEach
    void init() {
        confirmationStatementService =
                new ConfirmationStatementService(companyProfileService, eligibilityService, transactionService, confirmationStatementSubmissionsRepository);
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

        var response = this.confirmationStatementService.createConfirmationStatement(transaction, PASSTHROUGH_HEADER);

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

        var response = this.confirmationStatementService.createConfirmationStatement(transaction, PASSTHROUGH_HEADER);
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
            this.confirmationStatementService.createConfirmationStatement(transaction, PASSTHROUGH_HEADER);
        });
    }
}