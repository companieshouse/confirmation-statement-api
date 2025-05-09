package uk.gov.companieshouse.confirmationstatementapi.client;

import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpResponseException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClientException;

import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.company.PrivateCompanyResourceHandler;
import uk.gov.companieshouse.api.handler.company.request.*;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.common.Address;
import uk.gov.companieshouse.api.model.company.ActiveOfficerDetailsJson;
import uk.gov.companieshouse.api.model.company.PersonOfSignificantControl;
import uk.gov.companieshouse.api.model.company.RegisteredEmailAddressJson;
import uk.gov.companieshouse.api.model.company.StatementOfCapitalJson;
import uk.gov.companieshouse.api.model.payment.ConfirmationStatementPaymentJson;
import uk.gov.companieshouse.api.model.shareholder.ShareholderJson;
import uk.gov.companieshouse.confirmationstatementapi.exception.RegisteredEmailNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.exception.StatementOfCapitalNotFoundException;
import uk.gov.companieshouse.api.model.company.RegisterLocationJson;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;


import java.util.List;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:application.properties")
@SpringBootTest
class OracleQueryClientTest {

    private static final String COMPANY_NUMBER = "12345678";
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
    private ApiResponse<ShareholderJson[]> apiPrivateCompanyShareholdersGetResponse;

    @Mock
    private ApiResponse<StatementOfCapitalJson> apiPrivateCompanyStatementOfCapitalGetResponse;

    @Mock
    private ApiResponse<ActiveOfficerDetailsJson> apiPrivateActiveDirectorGetResponse;

    @Mock
    private ApiResponse<ActiveOfficerDetailsJson[]> apiPrivateActiveOfficersGetResponse;

    @Mock
    private ApiResponse<ConfirmationStatementPaymentJson> apiConfirmationStatementPaymentJsonApiResponse;

    @Mock
    private ApiResponse<PersonOfSignificantControl[]> apiPersonOfSignificantControlApiResponse;

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
    private PrivateCompanyShareHoldersGet privateCompanyShareHoldersGet;

    @Mock
    private PrivateCompanyStatementOfCapitalDataGet privateCompanyStatementOfCapitalDataGet;

    @Mock
    private PrivateCompanyActiveDirectorGet privateActiveDirectorGet;

    @Mock
    private PrivateCompanyActiveOfficersGet privateActiveOfficersGet;

    @Mock
    private PrivateCompanyPersonsOfSignificantControlGet privateCompanyPersonsOfSignificantControlGet;

    @Mock
    private PrivateCompanyRegisterLocationsGet privateRegisterLocationsGet;

    @Value("${api.path.company.details}") String apiPathCompanyDetails;
    @Value("${api.path.company.traded.status}") String apiPathCompanyTradedStatus;
    @Value("${api.path.company.shareholders.count}") String apiPathCompanyShareholdersCount;
    @Value("${api.path.company.statement.of.capital}") String apiPathCompanyStatementOfCapital;
    @Value("${api.path.company.director.active}") String apiPathCompanyDirectorActive;
    @Value("${api.path.company.officers.active}") String apiPathCompanyOfficersActive;
    @Value("${api.path.company.register.locations}") String apiPathCompanyRegisterLocations;
    @Value("${api.path.share.holders}") String apiPathShareHolders;
    @Value("${api.path.company.corporate.body.appointments.psc}") String apiPathCompanyCorporateBodyAppointmentsPsc;
    @Value("${api.path.company.confirmation.statement.paid}") String apiPathCompanyConfirmationStatementPaid;
    @Value("${api.path.registered.email.address}") String apiPathRegisteredEmailAddress;

    private OracleQueryClient oracleQueryClient;


    @BeforeEach
    void setup() {
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
        lenient().when(privateCompanyResourceHandler.getPersonsOfSignificantControl(Mockito.anyString()))
                .thenReturn(privateCompanyPersonsOfSignificantControlGet);
        lenient().when(privateCompanyResourceHandler.getRegisterLocations(Mockito.anyString())).thenReturn(privateRegisterLocationsGet);
        lenient().when(privateCompanyResourceHandler.getCompanyShareHolders(Mockito.anyString()))
                .thenReturn(privateCompanyShareHoldersGet);

        oracleQueryClient= new OracleQueryClient(apiClientService,
                apiPathCompanyDetails,
                apiPathCompanyTradedStatus,
                apiPathCompanyShareholdersCount,
                apiPathCompanyStatementOfCapital,
                apiPathCompanyDirectorActive,
                apiPathCompanyOfficersActive,
                apiPathCompanyRegisterLocations,
                apiPathShareHolders,
                apiPathCompanyCorporateBodyAppointmentsPsc,
                apiPathCompanyConfirmationStatementPaid,
                apiPathRegisteredEmailAddress
        );
    }

