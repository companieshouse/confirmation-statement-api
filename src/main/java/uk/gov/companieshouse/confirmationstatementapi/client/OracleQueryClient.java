package uk.gov.companieshouse.confirmationstatementapi.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.model.company.RegisteredEmailAddressJson;
import uk.gov.companieshouse.confirmationstatementapi.exception.ActiveOfficerNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.exception.RegisteredEmailNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.exception.StatementOfCapitalNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.model.ActiveOfficerDetails;
import uk.gov.companieshouse.confirmationstatementapi.model.PersonOfSignificantControl;
import uk.gov.companieshouse.confirmationstatementapi.model.json.payment.ConfirmationStatementPaymentJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.registerlocation.RegisterLocationJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.shareholder.ShareholderJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.statementofcapital.StatementOfCapitalJson;
import uk.gov.companieshouse.confirmationstatementapi.utils.ApiLogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Component
public class OracleQueryClient {

    private static final String CALLING_ORACLE_QUERY_API_URL_GET = "Calling Oracle Query API URL: %s";

    private static final String ORACLE_QUERY_API_STATUS_MESSAGE = "Oracle query api returned with status = %s, companyNumber = %s";

    private static final String ORACLE_QUERY_API_NO_DATA = "Oracle query api returned no data";

    private static final String REGISTERED_EMAIL_ADDRESS_NOT_FOUND = "Registered Email Address not found";

    private static final String REGISTERED_EMAIL_ADDRESS_URI_SUFFIX = "/company/%s/registered-email-address";

    @Autowired
    private ApiClientService apiClientService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${ORACLE_QUERY_API_URL}")
    private String oracleQueryApiUrl;

    @Value("${FEATURE_FLAG_FIVE_OR_LESS_OFFICERS_JOURNEY_21102021:false}")
    private boolean multipleOfficerJourneyFeatureFlag;

    public Long getCompanyTradedStatus(String companyNumber) {
        var getCompanyTradedStatusUrl = String.format("%s/company/%s/traded-status", oracleQueryApiUrl, companyNumber);
        ApiLogger.info(String.format(CALLING_ORACLE_QUERY_API_URL_GET, getCompanyTradedStatusUrl));

        ResponseEntity<Long> response = restTemplate.getForEntity(getCompanyTradedStatusUrl, Long.class);
        return response.getBody();
    }

    public Integer getShareholderCount(String companyNumber) {

        var shareholderCountUrl = String.format("%s/company/%s/shareholders/count", oracleQueryApiUrl, companyNumber);
        ApiLogger.info(String.format(CALLING_ORACLE_QUERY_API_URL_GET, shareholderCountUrl));

        ResponseEntity<Integer> response = restTemplate.getForEntity(shareholderCountUrl, Integer.class);
        return response.getBody();
    }

    public StatementOfCapitalJson getStatementOfCapitalData(String companyNumber) throws ServiceException, StatementOfCapitalNotFoundException {
        var statementOfCapitalUrl = String.format("%s/company/%s/statement-of-capital", oracleQueryApiUrl, companyNumber);
        ApiLogger.info(String.format(CALLING_ORACLE_QUERY_API_URL_GET, statementOfCapitalUrl));

        ResponseEntity<StatementOfCapitalJson> response = restTemplate.getForEntity(statementOfCapitalUrl, StatementOfCapitalJson.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            var statementOfCapitalJson = response.getBody();
            if (statementOfCapitalJson != null) {
                return statementOfCapitalJson;
            } else {
                throw new StatementOfCapitalNotFoundException(ORACLE_QUERY_API_NO_DATA);
            }
        } else {
            throw new ServiceException("Oracle query api returned with status " + response.getStatusCode());
        }
    }


