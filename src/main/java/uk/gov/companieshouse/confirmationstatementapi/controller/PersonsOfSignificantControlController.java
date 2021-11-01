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
import uk.gov.companieshouse.confirmationstatementapi.exception.SubmissionNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.model.json.PersonOfSignificantControlJson;
import uk.gov.companieshouse.confirmationstatementapi.repository.ConfirmationStatementSubmissionsRepository;
import uk.gov.companieshouse.confirmationstatementapi.service.PscService;
import uk.gov.companieshouse.confirmationstatementapi.utils.ApiLogger;

import java.util.HashMap;
import java.util.List;

import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.*;

@RestController
class PersonsOfSignificantControlController {

    @Autowired
    private PscService pscService;

    @Autowired
    private ConfirmationStatementSubmissionsRepository confirmationStatementSubmissionsRepository;

    @GetMapping("/transactions/{transaction_id}/confirmation-statement/{confirmation_statement_id}/persons-of-significant-control")
    public ResponseEntity<List<PersonOfSignificantControlJson>> getPersonsOfSignificantControl(
            @RequestAttribute("transaction") Transaction transaction,
            @PathVariable(TRANSACTION_ID_KEY) String transactionId,
            @PathVariable(CONFIRMATION_STATEMENT_ID_KEY) String submissionId,
            @RequestHeader(value = ERIC_REQUEST_ID_KEY) String requestId) {

        var logMap = new HashMap<String, Object>();
        logMap.put(TRANSACTION_ID_KEY, transactionId);
        logMap.put(CONFIRMATION_STATEMENT_ID_KEY, submissionId);

        String companyNumber = transaction.getCompanyNumber();
        String sanitizedCompanyNumber = null;
        if (companyNumber != null) {
            sanitizedCompanyNumber = companyNumber.replaceAll("[\n\r\t]", "_");
        }

        try {
            ApiLogger.infoContext(requestId, String.format("Calling PscService to retrieve persons of significant control for companyNumber %s", sanitizedCompanyNumber), logMap);
            var pscs = pscService.getPSCsFromOracle(submissionId, sanitizedCompanyNumber);
            return ResponseEntity.status(HttpStatus.OK).body(pscs);
        } catch (SubmissionNotFoundException e) {
            ApiLogger.errorContext(requestId, e.getMessage(), e, logMap);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (ServiceException e) {
            logErrorMessage(requestId, sanitizedCompanyNumber, logMap, e);
            return ResponseEntity.internalServerError().build();
        } catch (Exception e) {
            logErrorMessage(requestId, sanitizedCompanyNumber, logMap, e);
            throw e;
        }
    }

    private void logErrorMessage(String requestId, String sanitizedCompanyNumber, HashMap<String, Object> logMap, Exception e) {
        ApiLogger.errorContext(requestId, String.format("Calling PscService to retrieve persons of significant control for companyNumber %s", sanitizedCompanyNumber), e, logMap);
    }
}
