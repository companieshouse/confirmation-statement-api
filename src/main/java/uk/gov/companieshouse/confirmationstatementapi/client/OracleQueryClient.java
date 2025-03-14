package uk.gov.companieshouse.confirmationstatementapi.client;

import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.company.RegisteredEmailAddressJson;
import uk.gov.companieshouse.api.model.company.StatementOfCapitalJson;
import uk.gov.companieshouse.api.model.shareholder.ShareholderJson;
import uk.gov.companieshouse.confirmationstatementapi.exception.ActiveOfficerNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.exception.RegisteredEmailNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.exception.StatementOfCapitalNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.model.ActiveOfficerDetails;
import uk.gov.companieshouse.confirmationstatementapi.model.PersonOfSignificantControl;
import uk.gov.companieshouse.confirmationstatementapi.model.json.registerlocation.RegisterLocationJson;
import uk.gov.companieshouse.confirmationstatementapi.utils.ApiLogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;


@Component
public class OracleQueryClient {

    private static final String CALLING_ORACLE_QUERY_API_URL_GET = "Calling Oracle Query API URL: %s";

    private static final String CALLING_INTERNAL_API_CLIENT_GET = "Calling Oracle Query API URL '%s' via Internal Api Client";

    private static final String ORACLE_QUERY_API_STATUS_MESSAGE = "Oracle query api returned with status = %s, companyNumber = %s";

    private static final String REGISTERED_EMAIL_ADDRESS_NOT_FOUND = "Registered Email Address not found";

    private static final String STATEMENT_OF_CAPITAL_NOT_FOUND = "Statement Of Capital not found";

    private static final String REGISTERED_EMAIL_ADDRESS_URI_SUFFIX = "/company/%s/registered-email-address";

    @Autowired
    private ApiClientService apiClientService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${ORACLE_QUERY_API_URL}")
    private String oracleQueryApiUrl;

    @Value("${FEATURE_FLAG_FIVE_OR_LESS_OFFICERS_JOURNEY_21102021:false}")
    private boolean multipleOfficerJourneyFeatureFlag;

    // prafull working on this
    public Long getCompanyTradedStatus(String companyNumber) throws ServiceException {
        var tradedStatusUrl = String.format("%s/company/%s/shareholders/count", oracleQueryApiUrl, companyNumber);
        ApiLogger.info(String.format(CALLING_INTERNAL_API_CLIENT_GET, tradedStatusUrl));

        try {
            var internalApiClient = apiClientService.getInternalApiClient();
//            internalApiClient.setBasePath(oracleQueryApiUrl);

            return internalApiClient
                    .privateCompanyResourceHandler()
                    .getCompanyTradedStatus(tradedStatusUrl)
                    .execute()
                    .getData();
        } catch (Exception e) {
            throw new ServiceException(String.format(ORACLE_QUERY_API_STATUS_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR, companyNumber));
        }
    }

    public Integer getShareholderCount(String companyNumber) throws ServiceException {
        var shareholderCountUrl = String.format("%s/company/%s/shareholders/count", oracleQueryApiUrl, companyNumber);
        ApiLogger.info(String.format(CALLING_INTERNAL_API_CLIENT_GET, shareholderCountUrl));

        try {
            var internalApiClient = apiClientService.getInternalApiClient();
            internalApiClient.setBasePath(oracleQueryApiUrl);

            return internalApiClient
                    .privateCompanyResourceHandler()
                    .getCompanyShareHoldersCount(shareholderCountUrl)
                    .execute()
                    .getData();
        } catch (Exception e) {
            throw new ServiceException(String.format(ORACLE_QUERY_API_STATUS_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR, companyNumber));
        }
    }

    public StatementOfCapitalJson getStatementOfCapitalData(String companyNumber) throws ServiceException, StatementOfCapitalNotFoundException {
        var statementOfCapitalUrl = String.format("%s/company/%s/statement-of-capital", oracleQueryApiUrl, companyNumber);
        ApiLogger.info(String.format(CALLING_INTERNAL_API_CLIENT_GET, statementOfCapitalUrl));

        var internalApiClient = apiClientService.getInternalApiClient();
        internalApiClient.setBasePath(oracleQueryApiUrl);

        try {
            return internalApiClient
                    .privateCompanyResourceHandler()
                    .getStatementOfCapitalData(statementOfCapitalUrl)
                    .execute()
                    .getData();
        } catch (ApiErrorResponseException aere) {
            if (aere.getStatusCode() == NOT_FOUND.value()) {
                throw new StatementOfCapitalNotFoundException(STATEMENT_OF_CAPITAL_NOT_FOUND);
            } else {
                throw new ServiceException(String.format(ORACLE_QUERY_API_STATUS_MESSAGE, aere.getStatusCode(), companyNumber));
            }
        } catch (Exception e) {
            throw new ServiceException(String.format(ORACLE_QUERY_API_STATUS_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR, companyNumber));
        }
    }

    public ActiveOfficerDetails getActiveDirectorDetails(String companyNumber) throws ServiceException, ActiveOfficerNotFoundException {
        String directorDetailsUrl = String.format("/company/%s/director/active", companyNumber);
        ApiLogger.info(String.format(CALLING_ORACLE_QUERY_API_URL_GET, directorDetailsUrl));

        try {
            var internalApiClient = apiClientService.getInternalApiClient();
//            internalApiClient.setBasePath(oracleQueryApiUrl); // unsure if this is needed

            var director = internalApiClient.privateCompanyResourceHandler().getActiveDirector(directorDetailsUrl).execute().getData();
            var activeDirectorDetails = new ActiveOfficerDetails();

            // Copy properties from director to activeDirectorDetails
            BeanUtils.copyProperties(director, activeDirectorDetails);

            return activeDirectorDetails;
        } catch (ApiErrorResponseException e) {
            throw new RuntimeException(e);
        } catch (URIValidationException e) {
            throw new RuntimeException(e);
        }
    }

