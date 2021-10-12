package uk.gov.companieshouse.confirmationstatementapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.model.filinggenerator.FilingApi;
import uk.gov.companieshouse.confirmationstatementapi.exception.SubmissionNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.service.FilingService;

import java.util.HashMap;

import static uk.gov.companieshouse.confirmationstatementapi.ConfirmationStatementApiApplication.LOGGER;
import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.ERIC_REQUEST_ID;

@RestController
@RequestMapping("/private/transactions/{transaction_id}/confirmation-statement/{confirmation_statement_id}/filings")
public class FilingController {

    @Autowired
    private FilingService filingService;

    @GetMapping
    public ResponseEntity<FilingApi[]> getFiling(
            @PathVariable("confirmation_statement_id") String confirmationStatementId,
            @PathVariable("transaction_id") String transactionId,
            @RequestHeader(value = ERIC_REQUEST_ID) String requestId) {

        var logMap = new HashMap<String, Object>();
        logMap.put("transaction_id", transactionId);
        logMap.put("confirmation_statement_id", confirmationStatementId);
        LOGGER.infoContext(requestId, "Calling service to retrieve filing", logMap);

        try {
            FilingApi filing = filingService.generateConfirmationFiling(confirmationStatementId);
            return ResponseEntity.ok(new FilingApi[] { filing });
        } catch (SubmissionNotFoundException e) {
            LOGGER.errorContext(requestId, e.getMessage(), e, logMap);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            LOGGER.errorContext(requestId, e.getMessage(), e, logMap);
            return ResponseEntity.internalServerError().build();
        }
    }
}
