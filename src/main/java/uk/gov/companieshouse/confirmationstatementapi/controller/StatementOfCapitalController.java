package uk.gov.companieshouse.confirmationstatementapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.model.json.statementofcapital.StatementOfCapitalJson;
import uk.gov.companieshouse.confirmationstatementapi.service.StatementOfCapitalService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.util.HashMap;

import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.ERIC_REQUEST_ID;

@RestController
public class StatementOfCapitalController {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatementOfCapitalController.class.getName());

    private final StatementOfCapitalService statementOfCapitalService;

    @Autowired
    public StatementOfCapitalController(StatementOfCapitalService statementOfCapitalService) {
        this.statementOfCapitalService = statementOfCapitalService;
    }

    @GetMapping("/confirmation-statement/company/{companyNumber}/statement-of-capital")
    public ResponseEntity<StatementOfCapitalJson> getStatementOfCapital(@PathVariable String companyNumber,
            @RequestHeader(value = ERIC_REQUEST_ID) String requestId) {

        var logMap = new HashMap<String, Object>();
        logMap.put("requestId", requestId);
        logMap.put("companyNumber", companyNumber);

        try {
            LOGGER.infoContext(requestId, "Calling service to retrieve statement of capital data.", logMap);
            var statementOfCapital = statementOfCapitalService.getStatementOfCapital(companyNumber);
            return ResponseEntity.status(HttpStatus.OK).body(statementOfCapital);
        } catch (ServiceException e) {
            LOGGER.errorContext(requestId, "Error retrieving statement of capital data.", e, logMap);
            return ResponseEntity.notFound().build();
        }
    }
}
