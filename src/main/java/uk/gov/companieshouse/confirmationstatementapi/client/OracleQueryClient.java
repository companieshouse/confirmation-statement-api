package uk.gov.companieshouse.confirmationstatementapi.client;

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
import uk.gov.companieshouse.confirmationstatementapi.model.json.payment.ConfirmationStatementPaymentJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.registerlocation.RegisterLocationJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.shareholder.ShareholderJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.statementofcapital.StatementOfCapitalJson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static uk.gov.companieshouse.confirmationstatementapi.ConfirmationStatementApiApplication.LOGGER;

@Component
public class OracleQueryClient {

    private static final String CALLING_ORACLE_QUERY_API_URL_GET = "Calling Oracle Query API URL: %s";
    public static final String ORACLE_QUERY_API_STATUS_MESSAGE = "Oracle query api returned with status = %s, companyNumber = %s";

    @Autowired
    private RestTemplate restTemplate;

    @Value("${ORACLE_QUERY_API_URL}")
    private String oracleQueryApiUrl;

    public Long getCompanyTradedStatus(String companyNumber) {
        var getCompanyTradedStatusUrl = String.format("%s/company/%s/traded-status", oracleQueryApiUrl, companyNumber);
        LOGGER.info(String.format(CALLING_ORACLE_QUERY_API_URL_GET, getCompanyTradedStatusUrl));

        ResponseEntity<Long> response = restTemplate.getForEntity(getCompanyTradedStatusUrl, Long.class);
        var companyTradedStatus = response.getBody();

        return companyTradedStatus;
    }

    public Integer getShareholderCount(String companyNumber) {

        var shareholderCountUrl = String.format("%s/company/%s/shareholders/count", oracleQueryApiUrl, companyNumber);
        LOGGER.info(String.format(CALLING_ORACLE_QUERY_API_URL_GET, shareholderCountUrl));

        ResponseEntity<Integer> response = restTemplate.getForEntity(shareholderCountUrl, Integer.class);
        return response.getBody();
    }

    public StatementOfCapitalJson getStatementOfCapitalData(String companyNumber) throws ServiceException {
        var statementOfCapitalUrl = String.format("%s/company/%s/statement-of-capital", oracleQueryApiUrl, companyNumber);
        LOGGER.info(String.format(CALLING_ORACLE_QUERY_API_URL_GET, statementOfCapitalUrl));

        ResponseEntity<StatementOfCapitalJson> response = restTemplate.getForEntity(statementOfCapitalUrl, StatementOfCapitalJson.class);
        if(response.getStatusCode() == HttpStatus.OK) {
            StatementOfCapitalJson statementOfCapitalJson = response.getBody();
            if (statementOfCapitalJson != null) {
                return statementOfCapitalJson;
            }
            else {
                throw new ServiceException("Oracle query api returned no data");
            }
        } else {
            throw new ServiceException("Oracle query api returned with status " + response.getStatusCode());
        }
    }


    public ActiveDirectorDetails getActiveDirectorDetails(String companyNumber) throws ServiceException, ActiveDirectorNotFoundException {
        var directorDetailsUrl = String.format("%s/company/%s/director/active", oracleQueryApiUrl, companyNumber);
        LOGGER.info(String.format(CALLING_ORACLE_QUERY_API_URL_GET, directorDetailsUrl));

        ResponseEntity<ActiveDirectorDetails> response = restTemplate.getForEntity(directorDetailsUrl, ActiveDirectorDetails.class);

        switch (response.getStatusCode()) {
        case OK:
            var directorDetails = response.getBody();
            return directorDetails;
        case NOT_FOUND:
            throw new ActiveDirectorNotFoundException("Oracle query api returned no data. Company has either multiple or no active officers");
        default:
            throw new ServiceException("Oracle query api returned with status " + response.getStatusCode());
        }
    }

    public List<PersonOfSignificantControl> getPersonsOfSignificantControl(String companyNumber) throws ServiceException {
        var pscUrl = String.format("%s/company/%s/corporate-body-appointments/persons-of-significant-control", oracleQueryApiUrl, companyNumber);
        LOGGER.info(String.format(CALLING_ORACLE_QUERY_API_URL_GET, pscUrl));

        ResponseEntity<PersonOfSignificantControl[]> response = restTemplate.getForEntity(pscUrl, PersonOfSignificantControl[].class);
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new ServiceException(String.format(ORACLE_QUERY_API_STATUS_MESSAGE, response.getStatusCode(), companyNumber));
        }
        if (response.getBody() == null) {
            return new ArrayList<>();
        }
        return Arrays.asList(response.getBody());
    }

    public List<RegisterLocationJson> getRegisterLocations(String companyNumber) throws ServiceException {
        var regLocUrl = String.format("%s/company/%s/register/location", oracleQueryApiUrl, companyNumber);

        LOGGER.info(String.format(CALLING_ORACLE_QUERY_API_URL_GET, regLocUrl));

        ResponseEntity<RegisterLocationJson[]> response = restTemplate.getForEntity(regLocUrl, RegisterLocationJson[].class);
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new ServiceException(String.format(ORACLE_QUERY_API_STATUS_MESSAGE, response.getStatusCode(), companyNumber));
        }
        if (response.getBody() == null) {
            return new ArrayList<>();
        }
        return Arrays.asList(response.getBody());
    }

    public List<ShareholderJson> getShareholders(String companyNumber) throws ServiceException {
        var shareholdersUrl = String.format("%s/company/%s/shareholders", oracleQueryApiUrl, companyNumber);
        LOGGER.info(String.format(CALLING_ORACLE_QUERY_API_URL_GET, shareholdersUrl));

        ResponseEntity<ShareholderJson[]> response = restTemplate.getForEntity(shareholdersUrl, ShareholderJson[].class);
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new ServiceException(String.format(ORACLE_QUERY_API_STATUS_MESSAGE, response.getStatusCode(), companyNumber));
        }
        if (response.getBody() == null) {
            return new ArrayList<>();
        }
        return Arrays.asList(response.getBody());
    }

    public boolean isConfirmationStatementPaid(String companyNumber, String dueDate) throws ServiceException {
       var paymentsUrl = String.format(
               "%s/company/%s/confirmation-statement/paid?payment_period_made_up_to_date=%s", oracleQueryApiUrl, companyNumber, dueDate);

        LOGGER.info(String.format(CALLING_ORACLE_QUERY_API_URL_GET, paymentsUrl));
        ResponseEntity<ConfirmationStatementPaymentJson> response = restTemplate.getForEntity(paymentsUrl, ConfirmationStatementPaymentJson.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new ServiceException(String.format(ORACLE_QUERY_API_STATUS_MESSAGE + " with due date %s", response.getStatusCode(), companyNumber, dueDate));
        }
        ConfirmationStatementPaymentJson confirmationStatementPaymentJson = response.getBody();
        if (confirmationStatementPaymentJson == null || confirmationStatementPaymentJson.isPaid() == null) {
            throw new ServiceException("Oracle query api returned null for " + companyNumber + " with due date " + dueDate + ", boolean values expected");
        }
        return response.getBody().isPaid();
    }
}
