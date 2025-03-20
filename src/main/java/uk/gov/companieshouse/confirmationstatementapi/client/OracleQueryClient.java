package uk.gov.companieshouse.confirmationstatementapi.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.company.ActiveOfficerDetailsJson;
import uk.gov.companieshouse.api.model.company.PersonOfSignificantControl;
import uk.gov.companieshouse.api.model.company.RegisteredEmailAddressJson;
import uk.gov.companieshouse.api.model.company.StatementOfCapitalJson;
import uk.gov.companieshouse.api.model.shareholder.ShareholderJson;
import uk.gov.companieshouse.confirmationstatementapi.exception.RegisteredEmailNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.exception.StatementOfCapitalNotFoundException;
import uk.gov.companieshouse.api.model.company.RegisterLocationJson;
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
    private static final String API_PATH_COMPANY_OFFICERS_ACTIVE = "/company/%s/officers/active";
    private static final String API_PATH_COMPANY_REGISTER_LOCATIONS = "/company/%s/register/location";
    private static final String API_PATH_SHARE_HOLDERS = "/company/%s/shareholders";
    private static final String API_PATH_COMPANY_CORPORATE_BODY_APPOINTMENTS_PSC = "/company/%s/corporate-body-appointments/persons-of-significant-control";
    private static final String API_PATH_COMPANY_CONFIRMATION_STATEMENT_PAID = "/company/%s/confirmation-statement/paid";
    private static final String API_PATH_REGISTERED_EMAIL_ADDRESS = "/company/%s/registered-email-address";
    private static final String EXCEPTION_INVALID_URI = "Invalid URI: %s";
    private static final String GENERAL_EXCEPTION_API_CALL = "Error occurred while calling '%s'";


    @Autowired
    private ApiClientService apiClientService;

    @Value("${ORACLE_QUERY_API_URL}")
    private String oracleQueryApiUrl;

    @Value("${FEATURE_FLAG_FIVE_OR_LESS_OFFICERS_JOURNEY_21102021:false}")
    private boolean multipleOfficerJourneyFeatureFlag;

    public Long getCompanyTradedStatus(String companyNumber) throws ServiceException {
        String url = String.format(API_PATH_COMPANY_TRADED_STATUS, companyNumber);
        ApiLogger.info(String.format(CALLING_INTERNAL_API_CLIENT_GET, url));

        try {
            var client = apiClientService.getInternalApiClient();
            return client.privateCompanyResourceHandler().getCompanyTradedStatus(url).execute().getData();
        } catch (Exception e) {
            throw new ServiceException(String.format(ORACLE_QUERY_API_STATUS_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR, companyNumber), e);
        }
    }

    public Integer getShareholderCount(String companyNumber) throws ServiceException {
        String url = String.format(API_PATH_COMPANY_SHAREHOLDERS_COUNT, companyNumber);
        ApiLogger.info(String.format(CALLING_INTERNAL_API_CLIENT_GET, url));

        try {
            var client = apiClientService.getInternalApiClient();
            return client.privateCompanyResourceHandler().getCompanyShareHoldersCount(url).execute().getData();
        } catch (Exception e) {
            throw new ServiceException(String.format(ORACLE_QUERY_API_STATUS_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR, companyNumber), e);
        }
    }

    public StatementOfCapitalJson getStatementOfCapitalData(String companyNumber) throws ServiceException, StatementOfCapitalNotFoundException {
        String url = String.format(API_PATH_COMPANY_STATEMENT_OF_CAPITAL, companyNumber);
        ApiLogger.info(String.format(CALLING_INTERNAL_API_CLIENT_GET, url));

        try {
            var client = apiClientService.getInternalApiClient();
            var data = client.privateCompanyResourceHandler().getStatementOfCapitalData(url).execute().getData();
            if (data == null) {
                throw new StatementOfCapitalNotFoundException(STATEMENT_OF_CAPITAL_NOT_FOUND);
            }
            return data;
        } catch (ApiErrorResponseException e) {
            if (e.getStatusCode() == NOT_FOUND.value()) {
                throw new StatementOfCapitalNotFoundException(STATEMENT_OF_CAPITAL_NOT_FOUND);
            }
            throw new ServiceException(String.format(ORACLE_QUERY_API_STATUS_MESSAGE, e.getStatusCode(), companyNumber), e);
        } catch (Exception e) {
            throw new ServiceException(String.format(ORACLE_QUERY_API_STATUS_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR, companyNumber), e);
        }
    }

    public ActiveOfficerDetailsJson getActiveDirectorDetails(String companyNumber) throws ServiceException {
        String url = String.format(API_PATH_COMPANY_DIRECTOR_ACTIVE, companyNumber);
        ApiLogger.info(String.format(CALLING_ORACLE_QUERY_API_URL_GET, url));

        try {
            var client = apiClientService.getInternalApiClient();
            var director = client.privateCompanyResourceHandler().getActiveDirector(url).execute().getData();
            return director != null ? director : new ActiveOfficerDetailsJson();

        } catch (Exception e) {
            throw new ServiceException(String.format(GENERAL_EXCEPTION_API_CALL, url), e);
        }
    }

    public List<ActiveOfficerDetailsJson> getActiveOfficersDetails(String companyNumber) throws ServiceException {
        String url = String.format(API_PATH_COMPANY_OFFICERS_ACTIVE, companyNumber);
        ApiLogger.info(String.format(CALLING_ORACLE_QUERY_API_URL_GET, url));

        try {
            var client = apiClientService.getInternalApiClient();
            ActiveOfficerDetailsJson[] officers = client.privateCompanyResourceHandler().getActiveOfficers(url).execute().getData();
            return officers != null ? Arrays.asList(officers) : new ArrayList<>();

        } catch (Exception e) {
            throw new ServiceException(String.format(GENERAL_EXCEPTION_API_CALL, url), e);
        }
    }

    public List<PersonOfSignificantControl> getPersonsOfSignificantControl(String companyNumber) throws ServiceException {
        String url = String.format(API_PATH_COMPANY_CORPORATE_BODY_APPOINTMENTS_PSC, companyNumber);
        ApiLogger.info(String.format(CALLING_ORACLE_QUERY_API_URL_GET, url));

        try {
            var client = apiClientService.getInternalApiClient();
            PersonOfSignificantControl[] pscs = client.privateCompanyResourceHandler().getPersonsOfSignificantControl(url).execute().getData();
            return pscs != null ? Arrays.asList(pscs) : List.of();

        } catch (Exception e) {
            throw new ServiceException(String.format(GENERAL_EXCEPTION_API_CALL, url), e);
        }
    }

    public List<RegisterLocationJson> getRegisterLocations(String companyNumber) throws ServiceException {
        String url = String.format(API_PATH_COMPANY_REGISTER_LOCATIONS, companyNumber);
        ApiLogger.info(String.format(CALLING_ORACLE_QUERY_API_URL_GET, url));

        try {
            var client = apiClientService.getInternalApiClient();
            RegisterLocationJson[] regLocs = client.privateCompanyResourceHandler().getRegisterLocations(url).execute().getData();
            return regLocs != null && regLocs.length > 0 ? Arrays.asList(regLocs) : List.of();
        } catch (Exception e) {
            throw new ServiceException(String.format(GENERAL_EXCEPTION_API_CALL, url), e);
        }
    }

    public List<ShareholderJson> getShareholders(String companyNumber) throws ServiceException {
        ShareholderJson[] shareHolders;
        var shareholdersUrl = String.format(API_PATH_SHARE_HOLDERS, companyNumber);
        ApiLogger.info(String.format(CALLING_ORACLE_QUERY_API_URL_GET, shareholdersUrl));

        try {
            var internalApiClient = apiClientService.getInternalApiClient();
            shareHolders = internalApiClient
                    .privateCompanyResourceHandler()
                    .getCompanyShareHolders(shareholdersUrl)
                    .execute()
                    .getData();
        } catch (Exception e) {
            throw new ServiceException(String.format(GENERAL_EXCEPTION_API_CALL, shareholdersUrl), e);
        }

        return ((null == shareHolders || shareHolders.length == 0) ? List.of() : Arrays.asList(shareHolders));
    }

    public boolean isConfirmationStatementPaid(String companyNumber, String paymentPeriodMadeUpToDate) throws ServiceException {
        var paymentsUrl = String.format(API_PATH_COMPANY_CONFIRMATION_STATEMENT_PAID, companyNumber);
        ApiLogger.info(String.format(CALLING_INTERNAL_API_CLIENT_GET, paymentsUrl));

        boolean confirmationStatementPaid = false;

        try {
            var internalApiClient = apiClientService.getInternalApiClient();
            confirmationStatementPaid = internalApiClient
                    .privateCompanyResourceHandler()
                    .getConfirmationStatementPayment(paymentsUrl, paymentPeriodMadeUpToDate)
                    .execute()
                    .getData()
                    .isPaid();
        } catch (ApiErrorResponseException aere) {
            throw new ServiceException(String.format(ORACLE_QUERY_API_STATUS_MESSAGE + " with due date %s", aere.getStatusCode(), companyNumber, paymentPeriodMadeUpToDate));
        } catch (Exception e) {
            throw new ServiceException(String.format(GENERAL_EXCEPTION_API_CALL, paymentsUrl), e);
        }

        return confirmationStatementPaid;
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
