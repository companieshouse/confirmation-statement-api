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
import uk.gov.companieshouse.confirmationstatementapi.exception.ActiveOfficerNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.model.ActiveOfficerDetails;
import uk.gov.companieshouse.confirmationstatementapi.model.UsualResidentialAddress;
import uk.gov.companieshouse.confirmationstatementapi.model.json.statementofcapital.StatementOfCapitalJson;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OracleQueryClientTest {

    private static final String DUMMY_URL = "http://test";
    private static final String COMPANY_NUMBER = "12345678";

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
        when(restTemplate.getForEntity(DUMMY_URL + "/company/" + COMPANY_NUMBER + "/statement-of-capital", StatementOfCapitalJson.class))
                .thenReturn(new ResponseEntity<>(new StatementOfCapitalJson(), HttpStatus.OK));
        StatementOfCapitalJson result = oracleQueryClient.getStatementOfCapitalData(COMPANY_NUMBER);
        assertNotNull(result);
    }

    @Test
    void testGetStatementOfCapitalDataNullResponse() {
        when(restTemplate.getForEntity(DUMMY_URL + "/company/" + COMPANY_NUMBER + "/statement-of-capital", StatementOfCapitalJson.class))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));
        assertThrows(ServiceException.class, () -> oracleQueryClient.getStatementOfCapitalData(COMPANY_NUMBER));
    }

    @Test
    void testGetStatementOfCapitalDataNotOkStatusResponse() {
        when(restTemplate.getForEntity(DUMMY_URL + "/company/" + COMPANY_NUMBER + "/statement-of-capital", StatementOfCapitalJson.class))
                .thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThrows(ServiceException.class, () -> oracleQueryClient.getStatementOfCapitalData(COMPANY_NUMBER));
    }

    @Test
    void testGetActiveOfficerDetailsOkStatusResponse() throws ServiceException, ActiveOfficerNotFoundException {

        when(restTemplate.getForEntity(DUMMY_URL + "/company/" + COMPANY_NUMBER + "/officer/active", ActiveOfficerDetails.class))
                .thenReturn(new ResponseEntity<>(new ActiveOfficerDetails(), HttpStatus.OK));

        var result = oracleQueryClient.getActiveOfficerDetails(COMPANY_NUMBER);
        assertNotNull(result);
    }

    @Test
    void testGetActiveOfficerDetailsStatus400Response() {
        when(restTemplate.getForEntity(DUMMY_URL + "/company/" + COMPANY_NUMBER + "/officer/active", ActiveOfficerDetails.class))
                .thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        assertThrows(ActiveOfficerNotFoundException.class, () -> oracleQueryClient.getActiveOfficerDetails(COMPANY_NUMBER));
    }

    @Test
    void testGetActiveOfficerDetailsNotOkStatusResponse() {
        when(restTemplate.getForEntity(DUMMY_URL + "/company/" + COMPANY_NUMBER + "/officer/active", ActiveOfficerDetails.class))
                .thenReturn(new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE));

        assertThrows(ServiceException.class, () -> oracleQueryClient.getActiveOfficerDetails(COMPANY_NUMBER));
    }

    @Test
    void testGetUsualResidentialAddressResponse() throws ServiceException {
        var AREA = "north england";
        var COUNTRY_NAME = "england";
        var HOUSE_NAME_NUMBER = "12 house name";
        var POST_TOWN = "leeds";
        var REGION = "yorkshire";
        var STREET = "far close";

        var ura = new UsualResidentialAddress();
        ura.setArea(AREA);
        ura.setCountryName(COUNTRY_NAME);
        ura.setHouseNameNumber(HOUSE_NAME_NUMBER);
        ura.setPostTown(POST_TOWN);
        ura.setRegion(REGION);
        ura.setStreet(STREET);

        var corpBodyAppointmentId = "123213";

        when(restTemplate.getForEntity(DUMMY_URL + "/corporate-body-appointment/" + corpBodyAppointmentId + "/usual-residential-address", UsualResidentialAddress.class))
                .thenReturn(new ResponseEntity<>(ura, HttpStatus.OK));

        var result = oracleQueryClient.getUsualResidentialAddress(corpBodyAppointmentId);
        assertEquals(AREA, result.getArea());
        assertEquals(COUNTRY_NAME, result.getCountryName());
        assertEquals(HOUSE_NAME_NUMBER, result.getHouseNameNumber());
        assertEquals(POST_TOWN, result.getPostTown());
        assertEquals(REGION, result.getRegion());
        assertEquals(STREET, result.getStreet());
    }

    @Test
    void testGetUsualResidentialAddressNotOkStatusResponse() {
        var corpBodyAppointmentId = "123213";
        when(restTemplate.getForEntity(DUMMY_URL + "/corporate-body-appointment/" + corpBodyAppointmentId + "/usual-residential-address", UsualResidentialAddress.class))
                .thenReturn(new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE));

        var serviceException = assertThrows(ServiceException.class, () -> oracleQueryClient.getUsualResidentialAddress(corpBodyAppointmentId));
        assertTrue(serviceException.getMessage().contains(corpBodyAppointmentId));
        assertTrue(serviceException.getMessage().contains(HttpStatus.SERVICE_UNAVAILABLE.toString()));
    }
}
