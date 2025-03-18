package uk.gov.companieshouse.confirmationstatementapi.client;

import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpResponseException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.company.PrivateCompanyResourceHandler;
import uk.gov.companieshouse.api.handler.company.request.*;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.common.Address;
import uk.gov.companieshouse.api.model.company.ActiveOfficerDetails;
import uk.gov.companieshouse.api.model.company.RegisteredEmailAddressJson;
import uk.gov.companieshouse.api.model.company.StatementOfCapitalJson;
import uk.gov.companieshouse.api.model.payment.ConfirmationStatementPaymentJson;
import uk.gov.companieshouse.api.model.shareholder.ShareholderJson;
import uk.gov.companieshouse.confirmationstatementapi.exception.ActiveOfficerNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.exception.RegisteredEmailNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.exception.StatementOfCapitalNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.model.PersonOfSignificantControl;
import uk.gov.companieshouse.confirmationstatementapi.model.json.registerlocation.RegisterLocationJson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OracleQueryClientTest {

    private static final String ACTIVE_DIRECTOR_PATH = "/director/active";
    private static final String ACTIVE_OFFICERS_PATH = "/officers/active";
    private static final String COMPANY_NUMBER = "12345678";
    private static final String DUMMY_URL = "http://test";
    private static final String PSC_PATH = "/corporate-body-appointments/persons-of-significant-control";
    private static final String SHAREHOLDER_PATH = "/shareholders";
    private static final String REGISTER_LOCATIONS_PATH = "/register/location";
    private static final String COMPANY_EMAIL = "info@acme.com";
    public static final String DUE_DATE = "2022-01-01";

    @Mock
    private ApiClientService apiClientService;

    @Mock
    private InternalApiClient apiClient;

    @Mock
    private ApiResponse<RegisteredEmailAddressJson> apiPrivateCompanyEmailGetResponse;

    @Mock
    private ApiResponse<Long> apiPrivateCompanyTradedStatusGetResponse;

    @Mock
    private ApiResponse<Integer> apiPrivateCompanyShareholdersCountGetResponse;

    @Mock
    private ApiResponse<StatementOfCapitalJson> apiPrivateCompanyStatementOfCapitalGetResponse;

    @Mock
    private ApiResponse<ActiveOfficerDetails> apiPrivateActiveDirectorGetResponse;

    @Mock
    private ApiResponse<ActiveOfficerDetails[]> apiPrivateActiveOfficersGetResponse;

    @Mock
    private ApiResponse<ConfirmationStatementPaymentJson> apiConfirmationStatementPaymentJsonApiResponse;

    @Mock
    private PrivateCompanyResourceHandler privateCompanyResourceHandler;

    @Mock
    private PrivateCompanyEmailGet privateCompanyEmailGet;

    @Mock
    private PrivateCompanyTradedStatusGet privateCompanyTradedStatusGet;

    @Mock
    private PrivateCompanyConfirmationStatementPaymentGet privateCompanyConfirmationStatementPaymentGet;

    @Mock
    private PrivateCompanyShareHoldersCountGet privateCompanyShareHoldersCountGet;

    @Mock
    private PrivateCompanyStatementOfCapitalDataGet privateCompanyStatementOfCapitalDataGet;

    @Mock
    private PrivateActiveDirectorGet privateActiveDirectorGet;

    @Mock
    private PrivateActiveOfficersGet privateActiveOfficersGet;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private OracleQueryClient oracleQueryClient;


    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(oracleQueryClient, "oracleQueryApiUrl", DUMMY_URL);
        lenient().when(apiClientService.getInternalApiClient()).thenReturn(apiClient);
        lenient().when(apiClient.privateCompanyResourceHandler()).thenReturn(privateCompanyResourceHandler);
        lenient().when(privateCompanyResourceHandler.getCompanyTradedStatus(Mockito.anyString())).thenReturn(privateCompanyTradedStatusGet);
        lenient().when(privateCompanyResourceHandler.getCompanyShareHoldersCount(Mockito.anyString())).thenReturn(privateCompanyShareHoldersCountGet);
        lenient().when(privateCompanyResourceHandler.getStatementOfCapitalData(Mockito.anyString())).thenReturn(privateCompanyStatementOfCapitalDataGet);
        lenient().when(privateCompanyResourceHandler.getCompanyRegisteredEmailAddress(Mockito.anyString())).thenReturn(privateCompanyEmailGet);
        lenient().when(privateCompanyResourceHandler.getConfirmationStatementPayment(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(privateCompanyConfirmationStatementPaymentGet);
        lenient().when(privateCompanyResourceHandler.getActiveDirector(Mockito.anyString())).thenReturn(privateActiveDirectorGet);
        lenient().when(privateCompanyResourceHandler.getActiveOfficers(Mockito.anyString())).thenReturn(privateActiveOfficersGet);
    }

    @Test
    void testGetTradedStatus() throws ApiErrorResponseException, URIValidationException, ServiceException {
        // GIVEN
        long expectedCount = 1;

        // WHEN
        when(privateCompanyTradedStatusGet.execute()).thenReturn(apiPrivateCompanyTradedStatusGetResponse);
        when(apiPrivateCompanyTradedStatusGetResponse.getData()).thenReturn(expectedCount);

        // THEN
        long result = oracleQueryClient.getCompanyTradedStatus(COMPANY_NUMBER);
        assertEquals(expectedCount, result);
    }

    @Test
    void testGetTradedStatusInvalidURIThrowsServiceException() throws ApiErrorResponseException, URIValidationException {
        // GIVEN

        // WHEN
        when(privateCompanyTradedStatusGet.execute()).thenThrow(URIValidationException.class);

        // THEN
        assertThrows(ServiceException.class, () -> oracleQueryClient.getCompanyTradedStatus(COMPANY_NUMBER));
    }

    @Test
    void testGetCompanyTradedStatusApiErrorResponseThrowsServiceException() throws ApiErrorResponseException, URIValidationException {
        // GIVEN

        // WHEN
        when(privateCompanyTradedStatusGet.execute()).thenThrow(ApiErrorResponseException.class);

        // THEN
        assertThrows(ServiceException.class, () -> oracleQueryClient.getCompanyTradedStatus(COMPANY_NUMBER));
    }

    @Test
    void testGetActiveDirectorDetailsOkStatusResponse() throws ServiceException, ActiveOfficerNotFoundException, ApiErrorResponseException, URIValidationException {
        ActiveOfficerDetails expectedActiveOfficerDetails = new ActiveOfficerDetails();

        when(privateActiveDirectorGet.execute()).thenReturn(apiPrivateActiveDirectorGetResponse);
        when(apiPrivateActiveDirectorGetResponse.getData()).thenReturn(expectedActiveOfficerDetails);

        var result = oracleQueryClient.getActiveDirectorDetails(COMPANY_NUMBER);
        assertNotNull(result);
    }

    // Todo - test this
    @Test
    void testGetActiveDirectorDetailsStatus400Response() throws ApiErrorResponseException, URIValidationException {
        // GIVEN
        ApiErrorResponseException badRequestException = ApiErrorResponseException.fromHttpResponseException(
                new HttpResponseException.Builder(400, "Bad Request", new HttpHeaders()).build());

        // WHEN
        when(privateActiveDirectorGet.execute()).thenThrow(badRequestException);

        // THEN
        assertThrows(ServiceException.class, () -> oracleQueryClient.getActiveDirectorDetails(COMPANY_NUMBER));
    }

    @Test
    void testGetActiveDirectorDetailsNotOkStatusResponse() throws ApiErrorResponseException, URIValidationException {
        // GIVEN
        ApiErrorResponseException serviceUnavailableException = ApiErrorResponseException.fromHttpResponseException(
                new HttpResponseException.Builder(503, "Service Unavailable", new HttpHeaders()).build());

        // WHEN
        when(privateActiveDirectorGet.execute()).thenThrow(serviceUnavailableException);

        // THEN
        assertThrows(ServiceException.class, () -> oracleQueryClient.getActiveDirectorDetails(COMPANY_NUMBER));
    }

    @Test
    void testGetListActiveOfficersDetailsOkStatusResponse() throws ServiceException, ApiErrorResponseException, URIValidationException {
        // GIVEN
        ActiveOfficerDetails[] expectedActiveOfficerDetails = new ActiveOfficerDetails[0];

        // WHEN
        when(privateActiveOfficersGet.execute()).thenReturn(apiPrivateActiveOfficersGetResponse);
        when(apiPrivateActiveOfficersGetResponse.getData()).thenReturn(expectedActiveOfficerDetails);

        //THEN
        var result = oracleQueryClient.getActiveOfficersDetails(COMPANY_NUMBER);
        assertNotNull(result);
    }

    @Test
    void testGetListActiveOfficersDetailsNotOkStatusResponse() throws ApiErrorResponseException, URIValidationException, ServiceException {
        // GIVEN
        ApiErrorResponseException serviceUnavailableException = ApiErrorResponseException.fromHttpResponseException(
                new HttpResponseException.Builder(503, "Service Unavailable", new HttpHeaders()).build());

        // WHEN
        when(privateActiveOfficersGet.execute()).thenThrow(serviceUnavailableException);

        // THEN
        assertThrows(ServiceException.class, () -> oracleQueryClient.getActiveOfficersDetails(COMPANY_NUMBER));
    }

    @Test
    void testGetPersonsOfSignificantControlResponse() throws ServiceException {
        var psc1 = new PersonOfSignificantControl();
        psc1.setAppointmentTypeId("1");
        Address address = new Address();
        address.setAddressLine1("1 some street");
        address.setPostalCode("post code");
        psc1.setServiceAddress(address);


        var psc2 = new PersonOfSignificantControl();
        psc2.setAppointmentTypeId("1");
        psc2.setServiceAddress(address);

        PersonOfSignificantControl[] pscArray = {psc1, psc2};

        var companyNumber = "123213";

        when(restTemplate.getForEntity(DUMMY_URL + "/company/" + companyNumber + PSC_PATH, PersonOfSignificantControl[].class))
                .thenReturn(new ResponseEntity<>(pscArray, HttpStatus.OK));

        var result = oracleQueryClient.getPersonsOfSignificantControl(companyNumber);
        assertEquals(2, result.size());
    }

    @Test
    void testGetPersonsOfSignificantControlNotOkStatusResponse() {
        var companyNumber = "123213";
        when(restTemplate.getForEntity(DUMMY_URL + "/company/" + companyNumber + PSC_PATH, PersonOfSignificantControl[].class))
                .thenReturn(new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE));

        var serviceException = assertThrows(ServiceException.class, () -> oracleQueryClient.getPersonsOfSignificantControl(companyNumber));
        assertTrue(serviceException.getMessage().contains(companyNumber));
        assertTrue(serviceException.getMessage().contains(HttpStatus.SERVICE_UNAVAILABLE.toString()));
    }

    @Test
    void testGetShareholderResponse() throws ServiceException {
        var shareholder1 = new ShareholderJson();
        shareholder1.setForeName1("John");
        shareholder1.setForeName2("K");
        shareholder1.setSurname("Lewis");


        var shareholder2 = new ShareholderJson();
        shareholder2.setForeName1("James");
        shareholder2.setSurname("Bond");

        ShareholderJson[] shareholderArray = {shareholder1, shareholder2};

        var companyNumber = "123213";

        when(restTemplate.getForEntity(DUMMY_URL + "/company/" + companyNumber + SHAREHOLDER_PATH, ShareholderJson[].class))
                .thenReturn(new ResponseEntity<>(shareholderArray, HttpStatus.OK));

        var result = oracleQueryClient.getShareholders(companyNumber);
        assertEquals(2, result.size());
    }

    @Test
    void testGetShareholderNotOkStatusResponse() {
        var companyNumber = "123213";
        when(restTemplate.getForEntity(DUMMY_URL + "/company/" + companyNumber + SHAREHOLDER_PATH, ShareholderJson[].class))
                .thenReturn(new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE));

        var serviceException = assertThrows(ServiceException.class, () -> oracleQueryClient.getShareholders(companyNumber));
        assertTrue(serviceException.getMessage().contains(companyNumber));
        assertTrue(serviceException.getMessage().contains(HttpStatus.SERVICE_UNAVAILABLE.toString()));
    }

    @Test
    void testGetRegisterLocationsResponse() throws ServiceException {
        var regLoc1 = new RegisterLocationJson();
        regLoc1.setRegisterTypeDesc("desc1");
        regLoc1.setSailAddress(new Address());


        var regLoc2 = new RegisterLocationJson();
        regLoc2.setRegisterTypeDesc("desc2");
        regLoc2.setSailAddress(new Address());

        RegisterLocationJson[] registerLocationsArray = {regLoc1, regLoc2};

        var companyNumber = "123213";

        when(restTemplate.getForEntity(DUMMY_URL + "/company/" + companyNumber + REGISTER_LOCATIONS_PATH, RegisterLocationJson[].class))
                .thenReturn(new ResponseEntity<>(registerLocationsArray, HttpStatus.OK));

        var result = oracleQueryClient.getRegisterLocations(companyNumber);
        assertEquals(2, result.size());
    }

    @Test
    void testGetRegisterLocationsNotOkStatusResponse() {
        var companyNumber = "123213";
        when(restTemplate.getForEntity(DUMMY_URL + "/company/" + companyNumber + REGISTER_LOCATIONS_PATH, RegisterLocationJson[].class))
                .thenReturn(new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE));

        var serviceException = assertThrows(ServiceException.class, () -> oracleQueryClient.getRegisterLocations(companyNumber));
        assertTrue(serviceException.getMessage().contains(companyNumber));
        assertTrue(serviceException.getMessage().contains(HttpStatus.SERVICE_UNAVAILABLE.toString()));
    }

    @Test
    void testIsConfirmationStatementIsPaid() throws ServiceException, ApiErrorResponseException, URIValidationException {
        // GIVEN
        ConfirmationStatementPaymentJson confirmationStatementPaymentJson = new ConfirmationStatementPaymentJson();
        confirmationStatementPaymentJson.setPaid(true);

        // WHEN
        when(privateCompanyConfirmationStatementPaymentGet.execute()).thenReturn(apiConfirmationStatementPaymentJsonApiResponse);
        when(apiConfirmationStatementPaymentJsonApiResponse.getData()).thenReturn(confirmationStatementPaymentJson);

        // THEN
        boolean result = oracleQueryClient.isConfirmationStatementPaid(COMPANY_NUMBER, DUE_DATE);
        assertTrue(result);
    }

    @Test
    void testConfirmationStatementIsNotPaid() throws ServiceException, ApiErrorResponseException, URIValidationException {
        // GIVEN
        ConfirmationStatementPaymentJson confirmationStatementPaymentJson = new ConfirmationStatementPaymentJson();
        confirmationStatementPaymentJson.setPaid(false);

        // WHEN
        when(privateCompanyConfirmationStatementPaymentGet.execute()).thenReturn(apiConfirmationStatementPaymentJsonApiResponse);
        when(apiConfirmationStatementPaymentJsonApiResponse.getData()).thenReturn(confirmationStatementPaymentJson);

        // THEN
        boolean result = oracleQueryClient.isConfirmationStatementPaid(COMPANY_NUMBER, DUE_DATE);
        assertFalse(result);
    }

    @Test
    void testConfirmationStatementPaidInvalidURIThrowsServiceException() throws ApiErrorResponseException, URIValidationException {
        // GIVEN

        // WHEN
        lenient().when(privateCompanyConfirmationStatementPaymentGet.execute()).thenThrow(URIValidationException.class);

        // THEN
        assertThrows(ServiceException.class, () -> oracleQueryClient.isConfirmationStatementPaid(COMPANY_NUMBER, DUE_DATE));
    }

    @Test
    void testGetConfirmationStatementPaidUnexpectedResponse() throws ApiErrorResponseException, URIValidationException {
        // GIVEN

        // WHEN
        when(privateCompanyConfirmationStatementPaymentGet.execute()).thenThrow(ApiErrorResponseException.class);

        // THEN
        assertThrows(ServiceException.class, () -> oracleQueryClient.isConfirmationStatementPaid(COMPANY_NUMBER, DUE_DATE));
    }

    @Test
    void testGetStatementOfCapitalData() throws ServiceException, ApiErrorResponseException, URIValidationException, StatementOfCapitalNotFoundException {
        // GIVEN
        StatementOfCapitalJson expectedJson = new StatementOfCapitalJson();

        // WHEN
        when(privateCompanyStatementOfCapitalDataGet.execute()).thenReturn(apiPrivateCompanyStatementOfCapitalGetResponse);
        when(apiPrivateCompanyStatementOfCapitalGetResponse.getData()).thenReturn(expectedJson);

        // THEN
        StatementOfCapitalJson result = oracleQueryClient.getStatementOfCapitalData(COMPANY_NUMBER);
        assertEquals(expectedJson, result);
    }

    @Test
    void testGetStatementOfCapitalDataServiceUnavailable() {
        // GIVEN

        // WHEN
        lenient().when(apiPrivateCompanyStatementOfCapitalGetResponse.getData()).thenThrow(RestClientException.class);

        // THEN
        assertThrows(ServiceException.class, () -> oracleQueryClient.getStatementOfCapitalData(COMPANY_NUMBER));
    }

    @Test
    void testGetStatementOfCapitalDataInvalidURIThrowsServiceException() throws ApiErrorResponseException, URIValidationException {
        // GIVEN

        // WHEN
        lenient().when(privateCompanyStatementOfCapitalDataGet.execute()).thenThrow(URIValidationException.class);

        // THEN
        assertThrows(ServiceException.class, () -> oracleQueryClient.getStatementOfCapitalData(COMPANY_NUMBER));
    }

    @Test
    void testGetStatementOfCapitalDataUnexpectedResponse() throws ApiErrorResponseException, URIValidationException {
        // GIVEN

        // WHEN
        when(privateCompanyStatementOfCapitalDataGet.execute()).thenThrow(ApiErrorResponseException.class);

        // THEN
        assertThrows(ServiceException.class, () -> oracleQueryClient.getStatementOfCapitalData(COMPANY_NUMBER));
    }

    @Test
    void testGetStatementOfCapitalDataResponseNotFound() throws ApiErrorResponseException, URIValidationException {
        // GIVEN
        ApiErrorResponseException NOT_FOUND_EXCEPTION = ApiErrorResponseException.fromHttpResponseException(
                new HttpResponseException.Builder(404, "ERROR", new HttpHeaders()).build());

        // WHEN
        when(privateCompanyStatementOfCapitalDataGet.execute()).thenThrow(NOT_FOUND_EXCEPTION);

        // THEN
        assertThrows(StatementOfCapitalNotFoundException.class, () -> oracleQueryClient.getStatementOfCapitalData(COMPANY_NUMBER));
    }

    @Test
    void testGetShareholderCount() throws ApiErrorResponseException, URIValidationException, ServiceException {
        // GIVEN
        int expectedCount = 1;

        // WHEN
        when(privateCompanyShareHoldersCountGet.execute()).thenReturn(apiPrivateCompanyShareholdersCountGetResponse);
        when(apiPrivateCompanyShareholdersCountGetResponse.getData()).thenReturn(expectedCount);

        // THEN
        int result = oracleQueryClient.getShareholderCount(COMPANY_NUMBER);
        assertEquals(expectedCount, result);
    }

    @Test
    void testGetShareholderCountInvalidURIThrowsServiceException() throws ApiErrorResponseException, URIValidationException {
        // GIVEN

        // WHEN
        when(privateCompanyShareHoldersCountGet.execute()).thenThrow(URIValidationException.class);

        // THEN
        assertThrows(ServiceException.class, () -> oracleQueryClient.getShareholderCount(COMPANY_NUMBER));
    }

    @Test
    void testGetShareholderCountApiErrorResponseThrowsServiceException() throws ApiErrorResponseException, URIValidationException {
        // GIVEN

        // WHEN
        when(privateCompanyShareHoldersCountGet.execute()).thenThrow(ApiErrorResponseException.class);

        // THEN
        assertThrows(ServiceException.class, () -> oracleQueryClient.getShareholderCount(COMPANY_NUMBER));
    }

    @Test
    void testGetRegisteredEmailAddress() throws ServiceException, RegisteredEmailNotFoundException, ApiErrorResponseException, URIValidationException {
        // GIVEN
        var registeredEmailAddress = new RegisteredEmailAddressJson();
        registeredEmailAddress.setRegisteredEmailAddress("info@acme.com");

        // WHEN
        when(privateCompanyEmailGet.execute()).thenReturn(apiPrivateCompanyEmailGetResponse);
        when(apiPrivateCompanyEmailGetResponse.getData()).thenReturn(registeredEmailAddress);

        // THEN
        RegisteredEmailAddressJson response = oracleQueryClient.getRegisteredEmailAddress(COMPANY_NUMBER);
        assertEquals(COMPANY_EMAIL, response.getRegisteredEmailAddress());
    }

    @Test
    void testGetRegisteredEmailAddressServiceUnavailable() {
        // GIVEN

        // WHEN
        lenient().when(apiPrivateCompanyEmailGetResponse.getData()).thenThrow(RestClientException.class);

        // THEN
        assertThrows(ServiceException.class, () -> oracleQueryClient.getRegisteredEmailAddress(COMPANY_NUMBER));
    }

    @Test
    void testGetRegisteredEmailAddressUnexpectedResponse() throws RegisteredEmailNotFoundException, ApiErrorResponseException, URIValidationException {
        // GIVEN
        ApiErrorResponseException BAD_REQUEST_EXCEPTION = ApiErrorResponseException.fromHttpResponseException(
                new HttpResponseException.Builder(400, "ERROR", new HttpHeaders()).build());

        // WHEN
        when(privateCompanyEmailGet.execute()).thenThrow(BAD_REQUEST_EXCEPTION);

        // THEN
        try {
            oracleQueryClient.getRegisteredEmailAddress(COMPANY_NUMBER);
        } catch (ServiceException e) {
            assertEquals("Oracle query api returned with status = 400, companyNumber = 12345678", e.getMessage());
            return;
        }

        assert false;
    }

    @Test
    void testGetRegisteredEmailAddressEmailAddressResponseNotFound() throws ApiErrorResponseException, URIValidationException {
        // GIVEN
        ApiErrorResponseException NOT_FOUND_EXCEPTION = ApiErrorResponseException.fromHttpResponseException(
                new HttpResponseException.Builder(404, "ERROR", new HttpHeaders()).build());

        // WHEN
        when(privateCompanyEmailGet.execute()).thenThrow(NOT_FOUND_EXCEPTION);

        // THEN
        assertThrows(RegisteredEmailNotFoundException.class, () -> oracleQueryClient.getRegisteredEmailAddress(COMPANY_NUMBER));
    }
}
