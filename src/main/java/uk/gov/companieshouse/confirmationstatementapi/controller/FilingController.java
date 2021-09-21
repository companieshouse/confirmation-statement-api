package uk.gov.companieshouse.confirmationstatementapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.filinggenerator.FilingApi;

@RestController
@RequestMapping("/private/transactions/{transaction_id}/confirmation-statement/{confirmation_statement_id}/filings")
public class FilingController {

    @GetMapping
    public ResponseEntity<FilingApi> getFiling(@RequestAttribute("transaction") Transaction transaction) {
        var filing = new FilingApi();
        filing.setDescription(transaction.getDescription());

        return ResponseEntity.ok(filing);
    }
}
