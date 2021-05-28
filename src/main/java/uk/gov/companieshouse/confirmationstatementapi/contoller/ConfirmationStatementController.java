package uk.gov.companieshouse.confirmationstatementapi.contoller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.service.ConfirmationStatementService;

@RestController
@RequestMapping("/transactions/{transaction_id}/confirmation-statement")
public class ConfirmationStatementController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfirmationStatementController.class);

    private final ConfirmationStatementService confirmationStatementService;

    @Autowired
    public ConfirmationStatementController(ConfirmationStatementService confirmationStatementService) {
        this.confirmationStatementService = confirmationStatementService;
    }

    @PostMapping("/")
    public ResponseEntity<Object> createNewSubmission(@RequestAttribute("transaction") Transaction transaction) {
        try {
            return confirmationStatementService.createConfirmationStatement(transaction);
        } catch (ServiceException e) {
            LOGGER.error("Error Creating Confirmation Statement", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
