package uk.gov.companieshouse.confirmationstatementapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.confirmationstatementapi.exception.RegisteredEmailNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.service.EmailService;
import uk.gov.companieshouse.confirmationstatementapi.utils.ApiLogger;

import java.util.HashMap;

import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.ERIC_REQUEST_ID_KEY;
import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.COMPANY_NUMBER;

@RestController
@RequestMapping("/private/confirmation-statement")
public class EmailController {

    private final EmailService emailService;

    @Autowired
    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/company/{company-number}/registered-email-address")
    public ResponseEntity<String> getRegisteredEmailAddress(
            @PathVariable(COMPANY_NUMBER) String companyNumber,
            @RequestHeader(value = ERIC_REQUEST_ID_KEY) String requestId) {

        var map = new HashMap<String, Object>();
        map.put(COMPANY_NUMBER, companyNumber);

        try {
            ApiLogger.infoContext(requestId, "Calling service to retrieve registered email address", map);
            var rea = emailService.getRegisteredEmailAddress(companyNumber);
            return ResponseEntity.status(HttpStatus.OK).body(rea);
        } catch (RegisteredEmailNotFoundException renfe) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(renfe.getMessage());
        } catch (ServiceException e) {
            ApiLogger.errorContext(requestId, "Error retrieving registered email address", e, map);
            return ResponseEntity.internalServerError().build();
        }
    }
}
