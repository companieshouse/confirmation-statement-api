package uk.gov.companieshouse.confirmationstatementapi.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.confirmationstatementapi.exception.ActiveOfficerNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.api.model.company.ActiveOfficerDetailsJson;
import uk.gov.companieshouse.confirmationstatementapi.service.OfficerService;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

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

    @Test
    void testGetActiveOfficerDetails() throws ActiveOfficerNotFoundException, ServiceException {
        when(officerService.getActiveOfficerDetails(transaction.getCompanyNumber())).thenReturn(new ActiveOfficerDetailsJson());
        var response = officerController.getActiveOfficersDetails(transaction, TRANSACTION_ID, ERIC_REQUEST_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetActiveOfficerDetailsServiceException() throws ActiveOfficerNotFoundException, ServiceException {
        when(officerService.getActiveOfficerDetails(transaction.getCompanyNumber())).thenThrow(ServiceException.class);
        var response = officerController.getActiveOfficersDetails(transaction, TRANSACTION_ID, ERIC_REQUEST_ID);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testGetActiveOfficerDetailsOfficerNotFoundException() throws ActiveOfficerNotFoundException, ServiceException {
        when(officerService.getActiveOfficerDetails(transaction.getCompanyNumber())).thenThrow(ActiveOfficerNotFoundException.class);
        var response = officerController.getActiveOfficersDetails(transaction, TRANSACTION_ID, ERIC_REQUEST_ID);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetListActiveOfficersDetails() throws ServiceException {
        var officers = Arrays.asList(new ActiveOfficerDetailsJson(), new ActiveOfficerDetailsJson());
        when(officerService.getListActiveOfficersDetails(transaction.getCompanyNumber())).thenReturn(officers);
        var response = officerController.getListActiveOfficersDetails(transaction, TRANSACTION_ID, ERIC_REQUEST_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetListActiveOfficersDetailsServiceException() throws ServiceException {
        when(officerService.getListActiveOfficersDetails(transaction.getCompanyNumber())).thenThrow(ServiceException.class);
        var response = officerController.getListActiveOfficersDetails(transaction, TRANSACTION_ID, ERIC_REQUEST_ID);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testGetListActiveOfficersDetailsOfficerNotFoundException() throws ServiceException {
        when(officerService.getListActiveOfficersDetails(transaction.getCompanyNumber())).thenThrow(ServiceException.class);
        var response = officerController.getListActiveOfficersDetails(transaction, TRANSACTION_ID, ERIC_REQUEST_ID);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}