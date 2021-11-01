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
import uk.gov.companieshouse.confirmationstatementapi.exception.StatementOfCapitalNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.exception.SubmissionNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.model.json.statementofcapital.StatementOfCapitalJson;
import uk.gov.companieshouse.confirmationstatementapi.service.StatementOfCapitalService;
import uk.gov.companieshouse.confirmationstatementapi.utils.ApiLogger;

import java.util.HashMap;

import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.*;

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
            @PathVariable(CONFIRMATION_STATEMENT_ID_KEY) String submissionId,
            @RequestHeader(value = ERIC_REQUEST_ID_KEY) String requestId) {

        var logMap = new HashMap<String, Object>();
        logMap.put(TRANSACTION_ID_KEY, transactionId);
        logMap.put(CONFIRMATION_STATEMENT_ID_KEY, submissionId);

        try {
            ApiLogger.infoContext(requestId, "Calling service to retrieve statement of capital data.", logMap);
            var statementOfCapital = statementOfCapitalService.getStatementOfCapital(submissionId, transaction.getCompanyNumber());
            return ResponseEntity.status(HttpStatus.OK).body(statementOfCapital);
        } catch (SubmissionNotFoundException e) {
            ApiLogger.errorContext(requestId, e.getMessage(), e, logMap);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (StatementOfCapitalNotFoundException e) {
            ApiLogger.infoContext(requestId, e.getMessage(), logMap);
            return ResponseEntity.notFound().build();
        } catch (ServiceException e) {
            ApiLogger.errorContext(requestId, "Error retrieving statement of capital data.", e, logMap);
            return ResponseEntity.internalServerError().build();
        }
    }
}
