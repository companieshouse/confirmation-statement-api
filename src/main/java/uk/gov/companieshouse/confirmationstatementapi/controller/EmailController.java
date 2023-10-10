package uk.gov.companieshouse.confirmationstatementapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.confirmationstatementapi.exception.RegisteredEmailNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.service.EmailService;
import uk.gov.companieshouse.confirmationstatementapi.utils.ApiLogger;

import java.util.HashMap;

import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.ERIC_REQUEST_ID_KEY;
import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.TRANSACTION_ID_KEY;

@RestController
@RequestMapping("/transactions/{transaction_id}/confirmation-statement")
public class EmailController {

    private final EmailService emailService;

    @Autowired
    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/{confirmation_statement_id}/registered-email-address")
    public ResponseEntity<Object> getRegisteredEmailAddress(
            @RequestAttribute("transaction") Transaction transaction,
            @PathVariable(TRANSACTION_ID_KEY) String transactionId,
            @RequestHeader(value = ERIC_REQUEST_ID_KEY) String requestId) {

        var map = new HashMap<String, Object>();
        map.put(TRANSACTION_ID_KEY, transactionId);

        try {
            ApiLogger.infoContext(requestId, "Calling service to retrieve registered email address", map);
            var rea = emailService.getRegisteredEmailAddress(transaction.getCompanyNumber());
            return ResponseEntity.status(HttpStatus.OK).body(rea);
        } catch (RegisteredEmailNotFoundException renfe) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(renfe.getMessage());
        } catch (ServiceException e) {
            ApiLogger.errorContext(requestId, "Error retrieving registered email address", e, map);
            return ResponseEntity.internalServerError().build();
        }
    }
}
