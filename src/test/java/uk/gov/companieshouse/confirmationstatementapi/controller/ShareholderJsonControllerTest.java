package uk.gov.companieshouse.confirmationstatementapi.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.model.json.shareholder.ShareholderJson;
import uk.gov.companieshouse.confirmationstatementapi.service.ShareholderService;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShareholderJsonControllerTest {

    private static final String ERIC_REQUEST_ID = "XaBcDeF12345";
    private static final String TRANSACTION_ID = "GFEDCBA";
    private static final String SUBMISSION_ID = "ABCDEFG";

    @Mock
    private Transaction transaction;

    @Mock
    private ShareholderService shareholderService;

    @InjectMocks
    private ShareholderController shareholderController;

    @Test
    void testGetShareholderOKResponse() throws ServiceException {
        var shareholder = Arrays.asList(new ShareholderJson(), new ShareholderJson());
        when(shareholderService.getShareholders(transaction.getCompanyNumber())).thenReturn(shareholder);
        var response = shareholderController.getShareholders(transaction, TRANSACTION_ID, ERIC_REQUEST_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(shareholder, response.getBody());
    }

    @Test
    void testGetShareholderServiceException() throws ServiceException {
        when(shareholderService.getShareholders(transaction.getCompanyNumber())).thenThrow(new ServiceException("Internal Server Error"));
        var response = shareholderController.getShareholders(transaction, TRANSACTION_ID, ERIC_REQUEST_ID);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testGetShareholderUncheckedException() throws ServiceException {
        var runtimeException = new RuntimeException("Runtime Error");
        when(shareholderService.getShareholders(transaction.getCompanyNumber())).thenThrow(runtimeException);
        var thrown = assertThrows(Exception.class, () -> shareholderController.getShareholders(transaction, TRANSACTION_ID, ERIC_REQUEST_ID));

        assertEquals(runtimeException, thrown);
    }
}
