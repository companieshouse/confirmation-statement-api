package uk.gov.companieshouse.confirmationstatementapi.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.model.filinggenerator.FilingApi;
import uk.gov.companieshouse.confirmationstatementapi.exception.SubmissionNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.service.FilingService;

@RestController
@RequestMapping("/private/transactions/{transaction_id}/confirmation-statement/{confirmation_statement_id}/filings")
public class FilingController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FilingController.class);

    @Autowired
    private FilingService filingService;

    @GetMapping
    public ResponseEntity<FilingApi> getFiling(@PathVariable("confirmation_statement_id") String confirmationStatementId) {
        try {
            FilingApi filing = filingService.generateConfirmationFiling(confirmationStatementId);
            return ResponseEntity.ok(filing);
        } catch (SubmissionNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