    static Stream<Object[]> provideConstructorParameters() {
        return Stream.of(
                new Object[]{null, "details", "tradedStatus", "shareholdersCount", "statementOfCapital", "directorActive", "officersActive", "registerLocations", "shareHolders", "corporateBodyAppointmentsPsc", "confirmationStatementPaid", "registeredEmailAddress"},
                new Object[]{new ApiClientService(), null, "tradedStatus", "shareholdersCount", "statementOfCapital", "directorActive", "officersActive", "registerLocations", "shareHolders", "corporateBodyAppointmentsPsc", "confirmationStatementPaid", "registeredEmailAddress"},
                new Object[]{new ApiClientService(), "details", null, "shareholdersCount", "statementOfCapital", "directorActive", "officersActive", "registerLocations", "shareHolders", "corporateBodyAppointmentsPsc", "confirmationStatementPaid", "registeredEmailAddress"},
                new Object[]{new ApiClientService(), "details", "tradedStatus", null, "statementOfCapital", "directorActive", "officersActive", "registerLocations", "shareHolders", "corporateBodyAppointmentsPsc", "confirmationStatementPaid", "registeredEmailAddress"},
                new Object[]{new ApiClientService(), "details", "tradedStatus", "shareholdersCount", null, "directorActive", "officersActive", "registerLocations", "shareHolders", "corporateBodyAppointmentsPsc", "confirmationStatementPaid", "registeredEmailAddress"},
                new Object[]{new ApiClientService(), "details", "tradedStatus", "shareholdersCount", "statementOfCapital", null, "officersActive", "registerLocations", "shareHolders", "corporateBodyAppointmentsPsc", "confirmationStatementPaid", "registeredEmailAddress"},
                new Object[]{new ApiClientService(), "details", "tradedStatus", "shareholdersCount", "statementOfCapital", "directorActive", null, "registerLocations", "shareHolders", "corporateBodyAppointmentsPsc", "confirmationStatementPaid", "registeredEmailAddress"},
                new Object[]{new ApiClientService(), "details", "tradedStatus", "shareholdersCount", "statementOfCapital", "directorActive", "officersActive", null, "shareHolders", "corporateBodyAppointmentsPsc", "confirmationStatementPaid", "registeredEmailAddress"},
                new Object[]{new ApiClientService(), "details", "tradedStatus", "shareholdersCount", "statementOfCapital", "directorActive", "officersActive", "registerLocations", null, "corporateBodyAppointmentsPsc", "confirmationStatementPaid", "registeredEmailAddress"},
                new Object[]{new ApiClientService(), "details", "tradedStatus", "shareholdersCount", "statementOfCapital", "directorActive", "officersActive", "registerLocations", "shareHolders", null, "confirmationStatementPaid", "registeredEmailAddress"},
                new Object[]{new ApiClientService(), "details", "tradedStatus", "shareholdersCount", "statementOfCapital", "directorActive", "officersActive", "registerLocations", "shareHolders", "corporateBodyAppointmentsPsc", null, "registeredEmailAddress"},
                new Object[]{new ApiClientService(), "details", "tradedStatus", "shareholdersCount", "statementOfCapital", "directorActive", "officersActive", "registerLocations", "shareHolders", "corporateBodyAppointmentsPsc", "confirmationStatementPaid", null}
        );
    }

