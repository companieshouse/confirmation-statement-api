package uk.gov.companieshouse.confirmationstatementapi.controller;

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
import uk.gov.companieshouse.confirmationstatementapi.model.json.ConfirmationStatementSubmissionJson;
import uk.gov.companieshouse.confirmationstatementapi.service.ConfirmationStatementService;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/transactions/{transaction_id}/confirmation-statement")
public class ConfirmationStatementController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfirmationStatementController.class);

    private final ConfirmationStatementService confirmationStatementService;

    @Autowired
    public ConfirmationStatementController(ConfirmationStatementService confirmationStatementService) {
        this.confirmationStatementService = confirmationStatementService;
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
        LOGGER.info("CS SUBMISSION JSON:" + confirmationStatementSubmissionJson.getData().getRegisteredOfficeAddressData().getSectionStatus().toString());
        return confirmationStatementService.updateConfirmationStatement(submissionId, confirmationStatementSubmissionJson);
    }

    @GetMapping("/{confirmation_statement_id}")
    public ResponseEntity<Object> getSubmission(@PathVariable("confirmation_statement_id") String submissionId) {
        var serviceResponse = confirmationStatementService.getConfirmationStatement(submissionId);
        return serviceResponse.<ResponseEntity<Object>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
