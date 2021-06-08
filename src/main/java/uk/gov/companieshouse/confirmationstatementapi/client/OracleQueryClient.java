package uk.gov.companieshouse.confirmationstatementapi.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class OracleQueryClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(OracleQueryClient.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${ORACLE_QUERY_API_URL}")
    private String oracleQueryApiUrl;

    public Long getCompanyTradedStatus(String companyNumber) {
        String getCompanyTradedStatusUrl = String.format("%s/company/%s/traded-status", oracleQueryApiUrl, companyNumber);
        LOGGER.info("Calling Oracle Query API URL (get): {}", getCompanyTradedStatusUrl);

        ResponseEntity<Long> response = restTemplate.getForEntity(getCompanyTradedStatusUrl, Long.class);
        var companyTradedStatus = response.getBody();
        LOGGER.info("Received {} from Oracle Query API URL (get): {}", companyTradedStatus, getCompanyTradedStatusUrl);

        return companyTradedStatus;
    }
}
