package uk.gov.companieshouse.confirmationstatementapi.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.confirmationstatementapi.exception.ActiveOfficerNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.model.ActiveOfficerDetails;
import uk.gov.companieshouse.confirmationstatementapi.service.OfficerService;

@RestController
public class OfficerController {

    @Autowired
    private OfficerService officerService;

    private static final Logger LOGGER = LoggerFactory.getLogger(OfficerController.class);

    @GetMapping("/confirmation-statement/company/{companyNumber}/active-officer-details")
    public ResponseEntity<ActiveOfficerDetails> getActiveDirectorDetails(@PathVariable String companyNumber) {
        try {
            LOGGER.info("Calling service to retrieve the active officer details.");
            var directorDetails = officerService.getActiveDirectorDetails(companyNumber);
            return ResponseEntity.status(HttpStatus.OK).body(directorDetails);
        } catch (ActiveOfficerNotFoundException e) {
            LOGGER.error("Error retrieving active officer details.", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (ServiceException e) {
            LOGGER.error("Error retrieving active officer details.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
