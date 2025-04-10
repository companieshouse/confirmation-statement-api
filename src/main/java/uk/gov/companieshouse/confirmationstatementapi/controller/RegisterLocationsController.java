package uk.gov.companieshouse.confirmationstatementapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.api.model.company.RegisterLocationJson;
import uk.gov.companieshouse.confirmationstatementapi.service.RegisterLocationService;
import uk.gov.companieshouse.confirmationstatementapi.utils.ApiLogger;

import java.util.HashMap;
import java.util.List;

import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.ERIC_REQUEST_ID_KEY;
import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.TRANSACTION_ID_KEY;

@RestController
public class RegisterLocationsController {

    private final RegisterLocationService regLocService;

    @Autowired
    public RegisterLocationsController(RegisterLocationService regLocService) {
        this.regLocService = regLocService;
    }

    @GetMapping("/transactions/{transaction_id}/confirmation-statement/{confirmation_statement_id}/register-locations")
    public ResponseEntity<List<RegisterLocationJson>> getRegisterLocations(
            @RequestAttribute("transaction") Transaction transaction,
            @PathVariable(TRANSACTION_ID_KEY) String transactionId,
            @RequestHeader(value = ERIC_REQUEST_ID_KEY) String requestId) {

        var logMap = new HashMap<String, Object>();
        logMap.put(TRANSACTION_ID_KEY, transactionId);

        try {
            ApiLogger.infoContext(requestId, "Calling service to retrieve register locations data.", logMap);
            var registerLocations = regLocService.getRegisterLocations(transaction.getCompanyNumber());
            return ResponseEntity.status(HttpStatus.OK).body(registerLocations);
        } catch (ServiceException e) {
            ApiLogger.errorContext(requestId, "Error retrieving register locations data.", e, logMap);
            return ResponseEntity.internalServerError().build();
        }
    }
}