    @ParameterizedTest
    @MethodSource("provideConstructorParameters")
    void testConstructorThrowsExceptionWhenParameterIsNull(
            ApiClientService apiClientService,
            String apiPathCompanyDetails,
            String apiPathCompanyTradedStatus,
            String apiPathCompanyShareholdersCount,
            String apiPathCompanyStatementOfCapital,
            String apiPathCompanyDirectorActive,
            String apiPathCompanyOfficersActive,
            String apiPathCompanyRegisterLocations,
            String apiPathShareHolders,
            String apiPathCompanyCorporateBodyAppointmentsPsc,
            String apiPathCompanyConfirmationStatementPaid,
            String apiPathRegisteredEmailAddress
    ) {
        assertThrows(NullPointerException.class, () -> new OracleQueryClient(
                apiClientService,
                apiPathCompanyDetails,
                apiPathCompanyTradedStatus,
                apiPathCompanyShareholdersCount,
                apiPathCompanyStatementOfCapital,
                apiPathCompanyDirectorActive,
                apiPathCompanyOfficersActive,
                apiPathCompanyRegisterLocations,
                apiPathShareHolders,
                apiPathCompanyCorporateBodyAppointmentsPsc,
                apiPathCompanyConfirmationStatementPaid,
                apiPathRegisteredEmailAddress
        ));
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
    void testGetActiveDirectorDetailsOkStatusResponse() throws ServiceException, ApiErrorResponseException, URIValidationException {
        ActiveOfficerDetailsJson expectedActiveOfficerDetails = new ActiveOfficerDetailsJson();

        when(privateActiveDirectorGet.execute()).thenReturn(apiPrivateActiveDirectorGetResponse);
        when(apiPrivateActiveDirectorGetResponse.getData()).thenReturn(expectedActiveOfficerDetails);

        var result = oracleQueryClient.getActiveDirectorDetails(COMPANY_NUMBER);
        assertNotNull(result);
    }

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
        ActiveOfficerDetailsJson[] expectedActiveOfficerDetails = new ActiveOfficerDetailsJson[0];

        // WHEN
        when(privateActiveOfficersGet.execute()).thenReturn(apiPrivateActiveOfficersGetResponse);
        when(apiPrivateActiveOfficersGetResponse.getData()).thenReturn(expectedActiveOfficerDetails);

        //THEN
        var result = oracleQueryClient.getActiveOfficersDetails(COMPANY_NUMBER);
        assertNotNull(result);
    }

    @Test
    void testGetListActiveOfficersDetailsNotOkStatusResponse() throws ApiErrorResponseException, URIValidationException {
        // GIVEN
        ApiErrorResponseException serviceUnavailableException = ApiErrorResponseException.fromHttpResponseException(
                new HttpResponseException.Builder(503, "Service Unavailable", new HttpHeaders()).build());

        // WHEN
        when(privateActiveOfficersGet.execute()).thenThrow(serviceUnavailableException);

        // THEN
        assertThrows(ServiceException.class, () -> oracleQueryClient.getActiveOfficersDetails(COMPANY_NUMBER));
    }

    @Test
    void testGetPersonsOfSignificantControlResponse() throws ServiceException, ApiErrorResponseException, URIValidationException {
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

        when(privateCompanyPersonsOfSignificantControlGet.execute()).thenReturn(apiPersonOfSignificantControlApiResponse);
        when(apiPersonOfSignificantControlApiResponse.getData()).thenReturn(pscArray);

        var result = oracleQueryClient.getPersonsOfSignificantControl(companyNumber);
        assertEquals(2, result.size());
    }

    @Test
    void testGetPersonsOfSignificantControlNotOkStatusResponse() throws ApiErrorResponseException, URIValidationException {
        //WHEN
        lenient().when(privateCompanyPersonsOfSignificantControlGet.execute()).thenThrow(URIValidationException.class);
        // THEN
        assertThrowsExactly(ServiceException.class, () -> oracleQueryClient.getPersonsOfSignificantControl(COMPANY_NUMBER),
                String.format("Invalid URI: %s", ""));
    }

    @Test
    void testGetShareholdersResponse() throws ServiceException, ApiErrorResponseException, URIValidationException {

        // GIVEN
        int expectedCount = 2;
        ShareholderJson[] shareholderArray = {new ShareholderJson(), new ShareholderJson()};

        // WHEN
        when(privateCompanyShareHoldersGet.execute()).thenReturn(apiPrivateCompanyShareholdersGetResponse);
        when(apiPrivateCompanyShareholdersGetResponse.getData()).thenReturn(shareholderArray);

        // THEN
        List<ShareholderJson> result = oracleQueryClient.getShareholders(COMPANY_NUMBER);
        assertEquals(expectedCount, result.size());
    }

    @Test
    void testGetShareholdersInvalidURIThrowsServiceException() throws ApiErrorResponseException, URIValidationException {
        // GIVEN

        // WHEN
        when(privateCompanyShareHoldersGet.execute()).thenThrow(URIValidationException.class);

        // THEN
        assertThrows(ServiceException.class, () -> oracleQueryClient.getShareholders(COMPANY_NUMBER));
    }

    @Test
    void testGetShareholdersApiErrorResponseThrowsServiceException()  throws ApiErrorResponseException, URIValidationException {
        // GIVEN

        // WHEN
        when(privateCompanyShareHoldersGet.execute()).thenThrow(ApiErrorResponseException.class);

        // THEN
        assertThrows(ServiceException.class, () -> oracleQueryClient.getShareholders(COMPANY_NUMBER));
    }

