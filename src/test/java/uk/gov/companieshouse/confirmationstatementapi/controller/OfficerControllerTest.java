package uk.gov.companieshouse.confirmationstatementapi.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.confirmationstatementapi.exception.ActiveDirectorNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.model.ActiveDirectorDetails;
import uk.gov.companieshouse.confirmationstatementapi.service.OfficerService;

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
    void testGetActiveDirectorDetails() throws ActiveDirectorNotFoundException, ServiceException {
        when(officerService.getActiveDirectorDetails(transaction.getCompanyNumber())).thenReturn(new ActiveDirectorDetails());
        var response = officerController.getActiveDirectorDetails(transaction, TRANSACTION_ID, ERIC_REQUEST_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetActiveDirectorDetailsServiceException() throws ActiveDirectorNotFoundException, ServiceException {
        when(officerService.getActiveDirectorDetails(transaction.getCompanyNumber())).thenThrow(ServiceException.class);
        var response = officerController.getActiveDirectorDetails(transaction, TRANSACTION_ID, ERIC_REQUEST_ID);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testGetActiveDirectorDetailsOfficerNotFoundException() throws ActiveDirectorNotFoundException, ServiceException {
        when(officerService.getActiveDirectorDetails(transaction.getCompanyNumber())).thenThrow(ActiveDirectorNotFoundException.class);
        var response = officerController.getActiveDirectorDetails(transaction, TRANSACTION_ID, ERIC_REQUEST_ID);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}