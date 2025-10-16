package uk.gov.companieshouse.confirmationstatementapi.controller;

import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.ERIC_REQUEST_ID_KEY;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import uk.gov.companieshouse.confirmationstatementapi.exception.CompanyNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.model.json.NextMadeUpToDateJson;
import uk.gov.companieshouse.confirmationstatementapi.service.ConfirmationStatementService;
import uk.gov.companieshouse.confirmationstatementapi.utils.ApiLogger;

@RestController
public class NextMadeUpToDateController {

    private final ConfirmationStatementService confirmationStatementService;

    @Autowired
    public NextMadeUpToDateController(ConfirmationStatementService confirmationStatementService) {
        super();
        this.confirmationStatementService = confirmationStatementService;
    }

    @GetMapping("/confirmation-statement/company/{company-number}/next-made-up-to-date")
    public ResponseEntity<NextMadeUpToDateJson> getNextMadeUpToDate(
            @PathVariable("company-number") String companyNumber,
            @RequestHeader(value = ERIC_REQUEST_ID_KEY) String requestId) {
        var map = new HashMap<String, Object>();
        map.put("company_number", companyNumber);

        try {
            ApiLogger.infoContext(requestId, "Calling service to retrieve the next made up to date.", map);
            var nextMadeUpToDateJson = confirmationStatementService.getNextMadeUpToDate(companyNumber);
            return ResponseEntity.ok().body(nextMadeUpToDateJson);
        } catch(CompanyNotFoundException cnfe) {
            ApiLogger.infoContext(requestId, "Unable to find company.", map);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            ApiLogger.errorContext(requestId, "Error retrieving next made up to date.", e, map);
            return ResponseEntity.internalServerError().build();
        }
    }

}
