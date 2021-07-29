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
import uk.gov.companieshouse.api.handler.psc.PscsResourceHandler;
import uk.gov.companieshouse.api.handler.psc.request.PscsList;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.psc.PscsApi;
import uk.gov.companieshouse.confirmationstatementapi.client.ApiClientService;
import uk.gov.companieshouse.confirmationstatementapi.client.OracleQueryClient;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.model.PersonOfSignificantControl;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PscServiceTest {

    private static final String COMPANY_NUMBER = "12345678";
    
    @Mock
    private ApiClient apiClient;
    
    @Mock
    private ApiClientService apiClientService;

    @Mock
    private ApiResponse<PscsApi> apiResponse;
    
    @Mock
    private PscsResourceHandler pscResourceHandler;

    @Mock
    private PscsList pscsList;

    @Mock
    private OracleQueryClient oracleQueryClient;

    @InjectMocks
    private PscService pscService;

    @Test
    void getCompanyPscs() throws ApiErrorResponseException, URIValidationException, ServiceException {

        PscsApi pscsApi = new PscsApi();
        pscsApi.setActiveCount(3L);

        when(apiClientService.getApiKeyAuthenticatedClient()).thenReturn(apiClient);
        when(apiClient.pscs()).thenReturn(pscResourceHandler);
        when(pscResourceHandler.list("/company/" + COMPANY_NUMBER + "/persons-with-significant-control")).thenReturn(pscsList);
        when(pscsList.execute()).thenReturn(apiResponse);
        when(apiResponse.getData()).thenReturn(pscsApi);

        var response = pscService.getPSCsFromCHS(COMPANY_NUMBER);

        assertEquals(pscsApi, response);
        assertEquals(3, response.getActiveCount());
    }
    
    @Test
    void getCompanyPscsThrowsURIValidationException() throws ApiErrorResponseException, URIValidationException {
        when(apiClientService.getApiKeyAuthenticatedClient()).thenReturn(apiClient);
        when(apiClient.pscs()).thenReturn(pscResourceHandler);
        when(pscResourceHandler.list("/company/" + COMPANY_NUMBER + "/persons-with-significant-control")).thenReturn(pscsList);
        when(pscsList.execute()).thenThrow(new URIValidationException("ERROR"));

        assertThrows(ServiceException.class, () -> {
            pscService.getPSCsFromCHS(COMPANY_NUMBER);
        });
    }

    @Test
    void getPscsProfileApiErrorResponse() throws ApiErrorResponseException, URIValidationException {
        when(apiClientService.getApiKeyAuthenticatedClient()).thenReturn(apiClient);
        when(apiClient.pscs()).thenReturn(pscResourceHandler);
        when(pscResourceHandler.list("/company/" + COMPANY_NUMBER + "/persons-with-significant-control")).thenReturn(pscsList);
        when(pscsList.execute()).thenThrow(ApiErrorResponseException.fromIOException(new IOException("ERROR")));
        
        assertThrows(ServiceException.class, () -> {
            pscService.getPSCsFromCHS(COMPANY_NUMBER);
        });
    }

    @Test
    void getPscsProfileApiError404Response() throws IOException, URIValidationException, ServiceException {

        when(apiClientService.getApiKeyAuthenticatedClient()).thenReturn(apiClient);
        when(apiClient.pscs()).thenReturn(pscResourceHandler);
        when(pscResourceHandler.list("/company/" + COMPANY_NUMBER + "/persons-with-significant-control")).thenReturn(pscsList);
        var builder = new HttpResponseException.Builder(404, "String", new HttpHeaders());
        when(pscsList.execute()).thenThrow(new ApiErrorResponseException(builder));

        var response = pscService.getPSCsFromCHS(COMPANY_NUMBER);

        assertNotNull(response);
        assertNull(response.getActiveCount());
        assertNull(response.getCeasedCount());
        assertNull(response.getItemsPerPage());
        assertNull(response.getEtag());
        assertNull(response.getItems());
        assertNull(response.getTotalResults());
    }

    @Test
    void testGetPSCsFromOracle() throws ServiceException {
        var psc1 = new PersonOfSignificantControl();
        psc1.setServiceAddressLine1("something");
        var psc2 = new PersonOfSignificantControl();
        psc2.setServiceAddressLine1("else");

        PersonOfSignificantControl[] pSCs = { psc1, psc2 };

        when(oracleQueryClient.getPersonsOfSignificantControl(COMPANY_NUMBER)).thenReturn(Arrays.asList(pSCs));
        var response = pscService.getPSCsFromOracle(COMPANY_NUMBER);

        // TODO change asserts for proper values once mapping completed
        assertEquals(1, response.size());

    }

    @Test
    void testGetPSCsFromOracleThrowsServiceException() throws ServiceException {
        var se = new ServiceException("Message");
        when(oracleQueryClient.getPersonsOfSignificantControl(COMPANY_NUMBER)).thenThrow(se);

        var exception = assertThrows(ServiceException.class, () -> pscService.getPSCsFromOracle(COMPANY_NUMBER));
        assertEquals(se, exception);
    }
}
