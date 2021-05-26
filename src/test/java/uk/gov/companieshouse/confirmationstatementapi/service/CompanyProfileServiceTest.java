package uk.gov.companieshouse.confirmationstatementapi.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.company.CompanyResourceHandler;
import uk.gov.companieshouse.api.handler.company.request.CompanyGet;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.client.ApiClientService;
import uk.gov.companieshouse.confirmationstatementapi.exception.CompanyNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyProfileServiceTest {

    private static final String COMPANY_NUMBER = "12345678";
    @Mock
    private ApiClientService apiClientService;

    @Mock
    private ApiClient apiClient;

    @Mock
    private CompanyResourceHandler companyResourceHandler;

    @Mock
    private CompanyGet companyGet;

    @Mock
    private ApiResponse<CompanyProfileApi> apiResponse;

    @InjectMocks
    private CompanyProfileService companyProfileService;

    @Test
    void getCompanyProfile() throws ServiceException, ApiErrorResponseException, URIValidationException, CompanyNotFoundException {
        CompanyProfileApi companyProfile = new CompanyProfileApi();
        companyProfile.setCompanyName("COMPANY NAME");

        when(apiClientService.getApiKeyAuthenticatedClient()).thenReturn(apiClient);
        when(apiClient.company()).thenReturn(companyResourceHandler);
        when(companyResourceHandler.get("/company/" + COMPANY_NUMBER)).thenReturn(companyGet);
        when(companyGet.execute()).thenReturn(apiResponse);
        when(apiResponse.getData()).thenReturn(companyProfile);

        var response = companyProfileService.getCompanyProfile(COMPANY_NUMBER);

        assertEquals(companyProfile, response);

    }

    @Test
    void getCompanyProfileURIValidationException() throws ServiceException, ApiErrorResponseException, URIValidationException {
        when(apiClientService.getApiKeyAuthenticatedClient()).thenReturn(apiClient);
        when(apiClient.company()).thenReturn(companyResourceHandler);
        when(companyResourceHandler.get("/company/" + COMPANY_NUMBER)).thenReturn(companyGet);
        when(companyGet.execute()).thenThrow(new URIValidationException("ERROR"));

        assertThrows(ServiceException.class, () -> {
            companyProfileService.getCompanyProfile(COMPANY_NUMBER);
        });
    }

    @Test
    void getCompanyProfileApiErrorResponse() throws ServiceException, ApiErrorResponseException, URIValidationException {
        when(apiClientService.getApiKeyAuthenticatedClient()).thenReturn(apiClient);
        when(apiClient.company()).thenReturn(companyResourceHandler);
        when(companyResourceHandler.get("/company/" + COMPANY_NUMBER)).thenReturn(companyGet);
        when(companyGet.execute()).thenThrow(ApiErrorResponseException.fromIOException(new IOException("ERROR")));

        assertThrows(ServiceException.class, () -> {
            companyProfileService.getCompanyProfile(COMPANY_NUMBER);
        });
    }
}