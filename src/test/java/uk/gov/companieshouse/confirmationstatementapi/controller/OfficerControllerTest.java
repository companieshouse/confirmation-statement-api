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
import uk.gov.companieshouse.confirmationstatementapi.exception.SubmissionNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.model.ActiveDirectorDetails;
import uk.gov.companieshouse.confirmationstatementapi.repository.ConfirmationStatementSubmissionsRepository;
import uk.gov.companieshouse.confirmationstatementapi.service.OfficerService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OfficerControllerTest {

    @Mock
    private Transaction transaction;

    @Mock
    private OfficerService officerService;

    @Mock
    private ConfirmationStatementSubmissionsRepository confirmationStatementSubmissionsRepository;

    @InjectMocks
    private OfficerController officerController;

    private static final String ERIC_REQUEST_ID = "XaBcDeF12345";
    private static final String TRANSACTION_ID = "GFEDCBA";
    private static final String SUBMISSION_ID = "ABCDEFG";

    @Test
    void testGetActiveDirectorDetails() throws ActiveDirectorNotFoundException, ServiceException, SubmissionNotFoundException {
        when(officerService.getActiveDirectorDetails(SUBMISSION_ID, transaction.getCompanyNumber())).thenReturn(new ActiveDirectorDetails());
        var response = officerController.getActiveDirectorDetails(transaction, TRANSACTION_ID, SUBMISSION_ID, ERIC_REQUEST_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetActiveDirectorDetailsServiceException() throws ActiveDirectorNotFoundException, ServiceException, SubmissionNotFoundException {
        when(officerService.getActiveDirectorDetails(SUBMISSION_ID, transaction.getCompanyNumber())).thenThrow(ServiceException.class);
        var response = officerController.getActiveDirectorDetails(transaction, TRANSACTION_ID, SUBMISSION_ID, ERIC_REQUEST_ID);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testGetActiveDirectorDetailsOfficerNotFoundException() throws ActiveDirectorNotFoundException, ServiceException, SubmissionNotFoundException {
        when(officerService.getActiveDirectorDetails(SUBMISSION_ID, transaction.getCompanyNumber())).thenThrow(ActiveDirectorNotFoundException.class);
        var response = officerController.getActiveDirectorDetails(transaction, TRANSACTION_ID, SUBMISSION_ID, ERIC_REQUEST_ID);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetActiveDirectorDetailsSubmissionNotFoundException() throws ActiveDirectorNotFoundException, ServiceException, SubmissionNotFoundException {
        when(officerService.getActiveDirectorDetails(SUBMISSION_ID, transaction.getCompanyNumber())).thenThrow(SubmissionNotFoundException.class);
        var response = officerController.getActiveDirectorDetails(transaction, TRANSACTION_ID, SUBMISSION_ID, ERIC_REQUEST_ID);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}