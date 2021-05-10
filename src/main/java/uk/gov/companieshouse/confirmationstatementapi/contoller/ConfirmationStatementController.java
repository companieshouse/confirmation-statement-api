package uk.gov.companieshouse.confirmationstatementapi.contoller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.confirmationstatementapi.ConfirmationStatementApiApplication;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@RestController
@RequestMapping("/confirmation-statement")
public class ConfirmationStatementController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfirmationStatementApiApplication.APP_NAME);

    @PostMapping("/")
    public ResponseEntity<String> createNewSubmission() {
        LOGGER.info("'/confirmation-statement/' Endpoint hit");
        return ResponseEntity.ok().body("hello world");
    }
}
