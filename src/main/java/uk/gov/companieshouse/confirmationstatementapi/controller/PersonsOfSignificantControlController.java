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
import uk.gov.companieshouse.confirmationstatementapi.model.json.PersonOfSignificantControlJson;
import uk.gov.companieshouse.confirmationstatementapi.service.PscService;

import java.util.List;

@RestController
class PersonsOfSignificantControlController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonsOfSignificantControlController.class);

    @Autowired
    private PscService pscService;

    @GetMapping("/confirmation-statement/company/{company-number}/persons-of-significant-control")
    public ResponseEntity<List<PersonOfSignificantControlJson>> getPersonsOfSignificantControl(@PathVariable("company-number") String companyNumber) {
        String sanitizedCompanyNumber = null;
        if (companyNumber != null) {
            sanitizedCompanyNumber = companyNumber.replaceAll("[\n|\r|\t]", "_");
        }

        try {
            LOGGER.info("Calling PscService to retrieve persons of significant control for companyNumber {}", sanitizedCompanyNumber);
            var pscs = pscService.getPSCsFromOracle(sanitizedCompanyNumber);
            return ResponseEntity.status(HttpStatus.OK).body(pscs);
        } catch (ServiceException e) {
            logErrorMessage(sanitizedCompanyNumber, e);
            return ResponseEntity.internalServerError().build();
        } catch (Exception e) {
            logErrorMessage(sanitizedCompanyNumber, e);
            throw e;
        }
    }

    private void logErrorMessage(String sanitizedCompanyNumber, Exception e) {
        LOGGER.error("Error retrieving persons of significant control for companyNumber {}", sanitizedCompanyNumber, e);
    }
}
