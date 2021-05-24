package uk.gov.companieshouse.confirmationstatementapi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityFailureReason;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.model.response.EligibilityFailureResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
        assertFalse(responseBody.isPresent());
    }

    @Test
    void testInvalidCompanyStatus() throws EligibilityException {
        var responseBody = getValidationErrorResponse(EligibilityFailureReason.INVALID_COMPANY_STATUS);
        assertTrue(responseBody.isPresent());
        assertEquals(EligibilityFailureReason.INVALID_COMPANY_STATUS, responseBody.get().getValidationError());
    }

    @Test
    void testCS01filingNotRequired() throws EligibilityException {
        var responseBody = getValidationErrorResponse(EligibilityFailureReason.INVALID_COMPANY_TYPE_CS01_FILING_NOT_REQUIRED);
        assertTrue(responseBody.isPresent());
        assertEquals(EligibilityFailureReason.INVALID_COMPANY_TYPE_CS01_FILING_NOT_REQUIRED, responseBody.get().getValidationError());

    }

    @Test
    void testPaperFilingOnly() throws EligibilityException {
        var responseBody = getValidationErrorResponse(EligibilityFailureReason.INVALID_COMPANY_TYPE_PAPER_FILING_ONLY);
        assertTrue(responseBody.isPresent());
        assertEquals(EligibilityFailureReason.INVALID_COMPANY_TYPE_PAPER_FILING_ONLY, responseBody.get().getValidationError());

    }

    @Test
    void testUseWebFiling() throws EligibilityException {
        var responseBody = getValidationErrorResponse(EligibilityFailureReason.INVALID_COMPANY_TYPE_USE_WEB_FILING);
        assertTrue(responseBody.isPresent());
        assertEquals(EligibilityFailureReason.INVALID_COMPANY_TYPE_USE_WEB_FILING, responseBody.get().getValidationError());

    }

    private Optional<EligibilityFailureResponse> getValidationErrorResponse(EligibilityFailureReason reason) throws EligibilityException {
        CompanyProfileApi companyProfileApi = new CompanyProfileApi();
        companyProfileApi.setCompanyStatus("FailureValue");
        doThrow(new EligibilityException(reason)).when(eligibilityRule).validate(companyProfileApi);
        return eligibilityService.checkCompanyEligibility(companyProfileApi);
    }
}
