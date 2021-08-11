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
import org.springframework.web.client.RestTemplate;
import uk.gov.companieshouse.confirmationstatementapi.exception.ActiveDirectorNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.model.ActiveDirectorDetails;
import uk.gov.companieshouse.confirmationstatementapi.model.PersonOfSignificantControl;
import uk.gov.companieshouse.confirmationstatementapi.model.json.shareholder.ShareholderJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.statementofcapital.StatementOfCapitalJson;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OracleQueryClientTest {

    private static final String ACTIVE_DIRECTOR_PATH = "/director/active";
    private static final String COMPANY_NUMBER = "12345678";
    private static final String DUMMY_URL = "http://test";
    private static final String PSC_PATH = "/corporate-body-appointments/persons-of-significant-control";
    private static final String SOC_PATH = "/statement-of-capital";
    private static final String SHAREHOLDER_PATH = "/shareholders";

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
    void testGetStatementOfCapitalData() throws ServiceException {
        when(restTemplate.getForEntity(DUMMY_URL + "/company/" + COMPANY_NUMBER + SOC_PATH, StatementOfCapitalJson.class))
                .thenReturn(new ResponseEntity<>(new StatementOfCapitalJson(), HttpStatus.OK));
        StatementOfCapitalJson result = oracleQueryClient.getStatementOfCapitalData(COMPANY_NUMBER);
        assertNotNull(result);
    }

    @Test
    void testGetStatementOfCapitalDataNullResponse() {
        when(restTemplate.getForEntity(DUMMY_URL + "/company/" + COMPANY_NUMBER + SOC_PATH, StatementOfCapitalJson.class))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));
        assertThrows(ServiceException.class, () -> oracleQueryClient.getStatementOfCapitalData(COMPANY_NUMBER));
    }

    @Test
    void testGetStatementOfCapitalDataNotOkStatusResponse() {
        when(restTemplate.getForEntity(DUMMY_URL + "/company/" + COMPANY_NUMBER + SOC_PATH, StatementOfCapitalJson.class))
                .thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThrows(ServiceException.class, () -> oracleQueryClient.getStatementOfCapitalData(COMPANY_NUMBER));
    }

    @Test
    void testGetActiveDirectorDetailsOkStatusResponse() throws ServiceException, ActiveDirectorNotFoundException {

        when(restTemplate.getForEntity(DUMMY_URL + "/company/" + COMPANY_NUMBER + ACTIVE_DIRECTOR_PATH, ActiveDirectorDetails.class))
                .thenReturn(new ResponseEntity<>(new ActiveDirectorDetails(), HttpStatus.OK));

        var result = oracleQueryClient.getActiveDirectorDetails(COMPANY_NUMBER);
        assertNotNull(result);
    }

    @Test
    void testGetActiveDirectorDetailsStatus400Response() {
        when(restTemplate.getForEntity(DUMMY_URL + "/company/" + COMPANY_NUMBER + ACTIVE_DIRECTOR_PATH, ActiveDirectorDetails.class))
                .thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        assertThrows(ActiveDirectorNotFoundException.class, () -> oracleQueryClient.getActiveDirectorDetails(COMPANY_NUMBER));
    }

    @Test
    void testGetActiveDirectorDetailsNotOkStatusResponse() {
        when(restTemplate.getForEntity(DUMMY_URL + "/company/" + COMPANY_NUMBER + ACTIVE_DIRECTOR_PATH, ActiveDirectorDetails.class))
                .thenReturn(new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE));

        assertThrows(ServiceException.class, () -> oracleQueryClient.getActiveDirectorDetails(COMPANY_NUMBER));
    }

    @Test
    void testGetPersonsOfSignificantControlResponse() throws ServiceException {
        var psc1 = new PersonOfSignificantControl();
        psc1.setAppointmentTypeId("1");
        psc1.setServiceAddressLine1("1 some street");
        psc1.setServiceAddressPostCode("post code");


        var psc2 = new PersonOfSignificantControl();
        psc2.setAppointmentTypeId("1");
        psc2.setServiceAddressLine1("1 some street");
        psc2.setServiceAddressPostCode("post code");

        PersonOfSignificantControl[] pscArray = { psc1, psc2 };

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
        shareholder1.setForename1("John");
        shareholder1.setForename2("K");
        shareholder1.setSurname("Lewis");


        var shareholder2 = new ShareholderJson();
        shareholder2.setForename1("James");
        shareholder2.setSurname("Bond");

        ShareholderJson[] shareholderArray = { shareholder1, shareholder2 };

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
}
