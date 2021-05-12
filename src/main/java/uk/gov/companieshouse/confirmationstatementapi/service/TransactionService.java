package uk.gov.companieshouse.confirmationstatementapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.confirmationstatementapi.ConfirmationStatementApiApplication;
import uk.gov.companieshouse.confirmationstatementapi.client.ApiClientService;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.io.IOException;

@Service
public class TransactionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfirmationStatementApiApplication.APP_NAME);

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
            LOGGER.error(e);
            throw new ServiceException("Error Retrieving Transaction", e);
        }
    }
}
