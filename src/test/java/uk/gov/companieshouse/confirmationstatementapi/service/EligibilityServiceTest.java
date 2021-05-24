package uk.gov.companieshouse.confirmationstatementapi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.model.response.CompanyValidationResponse;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class EligibilityServiceTest {


    private List<EligibilityRule<CompanyProfileApi>> eligibilityRules;

    @Mock EligibilityRule<CompanyProfileApi> eligibilityRule;

    private EligibilityService eligibilityService;

    @BeforeEach
    void init() {
        eligibilityRules = new ArrayList<>();
        eligibilityRules.add(eligibilityRule);
        eligibilityService = new EligibilityService(eligibilityRules);
    }

    @Test
    void tesWithNoErrors() {
        CompanyProfileApi companyProfileApi = new CompanyProfileApi();
        companyProfileApi.setCompanyStatus("AcceptValue");
        var responseBody = eligibilityService.checkCompanyEligibility(companyProfileApi);
        assertNotNull(responseBody);
        assertNull(responseBody.getValidationError());
    }

    @Test
    void testInvalidCompanyStatus() throws EligibilityException {
        var responseBody = getValidationErrorResponse(EligibilityStatusCode.INVALID_COMPANY_STATUS);
        assertNotNull(responseBody);
        assertEquals(EligibilityStatusCode.INVALID_COMPANY_STATUS, responseBody.getValidationError());
    }

    @Test
    void testCS01filingNotRequired() throws EligibilityException {
        var responseBody = getValidationErrorResponse(EligibilityStatusCode.INVALID_COMPANY_TYPE_CS01_FILING_NOT_REQUIRED);
        assertNotNull(responseBody);
        assertEquals(EligibilityStatusCode.INVALID_COMPANY_TYPE_CS01_FILING_NOT_REQUIRED, responseBody.getValidationError());

    }

    @Test
    void testPaperFilingOnly() throws EligibilityException {
        var responseBody = getValidationErrorResponse(EligibilityStatusCode.INVALID_COMPANY_TYPE_PAPER_FILING_ONLY);
        assertNotNull(responseBody);
        assertEquals(EligibilityStatusCode.INVALID_COMPANY_TYPE_PAPER_FILING_ONLY, responseBody.getValidationError());

    }

    @Test
    void testUseWebFiling() throws EligibilityException {
        var responseBody = getValidationErrorResponse(EligibilityStatusCode.INVALID_COMPANY_TYPE_USE_WEB_FILING);
        assertNotNull(responseBody);
        assertEquals(EligibilityStatusCode.INVALID_COMPANY_TYPE_USE_WEB_FILING, responseBody.getValidationError());

    }

    private CompanyValidationResponse getValidationErrorResponse(EligibilityStatusCode reason) throws EligibilityException {
        CompanyProfileApi companyProfileApi = new CompanyProfileApi();
        companyProfileApi.setCompanyStatus("FailureValue");
        doThrow(new EligibilityException(reason)).when(eligibilityRule).validate(companyProfileApi);
        return eligibilityService.checkCompanyEligibility(companyProfileApi);
    }
}
