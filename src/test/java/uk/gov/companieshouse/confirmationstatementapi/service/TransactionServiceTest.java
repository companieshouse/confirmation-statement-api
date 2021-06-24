package uk.gov.companieshouse.confirmationstatementapi.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.handler.privatetransaction.PrivateTransactionResourceHandler;
import uk.gov.companieshouse.api.handler.privatetransaction.request.PrivateTransactionPatch;
import uk.gov.companieshouse.api.handler.transaction.TransactionsResourceHandler;
import uk.gov.companieshouse.api.handler.transaction.request.TransactionsGet;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.confirmationstatementapi.client.ApiClientService;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    private static final String TRANSACTION_ID = "12345678";
    private static final String PASSTHROUGH_HEADER = "passthrough";

    @Mock
    private ApiClientService apiClientService;

    @Mock
    private ApiClient apiClient;

    @Mock
    private TransactionsResourceHandler transactionsResourceHandler;

    @Mock
    private TransactionsGet transactionsGet;

    @Mock
    private InternalApiClient internalApiClient;

    @Mock
    private PrivateTransactionResourceHandler privateTransactionResourceHandler;

    @Mock
    private PrivateTransactionPatch privateTransactionPatch;

    @Mock
    private ApiResponse<Transaction> apiResponse;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void getTransaction() throws ServiceException, IOException, URIValidationException {
        Transaction transaction = new Transaction();
        transaction.setId(TRANSACTION_ID);

        when(apiClientService.getOauthAuthenticatedClient(PASSTHROUGH_HEADER)).thenReturn(apiClient);
        when(apiClient.transactions()).thenReturn(transactionsResourceHandler);
        when(transactionsResourceHandler.get("/transactions/" + TRANSACTION_ID)).thenReturn(transactionsGet);
        when(transactionsGet.execute()).thenReturn(apiResponse);
        when(apiResponse.getData()).thenReturn(transaction);

        var response = transactionService.getTransaction(TRANSACTION_ID, PASSTHROUGH_HEADER);

        assertEquals(transaction, response);

    }

    @Test
    void getTransactionURIValidationException() throws IOException, URIValidationException {
        when(apiClientService.getOauthAuthenticatedClient(PASSTHROUGH_HEADER)).thenReturn(apiClient);
        when(apiClient.transactions()).thenReturn(transactionsResourceHandler);
        when(transactionsResourceHandler.get("/transactions/" + TRANSACTION_ID)).thenReturn(transactionsGet);
        when(transactionsGet.execute()).thenThrow(new URIValidationException("ERROR"));

        assertThrows(ServiceException.class, () -> {
            transactionService.getTransaction(TRANSACTION_ID, PASSTHROUGH_HEADER);
        });
    }

    @Test
    void getTransactionProfileApiErrorResponse() throws IOException, URIValidationException {
        when(apiClientService.getOauthAuthenticatedClient(PASSTHROUGH_HEADER)).thenReturn(apiClient);
        when(apiClient.transactions()).thenReturn(transactionsResourceHandler);
        when(transactionsResourceHandler.get("/transactions/" + TRANSACTION_ID)).thenReturn(transactionsGet);
        when(transactionsGet.execute()).thenThrow(ApiErrorResponseException.fromIOException(new IOException("ERROR")));

        assertThrows(ServiceException.class, () -> {
            transactionService.getTransaction(TRANSACTION_ID, PASSTHROUGH_HEADER);
        });
    }

    @Test
    void updateTransaction() throws IOException, URIValidationException {
        Transaction transaction = new Transaction();
        transaction.setId(TRANSACTION_ID);

        when(apiClientService.getInternalOauthAuthenticatedClient(PASSTHROUGH_HEADER)).thenReturn(internalApiClient);
        when(internalApiClient.privateTransaction()).thenReturn(privateTransactionResourceHandler);
        when(privateTransactionResourceHandler.patch("/private/transactions/" + TRANSACTION_ID, transaction)).thenReturn(privateTransactionPatch);
        when(privateTransactionPatch.execute()).thenReturn(new ApiResponse<>(204, null));

        assertDoesNotThrow(() -> transactionService.updateTransaction(transaction, PASSTHROUGH_HEADER));

        verify(privateTransactionPatch, times(1)).execute();

    }

    @Test
    void updateTransactionApiErrorResponse() throws IOException, URIValidationException {
        Transaction transaction = new Transaction();
        transaction.setId(TRANSACTION_ID);

        when(apiClientService.getInternalOauthAuthenticatedClient(PASSTHROUGH_HEADER)).thenReturn(internalApiClient);
        when(internalApiClient.privateTransaction()).thenReturn(privateTransactionResourceHandler);
        when(privateTransactionResourceHandler.patch("/private/transactions/" + TRANSACTION_ID, transaction)).thenReturn(privateTransactionPatch);
        when(privateTransactionPatch.execute()).thenThrow(ApiErrorResponseException.fromIOException(new IOException("ERROR")));

        assertThrows(ServiceException.class, () -> transactionService.updateTransaction(transaction, PASSTHROUGH_HEADER));
    }

    @Test
    void updateTransactionUriValidationError() throws IOException, URIValidationException {
        Transaction transaction = new Transaction();
        transaction.setId(TRANSACTION_ID);

        when(apiClientService.getInternalOauthAuthenticatedClient(PASSTHROUGH_HEADER)).thenReturn(internalApiClient);
        when(internalApiClient.privateTransaction()).thenReturn(privateTransactionResourceHandler);
        when(privateTransactionResourceHandler.patch("/private/transactions/" + TRANSACTION_ID, transaction)).thenReturn(privateTransactionPatch);
        when(privateTransactionPatch.execute()).thenThrow(new URIValidationException("ERROR"));

        assertThrows(ServiceException.class, () -> transactionService.updateTransaction(transaction, PASSTHROUGH_HEADER));
    }
}