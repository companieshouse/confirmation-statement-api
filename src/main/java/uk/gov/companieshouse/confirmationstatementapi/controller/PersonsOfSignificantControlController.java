package uk.gov.companieshouse.confirmationstatementapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.model.json.PersonOfSignificantControlJson;
import uk.gov.companieshouse.confirmationstatementapi.service.PscService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.util.HashMap;
import java.util.List;

import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.ERIC_REQUEST_ID;

@RestController
class PersonsOfSignificantControlController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonsOfSignificantControlController.class.getName());

    @Autowired
    private PscService pscService;

    @GetMapping("/confirmation-statement/company/{companyNumber}/persons-of-significant-control")
    public ResponseEntity<List<PersonOfSignificantControlJson>> getPersonsOfSignificantControl(@PathVariable String companyNumber,
            @RequestHeader(value = ERIC_REQUEST_ID) String requestId) {

        var logMap = new HashMap<String, Object>();
        logMap.put("requestId", requestId);
        logMap.put("companyNumber", companyNumber);

        String sanitizedCompanyNumber = null;
        if (companyNumber != null) {
            sanitizedCompanyNumber = companyNumber.replaceAll("[\n|\r|\t]", "_");
        }

        try {
            LOGGER.infoContext(requestId, String.format("Calling PscService to retrieve persons of significant control for companyNumber %s", sanitizedCompanyNumber), logMap);
            var pscs = pscService.getPSCsFromOracle(sanitizedCompanyNumber);
            return ResponseEntity.status(HttpStatus.OK).body(pscs);
        } catch (ServiceException e) {
            logErrorMessage(requestId, sanitizedCompanyNumber, logMap, e);
            return ResponseEntity.internalServerError().build();
        } catch (Exception e) {
            logErrorMessage(requestId, sanitizedCompanyNumber, logMap, e);
            throw e;
        }
    }

    private void logErrorMessage(String requestId, String sanitizedCompanyNumber, HashMap logMap, Exception e) {
        LOGGER.errorContext(requestId, String.format("Calling PscService to retrieve persons of significant control for companyNumber %s", sanitizedCompanyNumber), e, logMap);
    }
}
