package uk.gov.companieshouse.confirmationstatementapi.contoller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/confirmation-statement")
public class ConfirmationStatementController {

    @PostMapping("/")
    public ResponseEntity<String> createNewSubmission() {
        return ResponseEntity.ok().body("hello world");
    }
}
