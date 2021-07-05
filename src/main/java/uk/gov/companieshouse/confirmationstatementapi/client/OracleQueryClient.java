package uk.gov.companieshouse.confirmationstatementapi.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.model.StatementOfCapital;

@Component
public class OracleQueryClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(OracleQueryClient.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${ORACLE_QUERY_API_URL}")
    private String oracleQueryApiUrl;

    public Long getCompanyTradedStatus(String companyNumber) {
        var getCompanyTradedStatusUrl = String.format("%s/company/%s/traded-status", oracleQueryApiUrl, companyNumber);
        LOGGER.info("Calling Oracle Query API URL (get): {}", getCompanyTradedStatusUrl);

        ResponseEntity<Long> response = restTemplate.getForEntity(getCompanyTradedStatusUrl, Long.class);
        var companyTradedStatus = response.getBody();
        LOGGER.info("Received {} from Oracle Query API URL (get): {}", companyTradedStatus, getCompanyTradedStatusUrl);

        return companyTradedStatus;
    }

    public Integer getShareholderCount(String companyNumber) {
        var shareholderCountUrl = String.format("%s/company/%s/shareholders/count", oracleQueryApiUrl, companyNumber);
        LOGGER.info("Calling Oracle Query API URL (get): {}", shareholderCountUrl);

        ResponseEntity<Integer> response = restTemplate.getForEntity(shareholderCountUrl, Integer.class);
        var count = response.getBody();
        LOGGER.info("Received {} from Oracle Query API URL (get): {}", count, shareholderCountUrl);

        return count;
    }

    public StatementOfCapital getStatmentOfCapitalData(String companyNumber) throws ServiceException {
        var statementOfCapitalUrl = String.format("%s/company/%s/statement-of-capital", oracleQueryApiUrl, companyNumber);
        LOGGER.info("Calling Oracle Query API URL (get): {}", statementOfCapitalUrl);

        ResponseEntity<StatementOfCapital> response = restTemplate.getForEntity(statementOfCapitalUrl, StatementOfCapital.class);
        if(response.getStatusCode() == HttpStatus.OK) {
            StatementOfCapital statementOfCapital = response.getBody();
            if (statementOfCapital != null) {
                LOGGER.info("Received {} from Oracle Query API URL (get): {}", statementOfCapital.toString(), statementOfCapitalUrl);
                return statementOfCapital;
            } else {
                throw new ServiceException("Oracle query api returned no data");
            }
        } else {
            throw new ServiceException("Oracle query api returned with status " + response.getStatusCode());
        }
    }
}
