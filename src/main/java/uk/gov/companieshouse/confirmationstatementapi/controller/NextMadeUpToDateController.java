package uk.gov.companieshouse.confirmationstatementapi.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.confirmationstatementapi.exception.CompanyNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.model.json.NextMadeUpToDateJson;
import uk.gov.companieshouse.confirmationstatementapi.service.ConfirmationStatementService;

@RestController
public class NextMadeUpToDateController {

    @Autowired
    private ConfirmationStatementService confirmationStatementService;

    private static final Logger LOGGER = LoggerFactory.getLogger(NextMadeUpToDateController.class);

    @GetMapping("/confirmation-statement/company/{company-number}/next-made-up-to-date")
    public ResponseEntity<NextMadeUpToDateJson> getNextMadeUpToDate(@PathVariable("company-number") String companyNumber) {
        try {
            LOGGER.info("Calling service to retrieve the next made up to date.");
            NextMadeUpToDateJson nextMadeUpToDateJson = confirmationStatementService.getNextMadeUpToDate(companyNumber);
            return ResponseEntity.ok().body(nextMadeUpToDateJson);
        } catch(CompanyNotFoundException cnfe) {
            LOGGER.error(String.format("Unable to find company %s", companyNumber), cnfe);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            LOGGER.error("Error retrieving next made up to date.", e);
            return ResponseEntity.internalServerError().build();
        }
    }

}