    public ActiveOfficerDetails getActiveDirectorDetails(String companyNumber) throws ServiceException, ActiveOfficerNotFoundException {
        var directorDetailsUrl = String.format("%s/company/%s/director/active", oracleQueryApiUrl, companyNumber);
        ApiLogger.info(String.format(CALLING_ORACLE_QUERY_API_URL_GET, directorDetailsUrl));

        ResponseEntity<ActiveOfficerDetails> response = restTemplate.getForEntity(directorDetailsUrl, ActiveOfficerDetails.class);

        switch (response.getStatusCode()) {
            case OK:
                return response.getBody();
            case NOT_FOUND:
                throw new ActiveOfficerNotFoundException("Oracle query api returned no data. Company has either multiple or no active officers");
            default:
                throw new ServiceException("Oracle query api returned with status " + response.getStatusCode());
        }
    }

    public List<ActiveOfficerDetails> getActiveOfficersDetails(String companyNumber) throws ServiceException {
        var officersDetailsUrl = String.format("%s/company/%s/officers/active", oracleQueryApiUrl, companyNumber);
        ApiLogger.info(String.format(CALLING_ORACLE_QUERY_API_URL_GET, officersDetailsUrl));

        ResponseEntity<ActiveOfficerDetails[]> response = restTemplate.getForEntity(officersDetailsUrl, ActiveOfficerDetails[].class);
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new ServiceException(String.format(ORACLE_QUERY_API_STATUS_MESSAGE, response.getStatusCode(), companyNumber));
        }
        if (response.getBody() == null) {
            return new ArrayList<>();
        }
        return Arrays.asList(response.getBody());
    }

    public List<PersonOfSignificantControl> getPersonsOfSignificantControl(String companyNumber) throws ServiceException {
        var pscUrl = String.format("%s/company/%s/corporate-body-appointments/persons-of-significant-control", oracleQueryApiUrl, companyNumber);
        ApiLogger.info(String.format(CALLING_ORACLE_QUERY_API_URL_GET, pscUrl));

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

        ApiLogger.info(String.format(CALLING_ORACLE_QUERY_API_URL_GET, regLocUrl));

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
        ApiLogger.info(String.format(CALLING_ORACLE_QUERY_API_URL_GET, shareholdersUrl));

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

        ApiLogger.info(String.format(CALLING_ORACLE_QUERY_API_URL_GET, paymentsUrl));
        ResponseEntity<ConfirmationStatementPaymentJson> response = restTemplate.getForEntity(paymentsUrl, ConfirmationStatementPaymentJson.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new ServiceException(String.format(ORACLE_QUERY_API_STATUS_MESSAGE + " with due date %s", response.getStatusCode(), companyNumber, dueDate));
        }
        var confirmationStatementPaymentJson = response.getBody();
        if (confirmationStatementPaymentJson == null || confirmationStatementPaymentJson.isPaid() == null) {
            throw new ServiceException("Oracle query api returned null for " + companyNumber + " with due date " + dueDate + ", boolean values expected");
        }
        return confirmationStatementPaymentJson.isPaid();
    }
// test comment to try to fix concourse build
    public RegisteredEmailAddressJson getRegisteredEmailAddress(String companyNumber) throws ServiceException, RegisteredEmailNotFoundException {
        try {
            var internalApiClient = apiClientService.getInternalApiClient();
            internalApiClient.setBasePath(oracleQueryApiUrl);

            return internalApiClient
                    .privateCompanyResourceHandler()
                    .getCompanyRegisteredEmailAddress(String.format(REGISTERED_EMAIL_ADDRESS_URI_SUFFIX, companyNumber))
                    .execute()
                    .getData();
        } catch (ApiErrorResponseException aere) {
            if (aere.getStatusCode() == HttpStatus.NOT_FOUND.value()) {
                throw new RegisteredEmailNotFoundException(REGISTERED_EMAIL_ADDRESS_NOT_FOUND);
            } else {
                throw new ServiceException(String.format(ORACLE_QUERY_API_STATUS_MESSAGE, aere.getStatusCode(), companyNumber));
            }
        } catch (Exception e) {
            throw new ServiceException(String.format(ORACLE_QUERY_API_STATUS_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR, companyNumber));
        }
    }
}
