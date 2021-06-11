package uk.gov.companieshouse.confirmationstatementapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.confirmationstatementapi.client.ApiClientService;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;

import java.io.IOException;

@Service
public class TransactionService {

    private final ApiClientService apiClientService;

    @Autowired
    public TransactionService(ApiClientService apiClientService) {
        this.apiClientService = apiClientService;
    }

    public Transaction getTransaction(String transactionId, String passthroughHeader) throws ServiceException {
        try {
            var uri = "/transactions/" + transactionId;
            return apiClientService.getOauthAuthenticatedClient(passthroughHeader).transactions().get(uri).execute().getData();
        } catch (URIValidationException | IOException e) {
            throw new ServiceException("Error Retrieving Transaction " + transactionId, e);
        }
    }

    public void updateTransaction(Transaction transaction, String passthroughHeader) throws ServiceException {
        try {
            var uri = "/transactions/" + transaction.getId();
            // apiClientService.getOauthAuthenticatedClient(passthroughHeader).transactions().update(uri, transaction).execute();
            // apiClientService.getApiKeyAuthenticatedClient().transactions().update(uri, transaction).execute();
             apiClientService.getInternalOauthAuthenticatedClient(passthroughHeader).transactions().update(uri, transaction).execute();
        } catch (URIValidationException | IOException e) {
            throw new ServiceException("Error Updating Transaction " + transaction.getId(), e);
        }
    }
}
