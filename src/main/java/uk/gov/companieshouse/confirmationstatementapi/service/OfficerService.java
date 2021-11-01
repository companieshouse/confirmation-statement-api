package uk.gov.companieshouse.confirmationstatementapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.officers.OfficersApi;
import uk.gov.companieshouse.confirmationstatementapi.client.ApiClientService;
import uk.gov.companieshouse.confirmationstatementapi.client.OracleQueryClient;
import uk.gov.companieshouse.confirmationstatementapi.exception.ActiveDirectorNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.model.ActiveDirectorDetails;

@Service
public class OfficerService {

    private final ApiClientService apiClientService;
    private final OracleQueryClient oracleQueryClient;

    @Autowired
    public OfficerService(ApiClientService apiClientService, OracleQueryClient oracleQueryClient) {
        this.apiClientService = apiClientService;
        this.oracleQueryClient = oracleQueryClient;
    }

    public OfficersApi getOfficers(String companyNumber) throws ServiceException {
        try {
            var uri = "/company/" + companyNumber + "/officers";
            return apiClientService.getApiKeyAuthenticatedClient().officers().list(uri).execute().getData();
        } catch (URIValidationException e) {
            throw new ServiceException("Error Retrieving Officers", e);
        } catch ( ApiErrorResponseException e) {
            if (HttpStatus.NOT_FOUND.value() == e.getStatusCode()){
                return new OfficersApi();
            }
            throw new ServiceException("Error Retrieving Officers", e);
        }
    }

    public ActiveDirectorDetails getActiveDirectorDetails(String companyNumber) throws ServiceException, ActiveDirectorNotFoundException {
        return oracleQueryClient.getActiveDirectorDetails(companyNumber);
    }
}
