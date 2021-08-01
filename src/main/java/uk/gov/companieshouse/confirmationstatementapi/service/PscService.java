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
import uk.gov.companieshouse.confirmationstatementapi.model.mapping.PscsMapper;
import java.util.List;

@Service
public class PscService {

    private final ApiClientService apiClientService;
    private final OracleQueryClient oracleQueryClient;
    private final PscsMapper pscsMapper;

    @Autowired
    public PscService(ApiClientService apiClientService,
                      OracleQueryClient oracleQueryClient,
                      PscsMapper pscsMapper) {
        this.apiClientService = apiClientService;
        this.oracleQueryClient = oracleQueryClient;
        this.pscsMapper = pscsMapper;
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
        return pscsMapper.mapToPscsApi(pscs);
    }
}
