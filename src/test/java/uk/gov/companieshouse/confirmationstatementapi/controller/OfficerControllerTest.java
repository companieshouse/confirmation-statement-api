package uk.gov.companieshouse.confirmationstatementapi.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import uk.gov.companieshouse.api.model.company.ActiveOfficerDetailsJson;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.service.OfficerService;

@ExtendWith(MockitoExtension.class)
class OfficerControllerTest {

    @Mock
    private Transaction transaction;

    @Mock
    private OfficerService officerService;

    @InjectMocks
    private OfficerController officerController;

    private static final String ERIC_REQUEST_ID = "XaBcDeF12345";
    private static final String TRANSACTION_ID = "GFEDCBA";
    private static final String CONFIRMATION_STATEMENT_ID = "abc123";

    @Test
    void testGetActiveOfficerDetails() throws ServiceException {
        when(officerService.getActiveOfficerDetails(transaction.getCompanyNumber())).thenReturn(new ActiveOfficerDetailsJson());
        var response = officerController.getActiveOfficersDetails(transaction, TRANSACTION_ID, CONFIRMATION_STATEMENT_ID, ERIC_REQUEST_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetActiveOfficerDetailsServiceException() throws ServiceException {
        when(officerService.getActiveOfficerDetails(transaction.getCompanyNumber())).thenThrow(ServiceException.class);
        var response = officerController.getActiveOfficersDetails(transaction, TRANSACTION_ID, CONFIRMATION_STATEMENT_ID, ERIC_REQUEST_ID);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testGetListActiveOfficersDetails() throws ServiceException {
        var officers = Arrays.asList(new ActiveOfficerDetailsJson(), new ActiveOfficerDetailsJson());
        when(officerService.getListActiveOfficersDetails(transaction.getCompanyNumber())).thenReturn(officers);
        var response = officerController.getListActiveOfficersDetails(transaction, TRANSACTION_ID, CONFIRMATION_STATEMENT_ID, ERIC_REQUEST_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetListActiveOfficersDetailsServiceException() throws ServiceException {
        when(officerService.getListActiveOfficersDetails(transaction.getCompanyNumber())).thenThrow(ServiceException.class);
        var response = officerController.getListActiveOfficersDetails(transaction, TRANSACTION_ID, CONFIRMATION_STATEMENT_ID, ERIC_REQUEST_ID);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

}