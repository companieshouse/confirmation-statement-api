package uk.gov.companieshouse.confirmationstatementapi.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.gov.companieshouse.confirmationstatementapi.exception.ActiveOfficerNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.model.ActiveOfficerDetails;
import uk.gov.companieshouse.confirmationstatementapi.model.StatementOfCapital;

@Component
public class OracleQueryClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(OracleQueryClient.class);
    public static final String CALLING_ORACLE_QUERY_API_URL_GET = "Calling Oracle Query API URL (get): {}";
    public static final String RECEIVED_FROM_ORACLE_QUERY_API_URL_GET = "Received {} from Oracle Query API URL (get): {}";

    @Autowired
    private RestTemplate restTemplate;

    @Value("${ORACLE_QUERY_API_URL}")
    private String oracleQueryApiUrl;

    public Long getCompanyTradedStatus(String companyNumber) {
        var getCompanyTradedStatusUrl = String.format("%s/company/%s/traded-status", oracleQueryApiUrl, companyNumber);
        LOGGER.info(CALLING_ORACLE_QUERY_API_URL_GET, getCompanyTradedStatusUrl);

        ResponseEntity<Long> response = restTemplate.getForEntity(getCompanyTradedStatusUrl, Long.class);
        var companyTradedStatus = response.getBody();
        LOGGER.info(RECEIVED_FROM_ORACLE_QUERY_API_URL_GET, companyTradedStatus, getCompanyTradedStatusUrl);

        return companyTradedStatus;
    }

    public Integer getShareholderCount(String companyNumber) {
        var shareholderCountUrl = String.format("%s/company/%s/shareholders/count", oracleQueryApiUrl, companyNumber);
        LOGGER.info(CALLING_ORACLE_QUERY_API_URL_GET, shareholderCountUrl);

        ResponseEntity<Integer> response = restTemplate.getForEntity(shareholderCountUrl, Integer.class);
        var count = response.getBody();
        LOGGER.info(RECEIVED_FROM_ORACLE_QUERY_API_URL_GET, count, shareholderCountUrl);

        return count;
    }

    public StatementOfCapital getStatementOfCapitalData(String companyNumber) throws ServiceException {
        var statementOfCapitalUrl = String.format("%s/company/%s/statement-of-capital", oracleQueryApiUrl, companyNumber);
        LOGGER.info(CALLING_ORACLE_QUERY_API_URL_GET, statementOfCapitalUrl);

        ResponseEntity<StatementOfCapital> response = restTemplate.getForEntity(statementOfCapitalUrl, StatementOfCapital.class);
        if(response.getStatusCode() == HttpStatus.OK) {
            StatementOfCapital statementOfCapital = response.getBody();
            if (statementOfCapital != null) {
                LOGGER.info(RECEIVED_FROM_ORACLE_QUERY_API_URL_GET, statementOfCapital, statementOfCapitalUrl);
                return statementOfCapital;
            } else {
                throw new ServiceException("Oracle query api returned no data");
            }
        } else {
            throw new ServiceException("Oracle query api returned with status " + response.getStatusCode());
        }
    }


    public ActiveOfficerDetails getActiveOfficerDetails(String companyNumber) throws ServiceException, ActiveOfficerNotFoundException {
        var directorDetailsUrl = String.format("%s/company/%s/officer/active", oracleQueryApiUrl, companyNumber);
        LOGGER.info(CALLING_ORACLE_QUERY_API_URL_GET, directorDetailsUrl);

        ResponseEntity<ActiveOfficerDetails> response = restTemplate.getForEntity(directorDetailsUrl, ActiveOfficerDetails.class);

        switch (response.getStatusCode()) {
        case OK:
            var directorDetails = response.getBody();
            LOGGER.info(RECEIVED_FROM_ORACLE_QUERY_API_URL_GET, directorDetails, directorDetailsUrl);
            return directorDetails;
        case NOT_FOUND:
            throw new ActiveOfficerNotFoundException("Oracle query api returned no data. Company has either multiple or no active officers");
        default:
            throw new ServiceException("Oracle query api returned with status " + response.getStatusCode());
        }

    }
}
