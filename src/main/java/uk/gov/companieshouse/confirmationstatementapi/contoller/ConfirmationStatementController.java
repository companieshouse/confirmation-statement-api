package uk.gov.companieshouse.confirmationstatementapi.contoller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@RestController
@RequestMapping("/transactions/{transaction_id}/confirmation-statement")
public class ConfirmationStatementController {

    private static final Logger LOGGER = LoggerFactory.getLogger("Confirmation-Statement-Controller");

    @PostMapping("/")
    public ResponseEntity<String> createNewSubmission(@PathVariable("transaction_id") String transactionId) {
        LOGGER.info("'/confirmation-statement/' Endpoint hit with id: " + transactionId);
        return ResponseEntity.ok().body("hello world");
    }
}
