package uk.gov.companieshouse.confirmationstatementapi.client;

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
import uk.gov.companieshouse.api.model.company.ActiveOfficerDetailsJson;
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
    private static final String API_PATH_COMPANY_TRADED_STATUS = "/company/%s/traded-status";
    private static final String API_PATH_COMPANY_SHAREHOLDERS_COUNT = "/company/%s/shareholders/count";
    private static final String API_PATH_COMPANY_STATEMENT_OF_CAPITAL = "/company/%s/statement-of-capital";
    private static final String API_PATH_COMPANY_DIRECTOR_ACTIVE = "/company/%s/director/active";
    private static final String API_PATH_OFFICERS_ACTIVE = "/company/%s/officers/active";
    private static final String API_PATH_COMPANY_CORPORATE_BODY_APPOINTMENTS_PSC = "/company/%s/corporate-body-appointments/persons-of-significant-control";
    private static final String API_PATH_COMPANY_CONFIRMATION_STATEMENT_PAID = "/company/%s/confirmation-statement/paid";
    private static final String API_PATH_REGISTERED_EMAIL_ADDRESS = "/company/%s/registered-email-address";
    private static final String EXCEPTION_INVALID_URI = "Invalid URI: %s";
    private static final String GENERAL_EXCEPTION_API_CALL = "Error occurred while calling '%s'";


    @Autowired
    private ApiClientService apiClientService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${ORACLE_QUERY_API_URL}")
    private String oracleQueryApiUrl;

    @Value("${FEATURE_FLAG_FIVE_OR_LESS_OFFICERS_JOURNEY_21102021:false}")
    private boolean multipleOfficerJourneyFeatureFlag;

    public Long getCompanyTradedStatus(String companyNumber) throws ServiceException {
        var tradedStatusUrl = String.format(API_PATH_COMPANY_TRADED_STATUS, companyNumber);
        ApiLogger.info(String.format(CALLING_INTERNAL_API_CLIENT_GET, tradedStatusUrl));

        try {
            var internalApiClient = apiClientService.getInternalApiClient();

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
        var shareholderCountUrl = String.format(API_PATH_COMPANY_SHAREHOLDERS_COUNT, companyNumber);
        ApiLogger.info(String.format(CALLING_INTERNAL_API_CLIENT_GET, shareholderCountUrl));

        try {
            var internalApiClient = apiClientService.getInternalApiClient();

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
        var statementOfCapitalUrl = String.format(API_PATH_COMPANY_STATEMENT_OF_CAPITAL, companyNumber);
        ApiLogger.info(String.format(CALLING_INTERNAL_API_CLIENT_GET, statementOfCapitalUrl));

        var internalApiClient = apiClientService.getInternalApiClient();

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

    //todo Migrate to private sdk ActiveOfficerDetails model
    public ActiveOfficerDetailsJson getActiveDirectorDetails(String companyNumber) throws ServiceException,
            ActiveOfficerNotFoundException {
        String directorDetailsUrl = String.format(API_PATH_COMPANY_DIRECTOR_ACTIVE, companyNumber);
        ApiLogger.info(String.format(CALLING_ORACLE_QUERY_API_URL_GET, directorDetailsUrl));

        try {
            var internalApiClient = apiClientService.getInternalApiClient();
//            internalApiClient.setBasePath(oracleQueryApiUrl); // unsure if this is needed

            var director = internalApiClient.privateCompanyResourceHandler().getActiveDirector(directorDetailsUrl).execute().getData();

            // Active officer not found handling
            if (director == null) {
//                throw new ActiveOfficerNotFoundException(directorDetailsUrl);
                return new ActiveOfficerDetailsJson();
            }

            return director;
        } catch (ApiErrorResponseException aere) {
            throw new ServiceException(String.format(ORACLE_QUERY_API_STATUS_MESSAGE, aere.getStatusCode(),
                    companyNumber), aere);
        }  catch (URIValidationException urive) {
            throw new ServiceException(String.format(EXCEPTION_INVALID_URI, directorDetailsUrl), urive);
        } catch (Exception e) {
            throw new ServiceException(String.format(GENERAL_EXCEPTION_API_CALL, directorDetailsUrl), e);
        }
    }

    //todo Migrate to private sdk ActiveOfficerDetails model
    public List<ActiveOfficerDetailsJson> getActiveOfficersDetails(String companyNumber) throws ServiceException {
        var officersDetailsUrl = String.format(API_PATH_OFFICERS_ACTIVE, companyNumber);
        ApiLogger.info(String.format(CALLING_ORACLE_QUERY_API_URL_GET, officersDetailsUrl));

        // NEW
        try {
            var internalApiClient = apiClientService.getInternalApiClient();
            uk.gov.companieshouse.api.model.company.ActiveOfficerDetailsJson[] officers =
                    internalApiClient.privateCompanyResourceHandler().getActiveOfficers(officersDetailsUrl).execute().getData();

            // Active officer not found handling
            if (officers == null) {
//                throw new ActiveOfficerNotFoundException(officersDetailsUrl);
                return new ArrayList<>();
            }

            return Arrays.asList(officers);

        } catch (ApiErrorResponseException aere) {
            throw new ServiceException(String.format(ORACLE_QUERY_API_STATUS_MESSAGE, aere.getStatusCode(),
                    companyNumber), aere);
        }  catch (URIValidationException urive) {
            throw new ServiceException(String.format(EXCEPTION_INVALID_URI, officersDetailsUrl), urive);
        } catch (Exception e) {
            throw new ServiceException(String.format(GENERAL_EXCEPTION_API_CALL, officersDetailsUrl), e);
        }
    }

    //todo
    public List<PersonOfSignificantControl> getPersonsOfSignificantControl(String companyNumber) throws ServiceException {
        var pscUrl = String.format(API_PATH_COMPANY_CORPORATE_BODY_APPOINTMENTS_PSC, oracleQueryApiUrl, companyNumber);
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
        var paymentsUrl = String.format(API_PATH_COMPANY_CONFIRMATION_STATEMENT_PAID, companyNumber);
        ApiLogger.info(String.format(CALLING_INTERNAL_API_CLIENT_GET, paymentsUrl));

        try {
            var internalApiClient = apiClientService.getInternalApiClient();
            return internalApiClient
                    .privateCompanyResourceHandler()
                    .getConfirmationStatementPayment(paymentsUrl, paymentPeriodMadeUpToDate)
                    .execute()
                    .getData()
                    .isPaid();
        } catch (ApiErrorResponseException aere) {
            throw new ServiceException(String.format(ORACLE_QUERY_API_STATUS_MESSAGE, aere.getStatusCode(),
                    companyNumber + " with due date " + paymentPeriodMadeUpToDate), aere);
        } catch (URIValidationException urive) {
            throw new ServiceException(String.format(EXCEPTION_INVALID_URI, paymentsUrl), urive);
        } catch (Exception e) {
            throw new ServiceException(String.format(GENERAL_EXCEPTION_API_CALL, paymentsUrl), e);
        }
    }

    public RegisteredEmailAddressJson getRegisteredEmailAddress(String companyNumber) throws ServiceException, RegisteredEmailNotFoundException {
        var registeredEmailAddressUrl = String.format(API_PATH_REGISTERED_EMAIL_ADDRESS, companyNumber);
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