    //todo
    public List<ActiveOfficerDetails> getActiveOfficersDetails(String companyNumber) throws ServiceException {
        var officersDetailsUrl = String.format("%s/company/%s/officers/active", oracleQueryApiUrl, companyNumber);
        ApiLogger.info(String.format(CALLING_ORACLE_QUERY_API_URL_GET, officersDetailsUrl));

        ResponseEntity<ActiveOfficerDetails[]> response = restTemplate.getForEntity(officersDetailsUrl, ActiveOfficerDetails[].class);
        if (response.getStatusCode() != OK) {
            throw new ServiceException(String.format(ORACLE_QUERY_API_STATUS_MESSAGE, response.getStatusCode(), companyNumber));
        }
        if (response.getBody() == null) {
            return new ArrayList<>();
        }
        return Arrays.asList(response.getBody());
    }

    //todo
    public List<PersonOfSignificantControl> getPersonsOfSignificantControl(String companyNumber) throws ServiceException {
        var pscUrl = String.format("%s/company/%s/corporate-body-appointments/persons-of-significant-control", oracleQueryApiUrl, companyNumber);
        ApiLogger.info(String.format(CALLING_ORACLE_QUERY_API_URL_GET, pscUrl));

        ResponseEntity<PersonOfSignificantControl[]> response = restTemplate.getForEntity(pscUrl, PersonOfSignificantControl[].class);
        if (response.getStatusCode() != OK) {
            throw new ServiceException(String.format(ORACLE_QUERY_API_STATUS_MESSAGE, response.getStatusCode(), companyNumber));
        }
        if (response.getBody() == null) {
            return new ArrayList<>();
        }
        return Arrays.asList(response.getBody());
    }

    //todo
    public List<RegisterLocationJson> getRegisterLocations(String companyNumber) throws ServiceException {
        var regLocUrl = String.format("%s/company/%s/register/location", oracleQueryApiUrl, companyNumber);

        ApiLogger.info(String.format(CALLING_ORACLE_QUERY_API_URL_GET, regLocUrl));

        ResponseEntity<RegisterLocationJson[]> response = restTemplate.getForEntity(regLocUrl, RegisterLocationJson[].class);
        if (response.getStatusCode() != OK) {
            throw new ServiceException(String.format(ORACLE_QUERY_API_STATUS_MESSAGE, response.getStatusCode(), companyNumber));
        }
        if (response.getBody() == null) {
            return new ArrayList<>();
        }
        return Arrays.asList(response.getBody());
    }

    //todo - ShareholderJson moved to private-api-sdk-java
    public List<ShareholderJson> getShareholders(String companyNumber) throws ServiceException {
        var shareholdersUrl = String.format("%s/company/%s/shareholders", oracleQueryApiUrl, companyNumber);
        ApiLogger.info(String.format(CALLING_ORACLE_QUERY_API_URL_GET, shareholdersUrl));

        ResponseEntity<ShareholderJson[]> response = restTemplate.getForEntity(shareholdersUrl, ShareholderJson[].class);
        if (response.getStatusCode() != OK) {
            throw new ServiceException(String.format(ORACLE_QUERY_API_STATUS_MESSAGE, response.getStatusCode(), companyNumber));
        }
        if (response.getBody() == null) {
            return new ArrayList<>();
        }
        return Arrays.asList(response.getBody());
    }

    public boolean isConfirmationStatementPaid(String companyNumber, String paymentPeriodMadeUpToDate) throws ServiceException {
        var paymentsUrl = String.format("/company/%s/confirmation-statement/paid", companyNumber);
        ApiLogger.info(String.format(CALLING_INTERNAL_API_CLIENT_GET, paymentsUrl));

        try {
            var internalApiClient = apiClientService.getInternalApiClient();
            return internalApiClient
                    .privateCompanyResourceHandler()
                    .getConfirmationStatementPayment(paymentsUrl, paymentPeriodMadeUpToDate)
                    .execute()
                    .getData()
                    .isPaid();
        } catch (Exception e) {
            throw new ServiceException(String.format(ORACLE_QUERY_API_STATUS_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR,
                    companyNumber + " with due date " + paymentPeriodMadeUpToDate));
        }
    }

    public RegisteredEmailAddressJson getRegisteredEmailAddress(String companyNumber) throws ServiceException, RegisteredEmailNotFoundException {
        var registeredEmailAddressUrl = String.format(REGISTERED_EMAIL_ADDRESS_URI_SUFFIX, companyNumber);
        ApiLogger.info(String.format(CALLING_INTERNAL_API_CLIENT_GET, registeredEmailAddressUrl));

        try {
            var internalApiClient = apiClientService.getInternalApiClient();
            internalApiClient.setBasePath(oracleQueryApiUrl);

            return internalApiClient
                    .privateCompanyResourceHandler()
                    .getCompanyRegisteredEmailAddress(registeredEmailAddressUrl)
                    .execute()
                    .getData();
        } catch (ApiErrorResponseException aere) {
            if (aere.getStatusCode() == NOT_FOUND.value()) {
                throw new RegisteredEmailNotFoundException(REGISTERED_EMAIL_ADDRESS_NOT_FOUND);
            } else {
                throw new ServiceException(String.format(ORACLE_QUERY_API_STATUS_MESSAGE, aere.getStatusCode(), companyNumber));
            }
        } catch (Exception e) {
            throw new ServiceException(String.format(ORACLE_QUERY_API_STATUS_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR, companyNumber));
        }
    }
}
