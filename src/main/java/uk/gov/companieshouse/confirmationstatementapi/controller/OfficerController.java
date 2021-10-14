package uk.gov.companieshouse.confirmationstatementapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.confirmationstatementapi.exception.ActiveDirectorNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.model.ActiveDirectorDetails;
import uk.gov.companieshouse.confirmationstatementapi.service.OfficerService;

import java.util.HashMap;

import static uk.gov.companieshouse.confirmationstatementapi.ConfirmationStatementApiApplication.LOGGER;
import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.ERIC_REQUEST_ID_KEY;

@RestController
public class OfficerController {

    @Autowired
    private OfficerService officerService;

    @GetMapping("/confirmation-statement/company/{companyNumber}/active-director-details")
    public ResponseEntity<ActiveDirectorDetails> getActiveDirectorDetails(
            @PathVariable String companyNumber,
            @RequestHeader(value = ERIC_REQUEST_ID_KEY) String requestId) {

        var logMap = new HashMap<String, Object>();
        logMap.put("company_number", companyNumber);

        try {
            LOGGER.infoContext(requestId, "Calling service to retrieve the active director details.", logMap);
            var directorDetails = officerService.getActiveDirectorDetails(companyNumber);
            return ResponseEntity.status(HttpStatus.OK).body(directorDetails);
        } catch (ActiveDirectorNotFoundException e) {
            LOGGER.errorContext(requestId, "Error retrieving active officer details.", e, logMap);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (ServiceException e) {
            LOGGER.errorContext(requestId, "Error retrieving active officer details.", e, logMap);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
