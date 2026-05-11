package uk.gov.companieshouse.confirmationstatementapi.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.CompanyProfileApplicableEligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.model.response.CompanyValidationResponse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class EligibilityServiceTest {

    private static final LocalDate MADE_UP_DATE = LocalDate.parse("2026-05-01");

    @Mock
    EligibilityRule<CompanyProfileApi> eligibilityRule;
    @Mock
    CompanyProfileApplicableEligibilityRule companyProfileApplicableEligibilityRule;

    private EligibilityService eligibilityService;

    private CompanyProfileApi companyProfileApi;

    @BeforeEach
    void init() {
        List<EligibilityRule<CompanyProfileApi>> eligibilityRules = new ArrayList<>();
        eligibilityRules.add(eligibilityRule);
        eligibilityRules.add(companyProfileApplicableEligibilityRule);
        eligibilityService = new EligibilityService(eligibilityRules);
    }

    @AfterEach
    void closeDown() {
        verifyNoMoreInteractions(companyProfileApplicableEligibilityRule, eligibilityRule);
    }

    @Test
    void testWithNoErrors() throws EligibilityException, ServiceException {
        companyProfileApi = new CompanyProfileApi();
        companyProfileApi.setCompanyStatus("AcceptValue");
        var responseBody = eligibilityService.checkCompanyEligibility(companyProfileApi);
        verify(companyProfileApplicableEligibilityRule).validate(companyProfileApi);
        verify(eligibilityRule).validate(companyProfileApi);
        assertNotNull(responseBody);
        assertEquals(EligibilityStatusCode.COMPANY_VALID_FOR_SERVICE, responseBody.getEligibilityStatusCode());
    }

    @Test
    void testInvalidCompanyStatus() throws EligibilityException, ServiceException {
        var responseBody = getValidationErrorResponse(EligibilityStatusCode.INVALID_COMPANY_STATUS);
        assertNotNull(responseBody);
        assertEquals(EligibilityStatusCode.INVALID_COMPANY_STATUS, responseBody.getEligibilityStatusCode());
    }

    @Test
    void testCS01filingNotRequired() throws EligibilityException, ServiceException {
        var responseBody = getValidationErrorResponse(EligibilityStatusCode.INVALID_COMPANY_TYPE_CS01_FILING_NOT_REQUIRED);
        assertNotNull(responseBody);
        assertEquals(EligibilityStatusCode.INVALID_COMPANY_TYPE_CS01_FILING_NOT_REQUIRED, responseBody.getEligibilityStatusCode());

    }

    @Test
    void testPaperFilingOnly() throws EligibilityException, ServiceException {
        var responseBody = getValidationErrorResponse(EligibilityStatusCode.INVALID_COMPANY_TYPE_PAPER_FILING_ONLY);
        assertNotNull(responseBody);
        assertEquals(EligibilityStatusCode.INVALID_COMPANY_TYPE_PAPER_FILING_ONLY, responseBody.getEligibilityStatusCode());

    }

    @Test
    void testUseWebFiling() throws EligibilityException, ServiceException {
        var responseBody = getValidationErrorResponse(EligibilityStatusCode.INVALID_COMPANY_TYPE_USE_WEB_FILING);
        assertNotNull(responseBody);
        assertEquals(EligibilityStatusCode.INVALID_COMPANY_TYPE_USE_WEB_FILING, responseBody.getEligibilityStatusCode());

    }

    @Test
    void testCheckCompanyEligibilityAgainstMadeUpDateWithMadeUpDateNoErrors()  throws EligibilityException, ServiceException {
        companyProfileApi = new CompanyProfileApi();
        companyProfileApi.setCompanyStatus("AcceptValue");
        var responseBody = eligibilityService.checkCompanyEligibilityAgainstMadeUpDate(companyProfileApi, MADE_UP_DATE);
        verify(companyProfileApplicableEligibilityRule).validateAgainstMadeUpDate(companyProfileApi, MADE_UP_DATE);
        verify(eligibilityRule).validate(companyProfileApi);
        assertNotNull(responseBody);
        assertEquals(EligibilityStatusCode.COMPANY_VALID_FOR_SERVICE, responseBody.getEligibilityStatusCode());
    }

    @Test
    void testCheckCompanyEligibilityAgainstMadeUpDateWithMadeUpDateWithErrors() throws ServiceException, EligibilityException {
        var responseBody = getValidationErrorResponseCompanyProfileEligibilityRule(EligibilityStatusCode.INVALID_COMPANY_STATUS);

        verify(companyProfileApplicableEligibilityRule).validate(companyProfileApi);
        verify(eligibilityRule).validate(companyProfileApi);
        assertNotNull(responseBody);
        assertEquals(EligibilityStatusCode.INVALID_COMPANY_STATUS, responseBody.getEligibilityStatusCode());
    }

    private CompanyValidationResponse getValidationErrorResponse(EligibilityStatusCode reason) throws EligibilityException, ServiceException {
        companyProfileApi = new CompanyProfileApi();
        companyProfileApi.setCompanyStatus("FailureValue");
        doThrow(new EligibilityException(reason)).when(eligibilityRule).validate(companyProfileApi);
        return eligibilityService.checkCompanyEligibility(companyProfileApi);
    }

    private CompanyValidationResponse getValidationErrorResponseCompanyProfileEligibilityRule(EligibilityStatusCode reason) throws EligibilityException, ServiceException {
        companyProfileApi = new CompanyProfileApi();
        companyProfileApi.setCompanyStatus("FailureValue");
        doThrow(new EligibilityException(reason)).when(companyProfileApplicableEligibilityRule).validate(companyProfileApi);
        return eligibilityService.checkCompanyEligibility(companyProfileApi);
    }
}
