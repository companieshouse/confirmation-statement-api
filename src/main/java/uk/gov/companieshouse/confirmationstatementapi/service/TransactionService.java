package uk.gov.companieshouse.confirmationstatementapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.confirmationstatementapi.client.ApiKeyClient;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
public class TransactionService {

    private static final Logger LOGGER = LoggerFactory.getLogger("Company-Profile-Service");

    private ApiKeyClient apiKeyClient;

    @Autowired
    public TransactionService(ApiKeyClient apiKeyClient) {
        this.apiKeyClient = apiKeyClient;
    }

    public Transaction getTransaction(String transaction) throws ServiceException {
        try {
            var uri = "/transactions/" + transaction;
            return apiKeyClient.getApiKeyAuthenticatedClient().transactions().get(uri).execute().getData();
        } catch (URIValidationException | ApiErrorResponseException e) {
            LOGGER.error(e);
            throw new ServiceException("Error Retrieving Transaction");
        }
    }
}
