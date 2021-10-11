package uk.gov.companieshouse.confirmationstatementapi.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import uk.gov.companieshouse.confirmationstatementapi.exception.ActiveDirectorNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.model.ActiveDirectorDetails;
import uk.gov.companieshouse.confirmationstatementapi.service.OfficerService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OfficerControllerTest {

    @Mock
    private OfficerService officerService;

    @InjectMocks
    private OfficerController officerController;

    private static final String COMPANY_NUMBER = "12345678";
    private static final String ERICK_REQUEST_ID = "XaBcDeF12345";

    @Test
    void testGetActiveDirectorDetails() throws ActiveDirectorNotFoundException, ServiceException {
        when(officerService.getActiveDirectorDetails(COMPANY_NUMBER)).thenReturn(new ActiveDirectorDetails());
        var response = officerController.getActiveDirectorDetails(COMPANY_NUMBER, ERICK_REQUEST_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetActiveDirectorDetailsServiceException() throws ActiveDirectorNotFoundException, ServiceException {
        when(officerService.getActiveDirectorDetails(COMPANY_NUMBER)).thenThrow(ServiceException.class);
        var response = officerController.getActiveDirectorDetails(COMPANY_NUMBER, ERICK_REQUEST_ID);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testGetActiveDirectorDetailsOfficerNotFoundException() throws ActiveDirectorNotFoundException, ServiceException {
        when(officerService.getActiveDirectorDetails(COMPANY_NUMBER)).thenThrow(ActiveDirectorNotFoundException.class);
        var response = officerController.getActiveDirectorDetails(COMPANY_NUMBER, ERICK_REQUEST_ID);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}