package uk.gov.companieshouse.confirmationstatementapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.confirmationstatementapi.exception.CompanyNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.service.CostService;
import uk.gov.companieshouse.confirmationstatementapi.utils.ApiLogger;

import java.util.Collections;
import java.util.HashMap;

import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.*;

@RestController
@RequestMapping("/transactions/{transaction_id}/confirmation-statement/{confirmation_statement_id}/costs")
public class CostsController {

    private final CostService costService;

    @Autowired
    public CostsController(CostService costService) {
        this.costService = costService;
    }

    @GetMapping
    public ResponseEntity<Object> getCosts(@RequestAttribute("transaction") Transaction transaction,
                                           @PathVariable(TRANSACTION_ID_KEY) String transactionId,
                                           @PathVariable(CONFIRMATION_STATEMENT_ID_KEY) String submissionId,
                                           @RequestHeader(value = ERIC_REQUEST_ID_KEY) String requestId) {

        var logMap = new HashMap<String, Object>();
        logMap.put(TRANSACTION_ID_KEY, transactionId);
        logMap.put(CONFIRMATION_STATEMENT_ID_KEY, submissionId);
        ApiLogger.infoContext(requestId, "Calling service to get costs", logMap);

        try {
            var cost = costService.getCosts(transaction);

            return ResponseEntity.ok(Collections.singletonList(cost));
        } catch (CompanyNotFoundException e) {
            ApiLogger.errorContext(requestId,e.getMessage(), e, logMap);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            ApiLogger.errorContext(requestId,e.getMessage(), e, logMap);
            return ResponseEntity.internalServerError().build();
        }
    }
}
