package uk.gov.companieshouse.confirmationstatementapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.officers.OfficersApi;
import uk.gov.companieshouse.confirmationstatementapi.ConfirmationStatementApiApplication;
import uk.gov.companieshouse.confirmationstatementapi.client.ApiClientService;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
public class OfficerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfirmationStatementApiApplication.APP_NAME);

    private final ApiClientService apiClientService;

    @Autowired
    public OfficerService(ApiClientService apiClientService) {
        this.apiClientService = apiClientService;
    }

    public OfficersApi getOfficers(String companyNumber) throws ServiceException {
        try {
            var uri = "/company/" + companyNumber + "/officers";
            return apiClientService.getApiKeyAuthenticatedClient().officers().list(uri).execute().getData();
        } catch (URIValidationException | ApiErrorResponseException e) {
            LOGGER.error(e);
            throw new ServiceException("Error Retrieving Officers", e);
        }
    }
}
