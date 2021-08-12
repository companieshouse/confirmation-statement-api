package uk.gov.companieshouse.confirmationstatementapi.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.gov.companieshouse.confirmationstatementapi.exception.ActiveDirectorNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.model.ActiveDirectorDetails;
import uk.gov.companieshouse.confirmationstatementapi.model.PersonOfSignificantControl;
import uk.gov.companieshouse.confirmationstatementapi.model.json.shareholder.ShareholderJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.statementofcapital.StatementOfCapitalJson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class OracleQueryClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(OracleQueryClient.class);
    private static final String CALLING_ORACLE_QUERY_API_URL_GET = "Calling Oracle Query API URL (get): {}";
    private static final String RECEIVED_FROM_ORACLE_QUERY_API_URL_GET = "Received {} from Oracle Query API URL (get): {}";

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

    public StatementOfCapitalJson getStatementOfCapitalData(String companyNumber) throws ServiceException {
        var statementOfCapitalUrl = String.format("%s/company/%s/statement-of-capital", oracleQueryApiUrl, companyNumber);
        LOGGER.info(CALLING_ORACLE_QUERY_API_URL_GET, statementOfCapitalUrl);

        ResponseEntity<StatementOfCapitalJson> response = restTemplate.getForEntity(statementOfCapitalUrl, StatementOfCapitalJson.class);
        if(response.getStatusCode() == HttpStatus.OK) {
            StatementOfCapitalJson statementOfCapitalJson = response.getBody();
            if (statementOfCapitalJson != null) {
                LOGGER.info(RECEIVED_FROM_ORACLE_QUERY_API_URL_GET, statementOfCapitalJson, statementOfCapitalUrl);
                return statementOfCapitalJson;
            } else {
                throw new ServiceException("Oracle query api returned no data");
            }
        } else {
            throw new ServiceException("Oracle query api returned with status " + response.getStatusCode());
        }
    }


    public ActiveDirectorDetails getActiveDirectorDetails(String companyNumber) throws ServiceException, ActiveDirectorNotFoundException {
        var directorDetailsUrl = String.format("%s/company/%s/director/active", oracleQueryApiUrl, companyNumber);
        LOGGER.info(CALLING_ORACLE_QUERY_API_URL_GET, directorDetailsUrl);

        ResponseEntity<ActiveDirectorDetails> response = restTemplate.getForEntity(directorDetailsUrl, ActiveDirectorDetails.class);

        switch (response.getStatusCode()) {
        case OK:
            var directorDetails = response.getBody();
            LOGGER.info(RECEIVED_FROM_ORACLE_QUERY_API_URL_GET, directorDetails, directorDetailsUrl);
            return directorDetails;
        case NOT_FOUND:
            throw new ActiveDirectorNotFoundException("Oracle query api returned no data. Company has either multiple or no active officers");
        default:
            throw new ServiceException("Oracle query api returned with status " + response.getStatusCode());
        }
    }

    public List<PersonOfSignificantControl> getPersonsOfSignificantControl(String companyNumber) throws ServiceException {
        var pscUrl = String.format("%s/company/%s/corporate-body-appointments/persons-of-significant-control", oracleQueryApiUrl, companyNumber);
        LOGGER.info(CALLING_ORACLE_QUERY_API_URL_GET, pscUrl);

        ResponseEntity<PersonOfSignificantControl[]> response = restTemplate.getForEntity(pscUrl, PersonOfSignificantControl[].class);
        LOGGER.info(RECEIVED_FROM_ORACLE_QUERY_API_URL_GET, response, pscUrl);
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new ServiceException(String.format("Oracle query api returned with status = %s, companyNumber = %s", response.getStatusCode(), companyNumber));
        }
        if (response.getBody() == null) {
            return new ArrayList<>();
        }
        return Arrays.asList(response.getBody());
    }

    public List<ShareholderJson> getShareholders(String companyNumber) throws ServiceException {
        var shareholdersUrl = String.format("%s/company/%s/shareholders", oracleQueryApiUrl, companyNumber);
        LOGGER.info(CALLING_ORACLE_QUERY_API_URL_GET, shareholdersUrl);

        ResponseEntity<ShareholderJson[]> response = restTemplate.getForEntity(shareholdersUrl, ShareholderJson[].class);
        LOGGER.info(RECEIVED_FROM_ORACLE_QUERY_API_URL_GET, response, shareholdersUrl);
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new ServiceException(String.format("Oracle query api returned with status = %s, companyNumber = %s", response.getStatusCode(), companyNumber));
        }
        if (response.getBody() == null) {
            return new ArrayList<>();
        }
        return Arrays.asList(response.getBody());
    }
}
