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
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.model.response.CompanyValidationResponse;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfirmationStatementServiceTest {

    private static final String COMPANY_NUMBER = "12345678";

    @Mock
    private CompanyProfileService companyProfileService;

    @Mock
    private EligibilityService eligibilityService;

    private ConfirmationStatementService confirmationStatementService;

    @BeforeEach
    void init() {
        confirmationStatementService = new ConfirmationStatementService(companyProfileService, eligibilityService);
    }

    @Test
    void createConfirmationStatement() throws ServiceException {
        Transaction transaction = new Transaction();
        transaction.setCompanyNumber(COMPANY_NUMBER);
        CompanyProfileApi companyProfileApi = new CompanyProfileApi();
        companyProfileApi.setCompanyStatus("AcceptValue");

        when(companyProfileService.getCompanyProfile(COMPANY_NUMBER)).thenReturn(companyProfileApi);
        when(eligibilityService.checkCompanyEligibility(companyProfileApi)).thenReturn(new CompanyValidationResponse());

        var response = this.confirmationStatementService.createConfirmationStatement(transaction);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void createConfirmationStatementFailingStatusValidation() throws ServiceException, EligibilityException {
        Transaction transaction = new Transaction();
        transaction.setCompanyNumber(COMPANY_NUMBER);
        CompanyProfileApi companyProfileApi = new CompanyProfileApi();
        companyProfileApi.setCompanyStatus("FailureValue");

        when(companyProfileService.getCompanyProfile(COMPANY_NUMBER)).thenReturn(companyProfileApi);

        CompanyValidationResponse companyValidationResponse = new CompanyValidationResponse();
        companyValidationResponse.setValidationError(EligibilityStatusCode.INVALID_COMPANY_STATUS);
        when(eligibilityService.checkCompanyEligibility(companyProfileApi))
                .thenReturn(companyValidationResponse);

        var response = this.confirmationStatementService.createConfirmationStatement(transaction);
        CompanyValidationResponse responseBody = (CompanyValidationResponse)response.getBody();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(responseBody);
        assertEquals(EligibilityStatusCode.INVALID_COMPANY_STATUS, responseBody.getValidationError());
    }
}