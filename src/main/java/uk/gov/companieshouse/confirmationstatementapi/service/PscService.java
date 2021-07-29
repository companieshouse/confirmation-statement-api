package uk.gov.companieshouse.confirmationstatementapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.psc.PscsApi;
import uk.gov.companieshouse.confirmationstatementapi.client.ApiClientService;
import uk.gov.companieshouse.confirmationstatementapi.client.OracleQueryClient;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.model.PersonOfSignificantControl;
import uk.gov.companieshouse.confirmationstatementapi.model.json.PersonOfSignificantControlJson;

import java.util.Arrays;
import java.util.List;

@Service
public class PscService {

    private final ApiClientService apiClientService;
    private final OracleQueryClient oracleQueryClient;

    @Autowired
    public PscService(ApiClientService apiClientService,
                      OracleQueryClient oracleQueryClient) {
        this.apiClientService = apiClientService;
        this.oracleQueryClient = oracleQueryClient;
    }

    public PscsApi getPSCsFromCHS(String companyNumber) throws ServiceException {
        try {
            var uri = "/company/" + companyNumber + "/persons-with-significant-control";
            return apiClientService.getApiKeyAuthenticatedClient().pscs().list(uri).execute().getData();
        } catch (URIValidationException e) {
            throw new ServiceException("Error Retrieving Persons of significant control", e);
        } catch (ApiErrorResponseException e) {
            if (HttpStatus.NOT_FOUND.value() == e.getStatusCode()) {
                return new PscsApi();
            }
            throw new ServiceException("Error Retrieving Persons of significant control", e);
        }
    }

    public List<PersonOfSignificantControlJson> getPSCsFromOracle(String companyNumber) throws ServiceException {
        List<PersonOfSignificantControl> pscs = oracleQueryClient.getPersonsOfSignificantControl(companyNumber);
        // TODO MAPPING - create mapper to do the work
        var pscJson = new PersonOfSignificantControlJson();
        return Arrays.asList(pscJson);
    }
}
