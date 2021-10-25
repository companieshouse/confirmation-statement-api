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
import uk.gov.companieshouse.confirmationstatementapi.utils.ApiLogger;

import java.util.HashMap;
import java.util.List;

import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.ERIC_REQUEST_ID_KEY;

@RestController
class PersonsOfSignificantControlController {

    @Autowired
    private ApiLogger apiLogger;

    @Autowired
    private PscService pscService;

    @GetMapping("/confirmation-statement/company/{companyNumber}/persons-of-significant-control")
    public ResponseEntity<List<PersonOfSignificantControlJson>> getPersonsOfSignificantControl(
            @PathVariable String companyNumber,
            @RequestHeader(value = ERIC_REQUEST_ID_KEY) String requestId) {

        var logMap = new HashMap<String, Object>();
        logMap.put("company_number", companyNumber);

        String sanitizedCompanyNumber = null;
        if (companyNumber != null) {
            sanitizedCompanyNumber = companyNumber.replaceAll("[\n|\r|\t]", "_");
        }

        try {
            apiLogger.infoContext(requestId, String.format("Calling PscService to retrieve persons of significant control for companyNumber %s", sanitizedCompanyNumber), logMap);
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

    private void logErrorMessage(String requestId, String sanitizedCompanyNumber, HashMap<String, Object> logMap, Exception e) {
        apiLogger.errorContext(requestId, String.format("Calling PscService to retrieve persons of significant control for companyNumber %s", sanitizedCompanyNumber), e, logMap);
    }
}
