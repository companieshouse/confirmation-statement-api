package uk.gov.companieshouse.confirmationstatementapi.contoller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.model.StatementOfCapital;
import uk.gov.companieshouse.confirmationstatementapi.model.json.ConfirmationStatementSubmissionJson;
import uk.gov.companieshouse.confirmationstatementapi.service.ConfirmationStatementService;
import uk.gov.companieshouse.confirmationstatementapi.service.StatementOfCapitalService;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/transactions/{transaction_id}/confirmation-statement")
public class ConfirmationStatementController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfirmationStatementController.class);

    private final ConfirmationStatementService confirmationStatementService;

    private final StatementOfCapitalService statementOfCapitalService;

    @Autowired
    public ConfirmationStatementController(ConfirmationStatementService confirmationStatementService,
                                           StatementOfCapitalService statementOfCapitalService) {
        this.confirmationStatementService = confirmationStatementService;
        this.statementOfCapitalService = statementOfCapitalService;
    }

    @PostMapping("/")
    public ResponseEntity<Object> createNewSubmission(@RequestAttribute("transaction") Transaction transaction, HttpServletRequest request) {

        String passthroughHeader = request
                .getHeader(ApiSdkManager.getEricPassthroughTokenHeader());
        try {
            return confirmationStatementService.createConfirmationStatement(transaction, passthroughHeader);
        } catch (ServiceException e) {
            LOGGER.error("Error Creating Confirmation Statement", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{confirmation_statement_id}")
    public ResponseEntity<Object> updateSubmission(@RequestBody ConfirmationStatementSubmissionJson confirmationStatementSubmissionJson,
                                                   @PathVariable("confirmation_statement_id") String submissionId) {
        return confirmationStatementService.updateConfirmationStatement(submissionId, confirmationStatementSubmissionJson);
    }

    @GetMapping("/{companyNumber}/statement-of-capital")
    public ResponseEntity<StatementOfCapital> getStatementOfCapital(@PathVariable String companyNumber) {
        try {
            LOGGER.info("Calling service to retrieve statement of capital for company number " + companyNumber);
            StatementOfCapital statementOfCapital = statementOfCapitalService.getStatementOfCapital(companyNumber);
            return ResponseEntity.status(HttpStatus.OK).body(statementOfCapital);
        } catch (ServiceException e) {
            LOGGER.error("Error retreiving statment of capital data ", e);
            return ResponseEntity.notFound().build();
        }
    }
}
