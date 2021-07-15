package uk.gov.companieshouse.confirmationstatementapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.transaction.Resource;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.exception.CompanyNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.ConfirmationStatementSubmissionDao;
import uk.gov.companieshouse.confirmationstatementapi.model.json.ConfirmationStatementSubmissionJson;
import uk.gov.companieshouse.confirmationstatementapi.model.mapping.ConfirmationStatementJsonDaoMapper;
import uk.gov.companieshouse.confirmationstatementapi.repository.ConfirmationStatementSubmissionsRepository;

import java.net.URI;
import java.util.Collections;
import java.util.Optional;

@Service
public class ConfirmationStatementService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfirmationStatementService.class);

    private final CompanyProfileService companyProfileService;
    private final EligibilityService eligibilityService;
    private final ConfirmationStatementSubmissionsRepository confirmationStatementSubmissionsRepository;
    private final TransactionService transactionService;
    private final ConfirmationStatementJsonDaoMapper confirmationStatementJsonDaoMapper;

    @Autowired
    public ConfirmationStatementService(CompanyProfileService companyProfileService, EligibilityService eligibilityService, ConfirmationStatementSubmissionsRepository confirmationStatementSubmissionsRepository, TransactionService transactionService, ConfirmationStatementJsonDaoMapper confirmationStatementJsonDaoMapper) {
        this.companyProfileService = companyProfileService;
        this.eligibilityService = eligibilityService;
        this.confirmationStatementSubmissionsRepository = confirmationStatementSubmissionsRepository;
        this.transactionService = transactionService;
        this.confirmationStatementJsonDaoMapper = confirmationStatementJsonDaoMapper;
    }

    public ResponseEntity<Object> createConfirmationStatement(Transaction transaction, String passthroughHeader) throws ServiceException {
        CompanyProfileApi companyProfile;
        try {
            companyProfile = companyProfileService.getCompanyProfile(transaction.getCompanyNumber());
        } catch (CompanyNotFoundException e) {
            throw new ServiceException("Error retrieving company profile", e);
        }
        var companyValidationResponse = eligibilityService.checkCompanyEligibility(companyProfile) ;

        if(EligibilityStatusCode.COMPANY_VALID_FOR_SERVICE != companyValidationResponse.getEligibilityStatusCode()) {
            return ResponseEntity.badRequest().body(companyValidationResponse);
        }

        var newSubmission = new ConfirmationStatementSubmissionDao();
        var insertedSubmission = confirmationStatementSubmissionsRepository.insert(newSubmission);

        String createdUri = "/transactions/" + transaction.getId() + "/confirmation-statement/" + insertedSubmission.getId();
        insertedSubmission.setLinks(Collections.singletonMap("self", createdUri));

        var updatedSubmission = confirmationStatementSubmissionsRepository.save(insertedSubmission);

        var csResource = new Resource();
        csResource.setKind("confirmation-statement");
        csResource.setLinks(Collections.singletonMap("resource", createdUri));

        transaction.setResources(Collections.singletonMap(createdUri, csResource));

        transactionService.updateTransaction(transaction, passthroughHeader);
        LOGGER.info("Confirmation Statement created for transaction id: {} with Submission id: {}", transaction.getId(), updatedSubmission.getId());

        var responseObject = confirmationStatementJsonDaoMapper.daoToJson(updatedSubmission);
        return ResponseEntity.created(URI.create(createdUri)).body(responseObject);
    }

    public ResponseEntity<Object> updateConfirmationStatement(String submissionId, ConfirmationStatementSubmissionJson confirmationStatementSubmissionJson) {
        // Check Submission exists
        var submission = confirmationStatementSubmissionsRepository.findById(submissionId);

        if (submission.isPresent()) {
            // Save updated submission to database
            LOGGER.info("{}: Confirmation Statement Submission found. About to update", submission.get().getId());
            var dao = confirmationStatementJsonDaoMapper.jsonToDao(confirmationStatementSubmissionJson);
            var savedResponse = confirmationStatementSubmissionsRepository.save(dao);
            LOGGER.info("{}: Confirmation Statement Submission updated", savedResponse.getId());
            return ResponseEntity.ok(savedResponse);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public Optional<ConfirmationStatementSubmissionJson> getConfirmationStatement(String submissionId) {
        // Check Submission exists
        var submission = confirmationStatementSubmissionsRepository.findById(submissionId);

        if (submission.isPresent()) {
            LOGGER.info("{}: Confirmation Statement Submission found. About to return", submission.get().getId());

            var json = confirmationStatementJsonDaoMapper.daoToJson(submission.get());
            return Optional.of(json);
        } else {
            return Optional.empty();
        }
    }
}
