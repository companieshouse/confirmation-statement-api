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
import uk.gov.companieshouse.confirmationstatementapi.client.OracleQueryClient;
import uk.gov.companieshouse.confirmationstatementapi.exception.ActiveOfficerNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.api.model.company.ActiveOfficerDetailsJson;
import uk.gov.companieshouse.confirmationstatementapi.repository.ConfirmationStatementSubmissionsRepository;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OfficerServiceTest {

    private static final String COMPANY_NUMBER = "12345678";

    @Mock
    private ApiClientService apiClientService;

    @Mock
    private ApiClient apiClient;

    @Mock
    private OracleQueryClient oracleQueryClient;

    @Mock
    private OfficersResourceHandler officersResourceHandler;

    @Mock
    private OfficersList officersList;

    @Mock
    private ApiResponse<OfficersApi> apiResponse;

    @Mock
    private ConfirmationStatementSubmissionsRepository confirmationStatementSubmissionsRepository;

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

    @Test
    void getActiveDirectorDetailsTest() throws ServiceException, ActiveOfficerNotFoundException {
        ActiveOfficerDetailsJson details = new ActiveOfficerDetailsJson();
        details.setForeName1("John");
        details.setSurname("Doe");
        when(oracleQueryClient.getActiveDirectorDetails(COMPANY_NUMBER)).thenReturn(details);

        var response = officerService.getActiveOfficerDetails(COMPANY_NUMBER);

        assertEquals(response, details);
    }

    @Test
    void getListActiveOfficersDetailsTest() throws ServiceException {
        ActiveOfficerDetailsJson officer1 = new ActiveOfficerDetailsJson();
        ActiveOfficerDetailsJson officer2 = new ActiveOfficerDetailsJson();
        List<ActiveOfficerDetailsJson> officers = Arrays.asList(officer1, officer2);
        when(oracleQueryClient.getActiveOfficersDetails(COMPANY_NUMBER)).thenReturn(officers);

        var response = officerService.getListActiveOfficersDetails(COMPANY_NUMBER);
        assertEquals(response, officers);
    }
}
