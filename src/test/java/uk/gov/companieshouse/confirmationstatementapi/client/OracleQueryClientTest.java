package uk.gov.companieshouse.confirmationstatementapi.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import uk.gov.companieshouse.api.model.common.Address;
import uk.gov.companieshouse.confirmationstatementapi.exception.ActiveOfficerNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.exception.RegisteredEmailNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.exception.StatementOfCapitalNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.model.ActiveOfficerDetails;
import uk.gov.companieshouse.confirmationstatementapi.model.PersonOfSignificantControl;
import uk.gov.companieshouse.confirmationstatementapi.model.json.payment.ConfirmationStatementPaymentJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.registeredemailaddress.RegisteredEmailAddressJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.registerlocation.RegisterLocationJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.shareholder.ShareholderJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.statementofcapital.StatementOfCapitalJson;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OracleQueryClientTest {

    private static final String ACTIVE_DIRECTOR_PATH = "/director/active";
    private static final String ACTIVE_OFFICERS_PATH = "/officers/active";
    private static final String COMPANY_NUMBER = "12345678";
    private static final String DUMMY_URL = "http://test";
    private static final String PSC_PATH = "/corporate-body-appointments/persons-of-significant-control";
    private static final String SOC_PATH = "/statement-of-capital";
    private static final String SHAREHOLDER_PATH = "/shareholders";
    private static final String REGISTER_LOCATIONS_PATH = "/register/location";
    private static final String PAYMENT_PATH = "/company/" + COMPANY_NUMBER + "/confirmation-statement/paid?payment_period_made_up_to_date=2022-01-01";


    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private OracleQueryClient oracleQueryClient;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(oracleQueryClient, "oracleQueryApiUrl", DUMMY_URL);
    }

    @Test
    void testGetCompanyTradedStatus() {
        var tradedStatus = 0L;
        when(restTemplate.getForEntity(DUMMY_URL + "/company/" + COMPANY_NUMBER + "/traded-status", Long.class))
                .thenReturn(new ResponseEntity<>(tradedStatus, HttpStatus.OK));

        Long result = oracleQueryClient.getCompanyTradedStatus(COMPANY_NUMBER);

        assertEquals(tradedStatus, result);
    }

    @Test
    void testGetCompanyShareholdersCount() {
        int expectedCount = 1;
        when(restTemplate.getForEntity(DUMMY_URL + "/company/" + COMPANY_NUMBER + "/shareholders/count", Integer.class))
                .thenReturn(new ResponseEntity<>(expectedCount, HttpStatus.OK));

        int result = oracleQueryClient.getShareholderCount(COMPANY_NUMBER);
        assertEquals(expectedCount, result);
    }

    @Test
    void testGetStatementOfCapitalData() throws ServiceException, StatementOfCapitalNotFoundException {
        when(restTemplate.getForEntity(DUMMY_URL + "/company/" + COMPANY_NUMBER + SOC_PATH, StatementOfCapitalJson.class))
                .thenReturn(new ResponseEntity<>(new StatementOfCapitalJson(), HttpStatus.OK));
        StatementOfCapitalJson result = oracleQueryClient.getStatementOfCapitalData(COMPANY_NUMBER);
        assertNotNull(result);
    }

    @Test
    void testGetStatementOfCapitalDataNullResponse() {
        when(restTemplate.getForEntity(DUMMY_URL + "/company/" + COMPANY_NUMBER + SOC_PATH, StatementOfCapitalJson.class))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));
        assertThrows(StatementOfCapitalNotFoundException.class, () -> oracleQueryClient.getStatementOfCapitalData(COMPANY_NUMBER));
    }

    @Test
    void testGetStatementOfCapitalDataNotOkStatusResponse() {
        when(restTemplate.getForEntity(DUMMY_URL + "/company/" + COMPANY_NUMBER + SOC_PATH, StatementOfCapitalJson.class))
                .thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThrows(ServiceException.class, () -> oracleQueryClient.getStatementOfCapitalData(COMPANY_NUMBER));
    }

    @Test
    void testGetActiveDirectorDetailsOkStatusResponse() throws ServiceException, ActiveOfficerNotFoundException {

        when(restTemplate.getForEntity(DUMMY_URL + "/company/" + COMPANY_NUMBER + ACTIVE_DIRECTOR_PATH, ActiveOfficerDetails.class))
                .thenReturn(new ResponseEntity<>(new ActiveOfficerDetails(), HttpStatus.OK));

        var result = oracleQueryClient.getActiveDirectorDetails(COMPANY_NUMBER);
        assertNotNull(result);
    }

    @Test
    void testGetActiveDirectorDetailsStatus400Response() {
        when(restTemplate.getForEntity(DUMMY_URL + "/company/" + COMPANY_NUMBER + ACTIVE_DIRECTOR_PATH, ActiveOfficerDetails.class))
                .thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        assertThrows(ActiveOfficerNotFoundException.class, () -> oracleQueryClient.getActiveDirectorDetails(COMPANY_NUMBER));
    }

    @Test
    void testGetActiveDirectorDetailsNotOkStatusResponse() {
        when(restTemplate.getForEntity(DUMMY_URL + "/company/" + COMPANY_NUMBER + ACTIVE_DIRECTOR_PATH, ActiveOfficerDetails.class))
                .thenReturn(new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE));

        assertThrows(ServiceException.class, () -> oracleQueryClient.getActiveDirectorDetails(COMPANY_NUMBER));
    }

    @Test
    void testGetListActiveOfficersDetailsOkStatusResponse() throws ServiceException {
        var officer1 = new ActiveOfficerDetails();
        var officer2 = new ActiveOfficerDetails();
        ActiveOfficerDetails[] officerArray = {officer1, officer2};

        when(restTemplate.getForEntity(DUMMY_URL + "/company/" + COMPANY_NUMBER + ACTIVE_OFFICERS_PATH, ActiveOfficerDetails[].class))
                .thenReturn(new ResponseEntity<>(officerArray, HttpStatus.OK));

        var result = oracleQueryClient.getActiveOfficersDetails(COMPANY_NUMBER);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testGetListActiveOfficersDetailsNotOkStatusResponse() {
        var companyNumber = "123213";
        when(restTemplate.getForEntity(DUMMY_URL + "/company/" + companyNumber + ACTIVE_OFFICERS_PATH, ActiveOfficerDetails[].class))
                .thenReturn(new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE));

        var serviceException = assertThrows(ServiceException.class, () -> oracleQueryClient.getActiveOfficersDetails(companyNumber));
        assertTrue(serviceException.getMessage().contains(companyNumber));
        assertTrue(serviceException.getMessage().contains(HttpStatus.SERVICE_UNAVAILABLE.toString()));
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
    void testIsConfirmationStatementPaid() throws ServiceException {
        ConfirmationStatementPaymentJson confirmationStatementPaymentJson = new ConfirmationStatementPaymentJson();
        confirmationStatementPaymentJson.setPaid(Boolean.TRUE);
        ResponseEntity<ConfirmationStatementPaymentJson> response = ResponseEntity.status(HttpStatus.OK).body(confirmationStatementPaymentJson);
        when(restTemplate.getForEntity(
                DUMMY_URL + PAYMENT_PATH,
                ConfirmationStatementPaymentJson.class)).thenReturn(response);
        assertTrue(oracleQueryClient.isConfirmationStatementPaid(COMPANY_NUMBER, "2022-01-01"));
    }

    @Test
    void testIsNotConfirmationStatementPaid() throws ServiceException {
        ConfirmationStatementPaymentJson confirmationStatementPaymentJson = new ConfirmationStatementPaymentJson();
        confirmationStatementPaymentJson.setPaid(Boolean.FALSE);
        ResponseEntity<ConfirmationStatementPaymentJson> response = ResponseEntity.status(HttpStatus.OK).body(confirmationStatementPaymentJson);
        when(restTemplate.getForEntity(
                DUMMY_URL + PAYMENT_PATH,
                ConfirmationStatementPaymentJson.class)).thenReturn(response);
        assertFalse(oracleQueryClient.isConfirmationStatementPaid(COMPANY_NUMBER, "2022-01-01"));
    }

    @Test
    void testIsNullConfirmationStatementPaid() {
        ResponseEntity<ConfirmationStatementPaymentJson> response = ResponseEntity.status(HttpStatus.OK).body(null);
        when(restTemplate.getForEntity(
                DUMMY_URL + PAYMENT_PATH,
                ConfirmationStatementPaymentJson.class)).thenReturn(response);
        assertThrows(ServiceException.class, () ->
                oracleQueryClient.isConfirmationStatementPaid(COMPANY_NUMBER, "2022-01-01"));
    }

    @Test
    void testGetRegisteredEmailAddress() throws ServiceException, RegisteredEmailNotFoundException {
        // GIVEN

        var registeredEmailAddress = "info@acme.com";

        var json = new RegisteredEmailAddressJson();
        json.setRegisteredEmailAddress(registeredEmailAddress);

        ResponseEntity<RegisteredEmailAddressJson> response = ResponseEntity.status(HttpStatus.OK).body(json);

        var url = DUMMY_URL + "/company/" + COMPANY_NUMBER + "/registered-email-address";

        // WHEN

        when(restTemplate.getForEntity(url, RegisteredEmailAddressJson.class)).thenReturn(response);

        // THEN

        assertEquals(registeredEmailAddress, oracleQueryClient.getRegisteredEmailAddress(COMPANY_NUMBER));

    }

    @Test
    void testGetRegisteredEmailAddressServiceUnavailable() {
        // GIVEN

        var url = DUMMY_URL + "/company/" + COMPANY_NUMBER + "/registered-email-address";

        // WHEN

        when(restTemplate.getForEntity(url, RegisteredEmailAddressJson.class)).thenThrow(RestClientException.class);

        // THEN

        assertThrows(ServiceException.class, () -> oracleQueryClient.getRegisteredEmailAddress(COMPANY_NUMBER));
    }

    @Test
    void testGetRegisteredEmailAddressCompanyNotFound() {
        // GIVEN

        var url = DUMMY_URL + "/company/" + COMPANY_NUMBER + "/registered-email-address";

        // WHEN

        when(restTemplate.getForEntity(url, RegisteredEmailAddressJson.class)).thenThrow(HttpClientErrorException.class);

        // THEN

        assertThrows(RegisteredEmailNotFoundException.class, () -> oracleQueryClient.getRegisteredEmailAddress(COMPANY_NUMBER));
    }
}
