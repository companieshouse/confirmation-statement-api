package uk.gov.companieshouse.confirmationstatementapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.exception.StatementOfCapitalNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.model.json.statementofcapital.StatementOfCapitalJson;
import uk.gov.companieshouse.confirmationstatementapi.service.StatementOfCapitalService;

import java.util.HashMap;

import static uk.gov.companieshouse.confirmationstatementapi.ConfirmationStatementApiApplication.LOGGER;
import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.ERIC_REQUEST_ID_KEY;

@RestController
public class StatementOfCapitalController {

    private final StatementOfCapitalService statementOfCapitalService;

    @Autowired
    public StatementOfCapitalController(StatementOfCapitalService statementOfCapitalService) {
        this.statementOfCapitalService = statementOfCapitalService;
    }

    @GetMapping("/confirmation-statement/company/{companyNumber}/statement-of-capital")
    public ResponseEntity<StatementOfCapitalJson> getStatementOfCapital(
            @PathVariable String companyNumber,
            @RequestHeader(value = ERIC_REQUEST_ID_KEY) String requestId) {

        var logMap = new HashMap<String, Object>();
        logMap.put("company_number", companyNumber);

        try {
            LOGGER.infoContext(requestId, "Calling service to retrieve statement of capital data.", logMap);
            var statementOfCapital = statementOfCapitalService.getStatementOfCapital(companyNumber);
            return ResponseEntity.status(HttpStatus.OK).body(statementOfCapital);
        } catch (StatementOfCapitalNotFoundException e) {
            LOGGER.infoContext(requestId, e.getMessage(), logMap);
            return ResponseEntity.notFound().build();
        } catch (ServiceException e) {
            LOGGER.errorContext(requestId, "Error retrieving statement of capital data.", e, logMap);
            return ResponseEntity.internalServerError().build();
        }
    }
}
