package uk.gov.companieshouse.confirmationstatementapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.model.filinggenerator.FilingApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.confirmationstatementapi.exception.SubmissionNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.service.FilingService;
import uk.gov.companieshouse.confirmationstatementapi.utils.ApiLogger;

import java.util.HashMap;

import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.CONFIRMATION_STATEMENT_ID_KEY;
import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.ERIC_REQUEST_ID_KEY;
import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.TRANSACTION_ID_KEY;

@RestController
@RequestMapping("/private/transactions/{transaction_id}/confirmation-statement/{confirmation_statement_id}/filings")
public class FilingController {

    @Autowired
    private FilingService filingService;

    @Autowired
    ApiLogger apiLogger;

    @GetMapping
    public ResponseEntity<FilingApi[]> getFiling(
            @RequestAttribute("transaction") Transaction transaction,
            @PathVariable(CONFIRMATION_STATEMENT_ID_KEY) String confirmationStatementId,
            @PathVariable(TRANSACTION_ID_KEY) String transactionId,
            @RequestHeader(value = ERIC_REQUEST_ID_KEY) String requestId) {

        var logMap = new HashMap<String, Object>();
        logMap.put(TRANSACTION_ID_KEY, transactionId);
        logMap.put(CONFIRMATION_STATEMENT_ID_KEY, confirmationStatementId);
        apiLogger.infoContext(requestId, "Calling service to retrieve filing", logMap);

        try {
            FilingApi filing = filingService.generateConfirmationFiling(confirmationStatementId, transaction);
            return ResponseEntity.ok(new FilingApi[] { filing });
        } catch (SubmissionNotFoundException e) {
            apiLogger.errorContext(requestId, e.getMessage(), e, logMap);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            apiLogger.errorContext(requestId, e.getMessage(), e, logMap);
            return ResponseEntity.internalServerError().build();
        }
    }
}
