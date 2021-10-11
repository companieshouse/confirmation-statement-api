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
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.util.HashMap;

import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.ERIC_REQUEST_ID;

@RestController
public class NextMadeUpToDateController {

    @Autowired
    private ConfirmationStatementService confirmationStatementService;

    private static final Logger LOGGER = LoggerFactory.getLogger(NextMadeUpToDateController.class.getName());

    @GetMapping("/confirmation-statement/company/{companyNumber}/next-made-up-to-date")
    public ResponseEntity<NextMadeUpToDateJson> getNextMadeUpToDate(@PathVariable String companyNumber,
            @RequestHeader(value = ERIC_REQUEST_ID) String requestId) {
        var map = new HashMap<String, Object>();
        map.put("companyNumber", companyNumber);

        try {
            LOGGER.infoContext(requestId, "Calling service to retrieve the next made up to date.", map);
            NextMadeUpToDateJson nextMadeUpToDateJson = confirmationStatementService.getNextMadeUpToDate(companyNumber);
            return ResponseEntity.ok().body(nextMadeUpToDateJson);
        } catch(CompanyNotFoundException cnfe) {
            LOGGER.errorContext(requestId,String.format("Unable to find company %s", companyNumber), cnfe, map);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            LOGGER.errorContext(requestId,String.format("Error retrieving next made up to date.", companyNumber), e, map);
            return ResponseEntity.internalServerError().build();
        }
    }

}
