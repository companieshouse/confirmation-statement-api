package uk.gov.companieshouse.confirmationstatementapi.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import uk.gov.companieshouse.api.model.filinggenerator.FilingApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.transaction.TransactionLinks;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.exception.SubmissionNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.service.FilingService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FilingControllerTest {

    private static final String CONFIRMATION_ID = "abc123";
    private static final String TRANSACTION_ID = "def456";
    private static final String ERIC_REQUEST_ID = "XaBcDeF12345";

    @InjectMocks
    private FilingController filingController;

    @Mock
    private FilingService filingService;

    private Transaction transaction;

    @BeforeEach
    void init() {
        transaction = new Transaction();
        var transactionLinks = new TransactionLinks();
        transactionLinks.setPayment("/12345678/payment");
        transaction.setLinks(transactionLinks);
    }

    @Test
    void getFiling() throws SubmissionNotFoundException, ServiceException {
        FilingApi filing = new FilingApi();
        filing.setDescription("12345678");
        when(filingService.generateConfirmationFiling(CONFIRMATION_ID, transaction)).thenReturn(filing);
        var result = filingController.getFiling(transaction, CONFIRMATION_ID, TRANSACTION_ID, ERIC_REQUEST_ID);

        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().length);
        assertEquals("12345678", result.getBody()[0].getDescription());
    }


    @Test
    void getFilingSubmissionNotFound() throws SubmissionNotFoundException, ServiceException {
        when(filingService.generateConfirmationFiling(CONFIRMATION_ID, transaction)).thenThrow(SubmissionNotFoundException.class);
        var result = filingController.getFiling(transaction, CONFIRMATION_ID, TRANSACTION_ID, ERIC_REQUEST_ID);

        assertNull(result.getBody());
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    void getFilingException() throws SubmissionNotFoundException, ServiceException {
        when(filingService.generateConfirmationFiling(CONFIRMATION_ID, transaction)).thenThrow(RuntimeException.class);
        var result = filingController.getFiling(transaction, CONFIRMATION_ID, TRANSACTION_ID, ERIC_REQUEST_ID);

        assertNull(result.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
    }
}