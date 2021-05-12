package uk.gov.companieshouse.confirmationstatementapi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityFailureReason;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfirmationStatementServiceTest {

    private static final String COMPANY_NUMBER = "12345678";

    @Mock
    private CompanyProfileService companyProfileService;

    private List<EligibilityRule<CompanyProfileApi>> eligibilityRules;

    @Mock EligibilityRule<CompanyProfileApi> eligibilityRule;

    private ConfirmationStatementService confirmationStatementService;

    @BeforeEach
    void init() {
        eligibilityRules = new ArrayList<>();
        eligibilityRules.add(eligibilityRule);

        confirmationStatementService = new ConfirmationStatementService(companyProfileService, eligibilityRules);
    }
    @Test
    void createConfirmationStatement() throws ServiceException {
        Transaction transaction = new Transaction();
        transaction.setCompanyNumber(COMPANY_NUMBER);
        CompanyProfileApi companyProfileApi = new CompanyProfileApi();
        companyProfileApi.setCompanyStatus("AcceptValue");

        when(companyProfileService.getCompanyProfile(COMPANY_NUMBER)).thenReturn(companyProfileApi);

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
        doThrow(new EligibilityException(EligibilityFailureReason.INVALID_COMPANY_STATUS)).when(eligibilityRule).validate(companyProfileApi);

        var response = this.confirmationStatementService.createConfirmationStatement(transaction);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(EligibilityFailureReason.INVALID_COMPANY_STATUS, response.getBody());
    }
}