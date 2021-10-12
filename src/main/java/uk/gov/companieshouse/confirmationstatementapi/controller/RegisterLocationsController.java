package uk.gov.companieshouse.confirmationstatementapi.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.model.json.registerlocation.RegisterLocationJson;
import uk.gov.companieshouse.confirmationstatementapi.service.RegisterLocationService;

import java.util.List;

@RestController
public class RegisterLocationsController {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterLocationsController.class);

    private RegisterLocationService regLocService;

    @Autowired
    public RegisterLocationsController(RegisterLocationService regLocService) {
        this.regLocService = regLocService;
    }

    @GetMapping("/confirmation-statement/company/{company-number}/register-locations")
    public ResponseEntity<List<RegisterLocationJson>> getRegisterLocations(@PathVariable("company-number") String companyNumber) {
        try {
            LOGGER.info("Calling service to retrieve register locations data");
            var registerLocations = regLocService.getRegisterLocations(companyNumber);
            return ResponseEntity.status(HttpStatus.OK).body(registerLocations);
        } catch (ServiceException e) {
            LOGGER.error("Error retrieving register locations data ", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
