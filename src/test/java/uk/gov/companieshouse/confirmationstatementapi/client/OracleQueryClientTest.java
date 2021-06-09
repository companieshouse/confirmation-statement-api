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

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    public void setup() {
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
}
