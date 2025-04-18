package uk.gov.companieshouse.confirmationstatementapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.api.model.company.ActiveOfficerDetailsJson;
import uk.gov.companieshouse.confirmationstatementapi.service.OfficerService;
import uk.gov.companieshouse.confirmationstatementapi.utils.ApiLogger;

import java.util.HashMap;
import java.util.List;

import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.ERIC_REQUEST_ID_KEY;
import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.TRANSACTION_ID_KEY;
import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.CONFIRMATION_STATEMENT_ID_KEY;

@RestController
public class OfficerController {

    private final OfficerService officerService;

    @Autowired
    public OfficerController(OfficerService officerService) {
        this.officerService = officerService;
    }

    @GetMapping("/transactions/{transaction_id}/confirmation-statement/{confirmation_statement_id}/active-director-details")
    public ResponseEntity<ActiveOfficerDetailsJson> getActiveOfficersDetails(
            @RequestAttribute("transaction") Transaction transaction,
            @PathVariable(TRANSACTION_ID_KEY) final String transactionId,
            @PathVariable(CONFIRMATION_STATEMENT_ID_KEY) String confirmationStatementId,
            @RequestHeader(value = ERIC_REQUEST_ID_KEY) String requestId) {

        var logMap = new HashMap<String, Object>();
        logMap.put(TRANSACTION_ID_KEY, transactionId);
        logMap.put(CONFIRMATION_STATEMENT_ID_KEY, confirmationStatementId);

        try {
            ApiLogger.infoContext(requestId, "Calling service to retrieve the active director details.", logMap);
            var directorDetails = officerService.getActiveOfficerDetails(transaction.getCompanyNumber());
            return ResponseEntity.status(HttpStatus.OK).body(directorDetails);
        } catch (ServiceException e) {
            ApiLogger.errorContext(requestId, "Error retrieving active officer details.", e, logMap);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/transactions/{transaction_id}/confirmation-statement/{confirmation_statement_id}/active-officers-details")
    public ResponseEntity<List<ActiveOfficerDetailsJson>> getListActiveOfficersDetails(
            @RequestAttribute("transaction") Transaction transaction,
            @PathVariable(TRANSACTION_ID_KEY) String transactionId,
            @PathVariable(CONFIRMATION_STATEMENT_ID_KEY) String confirmationStatementId,
            @RequestHeader(value = ERIC_REQUEST_ID_KEY) String requestId) {

        var logMap = new HashMap<String, Object>();
        logMap.put(TRANSACTION_ID_KEY, transactionId);
        logMap.put(CONFIRMATION_STATEMENT_ID_KEY, confirmationStatementId);

        try {
            ApiLogger.infoContext(requestId, "Calling service to retrieve the active officers details.", logMap);
            var officersDetails = officerService.getListActiveOfficersDetails(transaction.getCompanyNumber());
            return ResponseEntity.status(HttpStatus.OK).body(officersDetails);
        } catch (ServiceException e) {
            ApiLogger.errorContext(requestId, "Error retrieving active officers details.", e, logMap);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
