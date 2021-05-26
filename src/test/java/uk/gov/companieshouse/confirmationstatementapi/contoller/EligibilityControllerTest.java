package uk.gov.companieshouse.confirmationstatementapi.contoller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.exception.CompanyNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.model.response.CompanyValidationResponse;
import uk.gov.companieshouse.confirmationstatementapi.service.CompanyProfileService;
import uk.gov.companieshouse.confirmationstatementapi.service.EligibilityService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EligibilityControllerTest {

    private static final String COMPANY_NUMBER = "11111111";

    @Mock
    private CompanyProfileService companyProfileService;

    @Mock
    private EligibilityService eligibilityService;

    @InjectMocks
    private EligibilityController eligibilityController;

    private final ResponseEntity<Object> successResponse = ResponseEntity.ok("ok");
    private final ResponseEntity<Object> validationFailedResponse = ResponseEntity.badRequest().body("BAD");

    @Test
    void testSuccessfulGetEligibility() throws ServiceException, CompanyNotFoundException {
        CompanyProfileApi companyProfileApi = new CompanyProfileApi();
        companyProfileApi.setCompanyStatus("AcceptValue");
        when(companyProfileService.getCompanyProfile(COMPANY_NUMBER)).thenReturn(companyProfileApi);
        CompanyValidationResponse companyValidationResponse = new CompanyValidationResponse();
        companyValidationResponse.setEligibilityStatusCode(EligibilityStatusCode.COMPANY_VALID_FOR_SERVICE);
        when(eligibilityService.checkCompanyEligibility(companyProfileApi)).thenReturn(companyValidationResponse);
        ResponseEntity<CompanyValidationResponse> response = eligibilityController.getEligibility(COMPANY_NUMBER);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testFailedGetEligibility() throws ServiceException, CompanyNotFoundException {
        CompanyProfileApi companyProfileApi = new CompanyProfileApi();
        companyProfileApi.setCompanyStatus("FailureValue");
        when(companyProfileService.getCompanyProfile(COMPANY_NUMBER)).thenReturn(companyProfileApi);
        CompanyValidationResponse companyValidationResponse = new CompanyValidationResponse();
        companyValidationResponse.setEligibilityStatusCode(EligibilityStatusCode.INVALID_COMPANY_STATUS);
        when(eligibilityService.checkCompanyEligibility(companyProfileApi)).thenReturn(companyValidationResponse);
        ResponseEntity<CompanyValidationResponse> response = eligibilityController.getEligibility(COMPANY_NUMBER);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    void testServiceExceptionGetEligibility() throws ServiceException, CompanyNotFoundException {
        when(companyProfileService.getCompanyProfile(COMPANY_NUMBER)).thenThrow(new ServiceException("", new Exception()));
        ResponseEntity<CompanyValidationResponse> response = eligibilityController.getEligibility(COMPANY_NUMBER);
        assertEquals(500, response.getStatusCodeValue());
    }

    @Test
    void testCompanyNotFound() throws ServiceException, CompanyNotFoundException {
        when(companyProfileService.getCompanyProfile(COMPANY_NUMBER)).thenThrow(new CompanyNotFoundException());
        ResponseEntity<CompanyValidationResponse> response = eligibilityController.getEligibility(COMPANY_NUMBER);
        assertEquals(400, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(EligibilityStatusCode.COMPANY_NOT_FOUND, response.getBody().getEligibilityStatusCode());
    }
}
