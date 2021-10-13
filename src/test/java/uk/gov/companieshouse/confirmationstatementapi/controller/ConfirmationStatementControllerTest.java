package uk.gov.companieshouse.confirmationstatementapi.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.validationstatus.ValidationStatusError;
import uk.gov.companieshouse.api.model.validationstatus.ValidationStatusResponse;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.exception.SubmissionNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.model.json.ConfirmationStatementSubmissionJson;
import uk.gov.companieshouse.confirmationstatementapi.service.ConfirmationStatementService;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfirmationStatementControllerTest {

    private static final ResponseEntity<Object> CREATED_SUCCESS_RESPONSE = ResponseEntity.created(URI.create("URI")).body("Created");
    private static final ResponseEntity<Object> UPDATED_SUCCESS_RESPONSE = ResponseEntity.ok().build();
    private static final ResponseEntity<Object> VALIDATION_FAILED_RESPONSE = ResponseEntity.badRequest().body("BAD");
    private static final ResponseEntity<Object> NOT_FOUND_RESPONSE = ResponseEntity.notFound().build();
    private static final String PASSTHROUGH = "13456";
    private static final String SUBMISSION_ID = "ABCDEFG";
    private static final String TRANSACTION_ID = "GFEDCBA";
    private static final String ERIC_REQUEST_ID = "XaBcDeF12345";

    @Mock
    private ConfirmationStatementService confirmationStatementService;

    @Mock
    private Transaction transaction;

    private ConfirmationStatementSubmissionJson confirmationStatementSubmissionJson;

    private MockHttpServletRequest mockHttpServletRequest;

    @InjectMocks
    private ConfirmationStatementController confirmationStatementController;

    @BeforeEach
    void init() {
        mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.addHeader("ERIC-Access-Token", PASSTHROUGH);

        confirmationStatementSubmissionJson = new ConfirmationStatementSubmissionJson();
    }
    @Test
    void createNewSubmission() throws ServiceException {
        when(confirmationStatementService.createConfirmationStatement(transaction, PASSTHROUGH)).thenReturn(CREATED_SUCCESS_RESPONSE);
        var response = confirmationStatementController.createNewSubmission(transaction,TRANSACTION_ID, mockHttpServletRequest);

        assertEquals(CREATED_SUCCESS_RESPONSE, response);
    }

    @Test
    void createNewSubmissionValidationFailedResponse() throws ServiceException {
        when(confirmationStatementService.createConfirmationStatement(transaction, PASSTHROUGH)).thenReturn(VALIDATION_FAILED_RESPONSE);
        var response = confirmationStatementController.createNewSubmission(transaction,TRANSACTION_ID, mockHttpServletRequest);

        assertEquals(VALIDATION_FAILED_RESPONSE, response);
    }

    @Test
    void createNewSubmissionServiceException() throws ServiceException {
        when(confirmationStatementService.createConfirmationStatement(transaction, PASSTHROUGH)).thenThrow(new ServiceException("ERROR", new IOException()));

        var response = confirmationStatementController.createNewSubmission(transaction,TRANSACTION_ID, mockHttpServletRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void updateSubmission() {
        when(confirmationStatementService.updateConfirmationStatement(SUBMISSION_ID, confirmationStatementSubmissionJson))
                .thenReturn(UPDATED_SUCCESS_RESPONSE);

        var response = confirmationStatementController.updateSubmission(confirmationStatementSubmissionJson, SUBMISSION_ID,TRANSACTION_ID, ERIC_REQUEST_ID);

        assertEquals(UPDATED_SUCCESS_RESPONSE, response);
    }

    @Test
    void updateSubmissionIdNotFound() {
        when(confirmationStatementService.updateConfirmationStatement(SUBMISSION_ID, confirmationStatementSubmissionJson))
                .thenReturn(NOT_FOUND_RESPONSE);

        var response = confirmationStatementController.updateSubmission(confirmationStatementSubmissionJson, SUBMISSION_ID,TRANSACTION_ID, ERIC_REQUEST_ID);

        assertEquals(NOT_FOUND_RESPONSE, response);
    }

    @Test
    void getSubmission() {
        when(confirmationStatementService.getConfirmationStatement(SUBMISSION_ID))
                .thenReturn(Optional.of(new ConfirmationStatementSubmissionJson()));

        var response = confirmationStatementController.getSubmission(SUBMISSION_ID, TRANSACTION_ID, ERIC_REQUEST_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getSubmissionIdNotFound() {
        when(confirmationStatementService.getConfirmationStatement(SUBMISSION_ID))
                .thenReturn(Optional.empty());

        var response = confirmationStatementController.getSubmission(SUBMISSION_ID, TRANSACTION_ID, ERIC_REQUEST_ID);

        assertEquals(NOT_FOUND_RESPONSE, response);
    }

    @Test
    void getTrueValidationStatus() throws SubmissionNotFoundException {
        ValidationStatusResponse validationStatus = new ValidationStatusResponse();
        validationStatus.setValid(true);
        when(confirmationStatementService.isValid(SUBMISSION_ID)).thenReturn(validationStatus);
        var response = confirmationStatementController.getValidationStatus(SUBMISSION_ID, TRANSACTION_ID, ERIC_REQUEST_ID);
        assertEquals(ResponseEntity.ok().body(validationStatus), response);
    }

    @Test
    void getFalseValidationStatus() throws SubmissionNotFoundException {
        ValidationStatusResponse validationStatus = new ValidationStatusResponse();
        validationStatus.setValid(false);
        ValidationStatusError[] errors = new ValidationStatusError[1];
        ValidationStatusError error = new ValidationStatusError();
        error.setType("ch:validation");
        errors[0] = error;
        validationStatus.setValidationStatusError(errors);
        when(confirmationStatementService.isValid(SUBMISSION_ID)).thenReturn(validationStatus);
        var response = confirmationStatementController.getValidationStatus(SUBMISSION_ID, TRANSACTION_ID, ERIC_REQUEST_ID);
        assertEquals(ResponseEntity.ok().body(validationStatus), response);
    }

    @Test
    void getValidationStatusNotFound() throws SubmissionNotFoundException {
        ValidationStatusResponse validationStatus = new ValidationStatusResponse();
        validationStatus.setValid(true);
        when(confirmationStatementService.isValid(SUBMISSION_ID)).thenThrow(SubmissionNotFoundException.class);
        var response = confirmationStatementController.getValidationStatus(SUBMISSION_ID,TRANSACTION_ID, ERIC_REQUEST_ID);
        assertThrows(SubmissionNotFoundException.class, () -> confirmationStatementService.isValid(SUBMISSION_ID));
        assertEquals(NOT_FOUND_RESPONSE, response);
    }
}
