package uk.gov.companieshouse.confirmationstatementapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import uk.gov.companieshouse.api.model.company.StatementOfCapitalJson;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.exception.StatementOfCapitalNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.service.StatementOfCapitalService;
import uk.gov.companieshouse.confirmationstatementapi.utils.ApiLogger;

import java.util.HashMap;

import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.CONFIRMATION_STATEMENT_ID_KEY;
import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.ERIC_REQUEST_ID_KEY;
import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.TRANSACTION_ID_KEY;

@RestController
public class StatementOfCapitalController {

    private final StatementOfCapitalService statementOfCapitalService;

    @Autowired
    public StatementOfCapitalController(StatementOfCapitalService statementOfCapitalService) {
        this.statementOfCapitalService = statementOfCapitalService;
    }

    @GetMapping("/transactions/{transaction_id}/confirmation-statement/{confirmation_statement_id}/statement-of-capital")
    public ResponseEntity<StatementOfCapitalJson> getStatementOfCapital(
            @RequestAttribute("transaction") Transaction transaction,
            @PathVariable(TRANSACTION_ID_KEY) String transactionId,
            @PathVariable(CONFIRMATION_STATEMENT_ID_KEY) String confirmationStatementId,
            @RequestHeader(value = ERIC_REQUEST_ID_KEY) String requestId) {

        var logMap = new HashMap<String, Object>();
        logMap.put(TRANSACTION_ID_KEY, transactionId);

        try {
            ApiLogger.infoContext(requestId, "Calling service to retrieve statement of capital data.", logMap);
            var statementOfCapital = statementOfCapitalService.getStatementOfCapital(transaction.getCompanyNumber());
            return ResponseEntity.status(HttpStatus.OK).body(statementOfCapital);
        } catch (StatementOfCapitalNotFoundException socnfe) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (ServiceException e) {
            ApiLogger.errorContext(requestId, "Error retrieving statement of capital data.", e, logMap);
            return ResponseEntity.internalServerError().build();
        }
    }
}
