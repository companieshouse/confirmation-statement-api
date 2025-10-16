package uk.gov.companieshouse.confirmationstatementapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.confirmationstatementapi.exception.NewConfirmationDateInvalidException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.exception.SubmissionNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.model.json.ConfirmationStatementSubmissionJson;
import uk.gov.companieshouse.confirmationstatementapi.service.ConfirmationStatementService;
import uk.gov.companieshouse.confirmationstatementapi.utils.ApiLogger;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;

import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.CONFIRMATION_STATEMENT_ID_KEY;
import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.ERIC_REQUEST_ID_KEY;
import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.TRANSACTION_ID_KEY;

@RestController
@RequestMapping("/transactions/{transaction_id}/confirmation-statement")
public class ConfirmationStatementController {

    private final ConfirmationStatementService confirmationStatementService;

    @Autowired
    public ConfirmationStatementController(ConfirmationStatementService confirmationStatementService) {
        this.confirmationStatementService = confirmationStatementService;
    }

    @PostMapping("/")
    public ResponseEntity<Object> createNewSubmission(
            @RequestAttribute("transaction") Transaction transaction,
            @PathVariable(TRANSACTION_ID_KEY) String transactionId,
            @RequestHeader(value = ERIC_REQUEST_ID_KEY) String requestId,
            HttpServletRequest request) {

        String passthroughHeader = request.getHeader(ApiSdkManager.getEricPassthroughTokenHeader());

        var logMap = new HashMap<String, Object>();
        logMap.put(TRANSACTION_ID_KEY, transactionId);
        ApiLogger.infoContext(requestId, "Calling service to create submission", logMap);

        try {
            return confirmationStatementService.createConfirmationStatement(transaction, passthroughHeader);
        } catch (ServiceException e) {
            ApiLogger.errorContext(requestId,"Error Creating Confirmation Statement", e, logMap);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{confirmation_statement_id}")
    public ResponseEntity<Object> updateSubmission(
            @RequestAttribute("transaction") Transaction transaction,
            @RequestBody ConfirmationStatementSubmissionJson confirmationStatementSubmissionJson,
            @PathVariable(CONFIRMATION_STATEMENT_ID_KEY) String submissionId,
            @PathVariable(TRANSACTION_ID_KEY) String transactionId,
            @RequestHeader(value = ERIC_REQUEST_ID_KEY) String requestId) {

        var logMap = new HashMap<String, Object>();
        logMap.put(CONFIRMATION_STATEMENT_ID_KEY, submissionId);
        logMap.put(TRANSACTION_ID_KEY, transactionId);
        ApiLogger.infoContext(requestId, "Calling service to update confirmation statement data", logMap);

        try {
            return confirmationStatementService.updateConfirmationStatement(transaction, submissionId, confirmationStatementSubmissionJson);
        } catch (ServiceException | NewConfirmationDateInvalidException e) {
            String errorMessage = (e instanceof NewConfirmationDateInvalidException) ?
                    "Invalid New Confirmation Date" : "Error Updating Confirmation Statement";
            ApiLogger.errorContext(requestId, errorMessage, e, logMap);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{confirmation_statement_id}")
    public ResponseEntity<Object> getSubmission(
            @PathVariable(CONFIRMATION_STATEMENT_ID_KEY) String submissionId,
            @PathVariable(TRANSACTION_ID_KEY) String transactionId,
            @RequestHeader(value = ERIC_REQUEST_ID_KEY) String requestId) {

        var logMap = new HashMap<String, Object>();
        logMap.put(CONFIRMATION_STATEMENT_ID_KEY, submissionId);
        logMap.put(TRANSACTION_ID_KEY, transactionId);
        ApiLogger.infoContext(requestId, "Calling service to retrieve confirmation statement data", logMap);

        var serviceResponse = confirmationStatementService.getConfirmationStatement(submissionId);
        return serviceResponse.<ResponseEntity<Object>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{confirmation_statement_id}/validation-status")
    public ResponseEntity<Object> getValidationStatus(
            @RequestAttribute("transaction") Transaction transaction,
            @PathVariable(CONFIRMATION_STATEMENT_ID_KEY) String submissionId,
            @PathVariable(TRANSACTION_ID_KEY) String transactionId,
            @RequestHeader(value = ERIC_REQUEST_ID_KEY) String requestId) {

        var logMap = new HashMap<String, Object>();
        logMap.put(CONFIRMATION_STATEMENT_ID_KEY, submissionId);
        logMap.put(TRANSACTION_ID_KEY, transactionId);
        ApiLogger.infoContext(requestId, "Calling service to get validation status", logMap);

        try {
            var validationStatusResponse = confirmationStatementService.isValid(transaction, submissionId);
            return ResponseEntity.ok().body(validationStatusResponse);
        } catch (SubmissionNotFoundException e) {
            ApiLogger.errorContext(requestId,e.getMessage(), e, logMap);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            ApiLogger.errorContext(requestId,e.getMessage(), e, logMap);
            return ResponseEntity.internalServerError().build();
        }
    }
}
