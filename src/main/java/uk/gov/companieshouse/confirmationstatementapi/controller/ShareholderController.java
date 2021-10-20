package uk.gov.companieshouse.confirmationstatementapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.model.json.shareholder.ShareholderJson;
import uk.gov.companieshouse.confirmationstatementapi.service.ShareholderService;

import java.util.HashMap;
import java.util.List;

import static uk.gov.companieshouse.confirmationstatementapi.ConfirmationStatementApiApplication.LOGGER;
import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.*;

@RestController
public class ShareholderController {

    private ShareholderService shareholderService;

    @Autowired
    public ShareholderController(ShareholderService shareholderService) {
        this.shareholderService = shareholderService;
    }

    @GetMapping("/transactions/{transaction_id}/confirmation-statement/{confirmation_statement_submission_id}/shareholders")
    public ResponseEntity<List<ShareholderJson>> getShareholders(
            @RequestAttribute("transaction") Transaction transaction,
            @PathVariable(TRANSACTION_ID_KEY) String transactionId,
            @PathVariable(CONFIRMATION_STATEMENT_ID_KEY) String submissionId,
            @RequestHeader(value = ERIC_REQUEST_ID_KEY) String requestId) {

        var map = new HashMap<String, Object>();
        map.put(TRANSACTION_ID_KEY, transactionId);
        map.put(CONFIRMATION_STATEMENT_ID_KEY, submissionId);

        try {
            LOGGER.infoContext(requestId, "Calling service to retrieve shareholders data", map);
            var shareholders = shareholderService.getShareholders(transaction.getCompanyNumber());
            return ResponseEntity.status(HttpStatus.OK).body(shareholders);
        } catch (ServiceException e) {
            LOGGER.errorContext(requestId,"Error retrieving shareholders data", e, map);
            return ResponseEntity.internalServerError().build();
        }
    }
}
