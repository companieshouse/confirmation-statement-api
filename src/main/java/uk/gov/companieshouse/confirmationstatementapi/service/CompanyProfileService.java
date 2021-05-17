package uk.gov.companieshouse.confirmationstatementapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.ConfirmationStatementApiApplication;
import uk.gov.companieshouse.confirmationstatementapi.client.ApiClientService;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
public class CompanyProfileService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfirmationStatementApiApplication.APP_NAME);

    private final ApiClientService apiClientService;

    @Autowired
    public CompanyProfileService(ApiClientService apiClientService) {
        this.apiClientService = apiClientService;
    }

    public CompanyProfileApi getCompanyProfile(String companyNumber) throws ServiceException {
        try {
            var uri = "/company/" + companyNumber;
            return apiClientService.getApiKeyAuthenticatedClient().company().get(uri).execute().getData();
        } catch (URIValidationException | ApiErrorResponseException e) {
            LOGGER.error(e);
            throw new ServiceException("Error Retrieving Company Profile", e);
        }
    }
}