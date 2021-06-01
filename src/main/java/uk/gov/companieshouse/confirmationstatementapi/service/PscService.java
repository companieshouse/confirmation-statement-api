package uk.gov.companieshouse.confirmationstatementapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.psc.PscsApi;
import uk.gov.companieshouse.confirmationstatementapi.client.ApiClientService;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;

@Service
public class PscService {

    private final ApiClientService apiClientService;

    @Autowired
    public PscService(ApiClientService apiClientService) {
        this.apiClientService = apiClientService;
    }

    public PscsApi getPscs(String companyNumber) throws ServiceException {
        try {
            var uri = "/company/" + companyNumber + "/persons-with-significant-control";
            return apiClientService.getApiKeyAuthenticatedClient().pscs().list(uri).execute().getData();
        } catch (URIValidationException | ApiErrorResponseException e) {
            throw new ServiceException("Error Retrieving Persons of significant control", e);
        }
    }
}
