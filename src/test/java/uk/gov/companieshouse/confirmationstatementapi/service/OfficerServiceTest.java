package uk.gov.companieshouse.confirmationstatementapi.service;

import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpResponseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.handler.officers.OfficersResourceHandler;
import uk.gov.companieshouse.api.handler.officers.request.OfficersList;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.officers.OfficersApi;
import uk.gov.companieshouse.confirmationstatementapi.client.ApiClientService;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OfficerServiceTest {

    private static final String COMPANY_NUMBER = "12345678";

    @Mock
    private ApiClientService apiClientService;

    @Mock
    private ApiClient apiClient;

    @Mock
    private OfficersResourceHandler officersResourceHandler;

    @Mock
    private OfficersList officersList;

    @Mock
    private ApiResponse<OfficersApi> apiResponse;

    @InjectMocks
    private OfficerService officerService;

    @Test
    void getOfficers() throws ServiceException, ApiErrorResponseException, URIValidationException {
        OfficersApi officers = new OfficersApi();

        when(apiClientService.getApiKeyAuthenticatedClient()).thenReturn(apiClient);
        when(apiClient.officers()).thenReturn(officersResourceHandler);
        when(officersResourceHandler.list("/company/" + COMPANY_NUMBER +"/officers")).thenReturn(officersList);
        when(officersList.execute()).thenReturn(apiResponse);
        when(apiResponse.getData()).thenReturn(officers);

        var response = officerService.getOfficers(COMPANY_NUMBER);

        assertEquals(officers, response);
    }

    @Test
    void getOfficerURIValidationException() throws ApiErrorResponseException, URIValidationException {
        when(apiClientService.getApiKeyAuthenticatedClient()).thenReturn(apiClient);
        when(apiClient.officers()).thenReturn(officersResourceHandler);
        when(officersResourceHandler.list("/company/" + COMPANY_NUMBER +"/officers")).thenReturn(officersList);
        when(officersList.execute()).thenThrow(new URIValidationException("ERROR"));

        assertThrows(ServiceException.class, () -> {
            officerService.getOfficers(COMPANY_NUMBER);
        });
    }

    @Test
    void getOfficerAPIErrorException() throws ApiErrorResponseException, URIValidationException {
        when(apiClientService.getApiKeyAuthenticatedClient()).thenReturn(apiClient);
        when(apiClient.officers()).thenReturn(officersResourceHandler);
        when(officersResourceHandler.list("/company/" + COMPANY_NUMBER + "/officers")).thenReturn(officersList);
        when(officersList.execute()).thenThrow(ApiErrorResponseException.fromIOException(new IOException("ERROR")));

        assertThrows(ServiceException.class, () -> {
            officerService.getOfficers(COMPANY_NUMBER);
        });
    }

    @Test
    void getOfficersProfileApiError404Response() throws IOException, URIValidationException, ServiceException {

        when(apiClientService.getApiKeyAuthenticatedClient()).thenReturn(apiClient);
        when(apiClient.officers()).thenReturn(officersResourceHandler);
        when(officersResourceHandler.list("/company/" + COMPANY_NUMBER + "/officers")).thenReturn(officersList);
        var builder = new HttpResponseException.Builder(404, "String", new HttpHeaders());
        when(officersList.execute()).thenThrow(new ApiErrorResponseException(builder));

        var response = officerService.getOfficers(COMPANY_NUMBER);

        assertNotNull(response);
        assertNull(response.getActiveCount());
        assertNull(response.getItemsPerPage());
        assertNull(response.getEtag());
        assertNull(response.getItems());
    }
}
