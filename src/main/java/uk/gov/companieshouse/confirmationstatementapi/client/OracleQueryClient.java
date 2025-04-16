package uk.gov.companieshouse.confirmationstatementapi.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import uk.gov.companieshouse.api.error.ApiErrorResponseException;
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
import java.util.Objects;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Component
public class OracleQueryClient {

    private static final String CALLING_ORACLE_QUERY_API_URL_GET = "Calling Oracle Query API URL: %s";
    private static final String CALLING_INTERNAL_API_CLIENT_GET = "Calling Oracle Query API URL '%s' via Internal Api Client";
    private static final String ORACLE_QUERY_API_STATUS_MESSAGE = "Oracle query api returned with status = %s, companyNumber = %s";
    private static final String REGISTERED_EMAIL_ADDRESS_NOT_FOUND = "Registered Email Address not found";
    private static final String STATEMENT_OF_CAPITAL_NOT_FOUND = "Statement Of Capital not found";
    private static final String GENERAL_EXCEPTION_API_CALL = "Error occurred while calling '%s'";

    private final String apiPathCompanyDetails;
    private final String apiPathCompanyTradedStatus;
    private final String apiPathCompanyShareholdersCount;
    private final String apiPathCompanyStatementOfCapital;
    private final String apiPathCompanyDirectorActive;
    private final String apiPathCompanyOfficersActive;
    private final String apiPathCompanyRegisterLocations;
    private final String apiPathShareHolders;
    private final String apiPathCompanyCorporateBodyAppointmentsPsc;
    private final String apiPathCompanyConfirmationStatementPaid;
    private final String apiPathRegisteredEmailAddress;

    private final ApiClientService apiClientService;

    @Autowired
    public OracleQueryClient(
            ApiClientService apiClientService,
            @Value("${api.path.company.details}") String apiPathCompanyDetails,
            @Value("${api.path.company.traded.status}") String apiPathCompanyTradedStatus,
            @Value("${api.path.company.shareholders.count}") String apiPathCompanyShareholdersCount,
            @Value("${api.path.company.statement.of.capital}") String apiPathCompanyStatementOfCapital,
            @Value("${api.path.company.director.active}") String apiPathCompanyDirectorActive,
            @Value("${api.path.company.officers.active}") String apiPathCompanyOfficersActive,
            @Value("${api.path.company.register.locations}") String apiPathCompanyRegisterLocations,
            @Value("${api.path.share.holders}") String apiPathShareHolders,
            @Value("${api.path.company.corporate.body.appointments.psc}") String apiPathCompanyCorporateBodyAppointmentsPsc,
            @Value("${api.path.company.confirmation.statement.paid}") String apiPathCompanyConfirmationStatementPaid,
            @Value("${api.path.registered.email.address}") String apiPathRegisteredEmailAddress) {
        this.apiClientService = Objects.requireNonNull(apiClientService, "apiClientService must not be null");
        this.apiPathCompanyDetails = Objects.requireNonNull(apiPathCompanyDetails, "apiPathCompanyDetails must not be null");
        this.apiPathCompanyTradedStatus = Objects.requireNonNull(apiPathCompanyTradedStatus, "apiPathCompanyTradedStatus must not be null");
        this.apiPathCompanyShareholdersCount = Objects.requireNonNull(apiPathCompanyShareholdersCount, "apiPathCompanyShareholdersCount must not be null");
        this.apiPathCompanyStatementOfCapital = Objects.requireNonNull(apiPathCompanyStatementOfCapital, "apiPathCompanyStatementOfCapital must not be null");
        this.apiPathCompanyDirectorActive = Objects.requireNonNull(apiPathCompanyDirectorActive, "apiPathCompanyDirectorActive must not be null");
        this.apiPathCompanyOfficersActive = Objects.requireNonNull(apiPathCompanyOfficersActive, "apiPathCompanyOfficersActive must not be null");
        this.apiPathCompanyRegisterLocations = Objects.requireNonNull(apiPathCompanyRegisterLocations, "apiPathCompanyRegisterLocations must not be null");
        this.apiPathShareHolders = Objects.requireNonNull(apiPathShareHolders, "apiPathShareHolders must not be null");
        this.apiPathCompanyCorporateBodyAppointmentsPsc = Objects.requireNonNull(apiPathCompanyCorporateBodyAppointmentsPsc, "apiPathCompanyCorporateBodyAppointmentsPsc must not be null");
        this.apiPathCompanyConfirmationStatementPaid = Objects.requireNonNull(apiPathCompanyConfirmationStatementPaid, "apiPathCompanyConfirmationStatementPaid must not be null");
        this.apiPathRegisteredEmailAddress = Objects.requireNonNull(apiPathRegisteredEmailAddress, "apiPathRegisteredEmailAddress must not be null");
    }

    private String buildUrl(String companyNumber, String component) {
        return String.format("/%s/%s/%s", apiPathCompanyDetails, companyNumber, component);
    }

    public Long getCompanyTradedStatus(String companyNumber) throws ServiceException {
        var url = buildUrl(companyNumber, apiPathCompanyTradedStatus);
        ApiLogger.info(String.format(CALLING_INTERNAL_API_CLIENT_GET, url));
        try {
            var internalApiClient = apiClientService.getInternalApiClient();
            return internalApiClient
                    .privateCompanyResourceHandler()
                    .getCompanyTradedStatus(url)
                    .execute()
                    .getData();
        } catch (Exception e) {
            throw new ServiceException(String.format(ORACLE_QUERY_API_STATUS_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR, companyNumber), e);
        }
    }

