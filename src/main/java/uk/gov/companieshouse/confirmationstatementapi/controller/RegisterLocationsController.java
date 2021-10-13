package uk.gov.companieshouse.confirmationstatementapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.model.json.registerlocation.RegisterLocationJson;
import uk.gov.companieshouse.confirmationstatementapi.service.RegisterLocationService;

import java.util.HashMap;
import java.util.List;

import static uk.gov.companieshouse.confirmationstatementapi.ConfirmationStatementApiApplication.LOGGER;
import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.ERIC_REQUEST_ID_KEY;

@RestController
public class RegisterLocationsController {

    private RegisterLocationService regLocService;

    @Autowired
    public RegisterLocationsController(RegisterLocationService regLocService) {
        this.regLocService = regLocService;
    }

    @GetMapping("/confirmation-statement/company/{companyNumber}/register-locations")
    public ResponseEntity<List<RegisterLocationJson>> getRegisterLocations(@PathVariable String companyNumber,
            @RequestHeader(value = ERIC_REQUEST_ID_KEY) String requestId) {

        var logMap = new HashMap<String, Object>();
        logMap.put("company_number", companyNumber);

        try {
            LOGGER.infoContext(requestId, "Calling service to retrieve register locations data.", logMap);
            var registerLocations = regLocService.getRegisterLocations(companyNumber);
            return ResponseEntity.status(HttpStatus.OK).body(registerLocations);
        } catch (ServiceException e) {
            LOGGER.errorContext(requestId, "Error retrieving register locations data.", e, logMap);
            return ResponseEntity.internalServerError().build();
        }
    }
}
