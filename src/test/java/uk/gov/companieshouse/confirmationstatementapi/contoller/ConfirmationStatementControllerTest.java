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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfirmationStatementControllerTest {

    private static final ResponseEntity<Object> SUCCESS_RESPONSE = ResponseEntity.created(URI.create("URI")).body("Created");
    private static final ResponseEntity<Object> VALIDATION_FAILED_RESPONSE = ResponseEntity.badRequest().body("BAD");

    @Mock
    private ConfirmationStatementService confirmationStatementService;

    @Mock
    private Transaction transaction;

    @InjectMocks
    private ConfirmationStatementController confirmationStatementController;

    @Test
    void createNewSubmission() throws ServiceException {
        when(confirmationStatementService.createConfirmationStatement(transaction)).thenReturn(SUCCESS_RESPONSE);
        var response = confirmationStatementController.createNewSubmission(transaction);

        assertEquals(SUCCESS_RESPONSE, response);
    }

    @Test
    void createNewSubmissionValidationFailedResponse() throws ServiceException {
        when(confirmationStatementService.createConfirmationStatement(transaction)).thenReturn(VALIDATION_FAILED_RESPONSE);
        var response = confirmationStatementController.createNewSubmission(transaction);

        assertEquals(VALIDATION_FAILED_RESPONSE, response);
    }

    @Test
    void createNewSubmissionServiceException() throws ServiceException {
        when(confirmationStatementService.createConfirmationStatement(transaction)).thenThrow(new ServiceException("ERROR", new IOException()));

        var response = confirmationStatementController.createNewSubmission(transaction);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}