    public Integer getShareholderCount(String companyNumber) throws ServiceException {
        var url = buildUrl(companyNumber, apiPathCompanyShareholdersCount);
        ApiLogger.info(String.format(CALLING_INTERNAL_API_CLIENT_GET, url));

        try {
            var internalApiClient = apiClientService.getInternalApiClient();
            return internalApiClient
                    .privateCompanyResourceHandler()
                    .getCompanyShareHoldersCount(url)
                    .execute()
                    .getData();
        } catch (Exception e) {
            throw new ServiceException(String.format(ORACLE_QUERY_API_STATUS_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR, companyNumber), e);
        }
    }

    public StatementOfCapitalJson getStatementOfCapitalData(String companyNumber) throws ServiceException, StatementOfCapitalNotFoundException {
        var url = buildUrl(companyNumber, apiPathCompanyStatementOfCapital);
        ApiLogger.info(String.format(CALLING_INTERNAL_API_CLIENT_GET, url));

        try {
            var internalApiClient = apiClientService.getInternalApiClient();
            var data = internalApiClient
                    .privateCompanyResourceHandler()
                    .getStatementOfCapitalData(url)
                    .execute()
                    .getData();
            if (data == null) {
                return new StatementOfCapitalJson();
            }
            return data;

        } catch (ApiErrorResponseException aere) {
            if (aere.getStatusCode() == NOT_FOUND.value()) {
                throw new StatementOfCapitalNotFoundException(STATEMENT_OF_CAPITAL_NOT_FOUND);
            } else {
                throw new ServiceException(String.format(ORACLE_QUERY_API_STATUS_MESSAGE, aere.getStatusCode(), companyNumber), aere);
            }
        } catch (Exception e) {
            throw new ServiceException(String.format(ORACLE_QUERY_API_STATUS_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR, companyNumber), e);
        }
    }

    public ActiveOfficerDetailsJson getActiveDirectorDetails(String companyNumber) throws ServiceException {
        var url = buildUrl(companyNumber, apiPathCompanyDirectorActive);
        ApiLogger.info(String.format(CALLING_ORACLE_QUERY_API_URL_GET, url));

        try {
            var internalApiClient = apiClientService.getInternalApiClient();
            var director = internalApiClient
                    .privateCompanyResourceHandler()
                    .getActiveDirector(url)
                    .execute()
                    .getData();
            return director != null ? director : new ActiveOfficerDetailsJson();

        } catch (Exception e) {
            throw new ServiceException(String.format(GENERAL_EXCEPTION_API_CALL, url), e);
        }
    }

    public List<ActiveOfficerDetailsJson> getActiveOfficersDetails(String companyNumber) throws ServiceException {
        var url = buildUrl(companyNumber, apiPathCompanyOfficersActive);
        ApiLogger.info(String.format(CALLING_ORACLE_QUERY_API_URL_GET, url));

        try {
            var internalApiClient = apiClientService.getInternalApiClient();
            ActiveOfficerDetailsJson[] officers = internalApiClient
                    .privateCompanyResourceHandler()
                    .getActiveOfficers(url)
                    .execute()
                    .getData();
            return officers != null ? Arrays.asList(officers) : new ArrayList<>();

        } catch (Exception e) {
            throw new ServiceException(String.format(GENERAL_EXCEPTION_API_CALL, url), e);
        }
    }

    public List<PersonOfSignificantControl> getPersonsOfSignificantControl(String companyNumber) throws ServiceException {
        var url = buildUrl(companyNumber, apiPathCompanyCorporateBodyAppointmentsPsc);
        ApiLogger.info(String.format(CALLING_ORACLE_QUERY_API_URL_GET, url));

        try {
            var internalApiClient = apiClientService.getInternalApiClient();
            PersonOfSignificantControl[] pscs = internalApiClient
                    .privateCompanyResourceHandler()
                    .getPersonsOfSignificantControl(url)
                    .execute()
                    .getData();
            return pscs != null ? Arrays.asList(pscs) : List.of();

        } catch (Exception e) {
            throw new ServiceException(String.format(GENERAL_EXCEPTION_API_CALL, url), e);
        }
    }

    public List<RegisterLocationJson> getRegisterLocations(String companyNumber) throws ServiceException {
        var url = buildUrl(companyNumber, apiPathCompanyRegisterLocations);
        ApiLogger.info(String.format(CALLING_ORACLE_QUERY_API_URL_GET, url));

        try {
            var internalApiClient = apiClientService.getInternalApiClient();
            RegisterLocationJson[] regLocs = internalApiClient
                    .privateCompanyResourceHandler()
                    .getRegisterLocations(url)
                    .execute()
                    .getData();
            return regLocs != null && regLocs.length > 0 ? Arrays.asList(regLocs) : List.of();
        } catch (Exception e) {
            throw new ServiceException(String.format(GENERAL_EXCEPTION_API_CALL, url), e);
        }
    }

    public List<ShareholderJson> getShareholders(String companyNumber) throws ServiceException {
        ShareholderJson[] shareHolders;
        var shareholdersUrl = buildUrl(companyNumber, apiPathShareHolders);
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

        return ((null != shareHolders && shareHolders.length > 0) ? Arrays.asList(shareHolders) : List.of());
    }

    public boolean isConfirmationStatementPaid(String companyNumber, String paymentPeriodMadeUpToDate) throws ServiceException {
        var paymentsUrl = buildUrl(companyNumber, apiPathCompanyConfirmationStatementPaid);
        ApiLogger.info(String.format(CALLING_INTERNAL_API_CLIENT_GET, paymentsUrl));

        boolean confirmationStatementPaid;

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
        var registeredEmailAddressUrl = buildUrl(companyNumber, apiPathRegisteredEmailAddress);
        ApiLogger.info(String.format(CALLING_INTERNAL_API_CLIENT_GET, registeredEmailAddressUrl));

        try {
            var internalApiClient = apiClientService.getInternalApiClient();
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
