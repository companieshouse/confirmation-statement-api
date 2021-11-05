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
import uk.gov.companieshouse.confirmationstatementapi.exception.SubmissionNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.model.ActiveOfficerDetails;
import uk.gov.companieshouse.confirmationstatementapi.repository.ConfirmationStatementSubmissionsRepository;
import uk.gov.companieshouse.confirmationstatementapi.service.OfficerService;

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
    void testGetActiveOfficerDetails() throws ActiveOfficerNotFoundException, ServiceException, SubmissionNotFoundException {
        when(officerService.getActiveOfficerDetails(SUBMISSION_ID, transaction.getCompanyNumber())).thenReturn(new ActiveOfficerDetails());
        var response = officerController.getActiveOfficersDetails(transaction, TRANSACTION_ID, SUBMISSION_ID, ERIC_REQUEST_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetActiveOfficerDetailsServiceException() throws ActiveOfficerNotFoundException, ServiceException, SubmissionNotFoundException {
        when(officerService.getActiveOfficerDetails(SUBMISSION_ID, transaction.getCompanyNumber())).thenThrow(ServiceException.class);
        var response = officerController.getActiveOfficersDetails(transaction, TRANSACTION_ID, SUBMISSION_ID, ERIC_REQUEST_ID);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testGetActiveOfficerDetailsOfficerNotFoundException() throws ActiveOfficerNotFoundException, ServiceException, SubmissionNotFoundException {
        when(officerService.getActiveOfficerDetails(SUBMISSION_ID, transaction.getCompanyNumber())).thenThrow(ActiveOfficerNotFoundException.class);
        var response = officerController.getActiveOfficersDetails(transaction, TRANSACTION_ID, SUBMISSION_ID, ERIC_REQUEST_ID);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetActiveOfficerDetailsSubmissionNotFoundException() throws ActiveOfficerNotFoundException, ServiceException, SubmissionNotFoundException {
        when(officerService.getActiveOfficerDetails(SUBMISSION_ID, transaction.getCompanyNumber())).thenThrow(SubmissionNotFoundException.class);
        var response = officerController.getActiveOfficersDetails(transaction, TRANSACTION_ID, SUBMISSION_ID, ERIC_REQUEST_ID);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}