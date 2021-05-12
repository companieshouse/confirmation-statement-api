package uk.gov.companieshouse.confirmationstatementapi.contoller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.service.ConfirmationStatementService;

import java.io.IOException;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfirmationStatementControllerTest {

    @Mock
    private ConfirmationStatementService confirmationStatementService;

    @Mock
    private Transaction transaction;

    @InjectMocks
    private ConfirmationStatementController confirmationStatementController;

    private final ResponseEntity<Object> successResponse = ResponseEntity.created(URI.create("URI")).body("Created");
    private final ResponseEntity<Object> validationFailedResponse = ResponseEntity.badRequest().body("BAD");

    @Test
    void createNewSubmission() throws ServiceException {
        when(confirmationStatementService.createConfirmationStatement(transaction)).thenReturn(successResponse);
        var response = confirmationStatementController.createNewSubmission(transaction);

        assertEquals(successResponse, response);
    }

    @Test
    void createNewSubmissionValidationFailedResponse() throws ServiceException {
        when(confirmationStatementService.createConfirmationStatement(transaction)).thenReturn(validationFailedResponse);
        var response = confirmationStatementController.createNewSubmission(transaction);

        assertEquals(validationFailedResponse, response);
    }

    @Test
    void createNewSubmissionServiceException() throws ServiceException {
        when(confirmationStatementService.createConfirmationStatement(transaction)).thenThrow(new ServiceException("ERROR", new IOException()));

        var response = confirmationStatementController.createNewSubmission(transaction);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}