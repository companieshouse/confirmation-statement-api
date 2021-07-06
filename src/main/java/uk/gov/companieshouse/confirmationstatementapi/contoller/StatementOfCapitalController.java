package uk.gov.companieshouse.confirmationstatementapi.contoller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.model.StatementOfCapital;
import uk.gov.companieshouse.confirmationstatementapi.service.StatementOfCapitalService;

@RestController
@RequestMapping("/transactions/{transaction_id}/confirmation-statement")
public class StatementOfCapitalController {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatementOfCapitalController.class);

    private final StatementOfCapitalService statementOfCapitalService;

    @Autowired
    public StatementOfCapitalController(StatementOfCapitalService statementOfCapitalService) {
        this.statementOfCapitalService = statementOfCapitalService;
    }

    @GetMapping("/{companyNumber}/statement-of-capital")
    public ResponseEntity<StatementOfCapital> getStatementOfCapital(@PathVariable String companyNumber) {
        try {
            LOGGER.info("Calling service to retrieve statement of capital data");
            StatementOfCapital statementOfCapital = statementOfCapitalService.getStatementOfCapital(companyNumber);
            return ResponseEntity.status(HttpStatus.OK).body(statementOfCapital);
        } catch (ServiceException e) {
            LOGGER.error("Error retreiving statment of capital data ", e);
            return ResponseEntity.notFound().build();
        }
    }
}