    @Test
    void testGetRegisterLocationsResponse() throws ServiceException, ApiErrorResponseException, URIValidationException {
        var regLoc1 = new RegisterLocationJson();
        regLoc1.setRegisterTypeDesc("desc1");
        regLoc1.setSailAddress(new Address());

        var regLoc2 = new RegisterLocationJson();
        regLoc2.setRegisterTypeDesc("desc2");
        regLoc2.setSailAddress(new Address());

        RegisterLocationJson[] registerLocationsArray = {regLoc1, regLoc2};
        ApiResponse<RegisterLocationJson[]> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), new HttpHeaders(), registerLocationsArray);

        var companyNumber = "123213";

        when(privateRegisterLocationsGet.execute()).thenReturn(apiResponse);

        var result = oracleQueryClient.getRegisterLocations(companyNumber);
        assertEquals(2, result.size());
    }

    @Test
    void testGetRegisterLocationsNotOkStatusResponse() throws ApiErrorResponseException, URIValidationException {
        // GIVEN
        ApiErrorResponseException serviceUnavailableException = ApiErrorResponseException.fromHttpResponseException(
                new HttpResponseException.Builder(503, "Service Unavailable", new HttpHeaders()).build());

        // WHEN
        when(privateRegisterLocationsGet.execute()).thenThrow(serviceUnavailableException);

        // THEN
        assertThrows(ServiceException.class, () -> oracleQueryClient.getRegisterLocations(COMPANY_NUMBER));
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
    void testIsConfirmationStatementIsNotPaid() throws ServiceException, ApiErrorResponseException, URIValidationException {
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
    void testIsConfirmationStatementPaidInvalidURIThrowsServiceException() throws ApiErrorResponseException, URIValidationException {
        // GIVEN

        // WHEN
        lenient().when(privateCompanyConfirmationStatementPaymentGet.execute()).thenThrow(URIValidationException.class);

        // THEN
        assertThrows(ServiceException.class, () -> oracleQueryClient.isConfirmationStatementPaid(COMPANY_NUMBER, DUE_DATE));
    }

    @Test
    void testIsConfirmationStatementPaidApiErrorResponseThrowsServiceException() throws ApiErrorResponseException, URIValidationException {
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
        ApiErrorResponseException notFound = ApiErrorResponseException.fromHttpResponseException(
                new HttpResponseException.Builder(404, "NOT FOUND", new HttpHeaders()).build());

        // WHEN
        when(privateCompanyStatementOfCapitalDataGet.execute()).thenThrow(notFound);

        // THEN
        assertThrows(StatementOfCapitalNotFoundException.class, () -> oracleQueryClient.getStatementOfCapitalData(COMPANY_NUMBER));
    }

    @Test
    void testGetStatementOfCapitalDataReturnsEmptyObjectIfDataIsNull() throws Exception {
        // GIVEN
        when(apiClientService.getInternalApiClient()).thenReturn(apiClient);
        when(apiClient.privateCompanyResourceHandler()).thenReturn(privateCompanyResourceHandler);
        lenient().when(privateCompanyResourceHandler.getStatementOfCapitalData(String.format("/company/%s/statement-of-capital", COMPANY_NUMBER)))
                .thenReturn(privateCompanyStatementOfCapitalDataGet);
        when(privateCompanyStatementOfCapitalDataGet.execute()).thenReturn(apiPrivateCompanyStatementOfCapitalGetResponse);
        when(apiPrivateCompanyStatementOfCapitalGetResponse.getData()).thenReturn(null);

        // WHEN
        StatementOfCapitalJson result = oracleQueryClient.getStatementOfCapitalData(COMPANY_NUMBER);

        // THEN
        assertInstanceOf(StatementOfCapitalJson.class, result);
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
        ApiErrorResponseException badRequestException = ApiErrorResponseException.fromHttpResponseException(
                new HttpResponseException.Builder(400, "BAD REQUEST", new HttpHeaders()).build());

        // WHEN
        when(privateCompanyEmailGet.execute()).thenThrow(badRequestException);

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
        ApiErrorResponseException notFoundException = ApiErrorResponseException.fromHttpResponseException(
                new HttpResponseException.Builder(404, "NOT FOUND", new HttpHeaders()).build());

        // WHEN
        when(privateCompanyEmailGet.execute()).thenThrow(notFoundException);

        // THEN
        assertThrows(RegisteredEmailNotFoundException.class, () -> oracleQueryClient.getRegisteredEmailAddress(COMPANY_NUMBER));
    }
}
