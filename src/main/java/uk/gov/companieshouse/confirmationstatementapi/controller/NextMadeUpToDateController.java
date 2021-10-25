package uk.gov.companieshouse.confirmationstatementapi.controller;

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

import java.util.HashMap;

import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.ERIC_REQUEST_ID_KEY;

@RestController
public class NextMadeUpToDateController {

    @Autowired
    private ApiLogger apiLogger;

    @Autowired
    private ConfirmationStatementService confirmationStatementService;

    @GetMapping("/confirmation-statement/company/{companyNumber}/next-made-up-to-date")
    public ResponseEntity<NextMadeUpToDateJson> getNextMadeUpToDate(
            @PathVariable String companyNumber,
            @RequestHeader(value = ERIC_REQUEST_ID_KEY) String requestId) {
        var map = new HashMap<String, Object>();
        map.put("company_number", companyNumber);

        try {
            apiLogger.infoContext(requestId, "Calling service to retrieve the next made up to date.", map);
            NextMadeUpToDateJson nextMadeUpToDateJson = confirmationStatementService.getNextMadeUpToDate(companyNumber);
            return ResponseEntity.ok().body(nextMadeUpToDateJson);
        } catch(CompanyNotFoundException cnfe) {
            apiLogger.infoContext(requestId, "Unable to find company.", map);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            apiLogger.errorContext(requestId, "Error retrieving next made up to date.", e, map);
            return ResponseEntity.internalServerError().build();
        }
    }

}
