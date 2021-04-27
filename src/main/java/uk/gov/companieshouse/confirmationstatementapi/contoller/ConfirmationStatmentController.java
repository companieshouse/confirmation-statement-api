package uk.gov.companieshouse.confirmationstatementapi.contoller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfirmationStatmentController {

    @PostMapping("/")
    public ResponseEntity<String> createNewSubmission() {
        return ResponseEntity.ok().body("hello world");
    }
}
