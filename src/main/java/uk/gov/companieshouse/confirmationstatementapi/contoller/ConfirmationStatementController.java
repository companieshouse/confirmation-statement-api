package uk.gov.companieshouse.confirmationstatementapi.contoller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.confirmationstatementapi.common.ApiLogger;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/confirmation-statement")
public class ConfirmationStatementController {

    @Autowired
    private ApiLogger apiLogger;


    @PostMapping("/")
    public ResponseEntity<String> createNewSubmission() {

        apiLogger.info("POST '//confirmation-statement/' Endpoint hit");
        return ResponseEntity.ok().body("hello world");
    }
}
