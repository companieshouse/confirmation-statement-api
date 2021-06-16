package uk.gov.companieshouse.confirmationstatementapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.exception.CompanyNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.model.ConfirmationStatementSubmission;
import uk.gov.companieshouse.confirmationstatementapi.repository.ConfirmationStatementSubmissionsRepository;

import java.net.URI;
import java.util.Collections;

@Service
public class ConfirmationStatementService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfirmationStatementService.class);

    private final CompanyProfileService companyProfileService;
    private final EligibilityService eligibilityService;
    private final ConfirmationStatementSubmissionsRepository confirmationStatementSubmissionsRepository;

    @Autowired
    public ConfirmationStatementService(CompanyProfileService companyProfileService,
                                        EligibilityService eligibilityService,
                                        ConfirmationStatementSubmissionsRepository confirmationStatementSubmissionsRepository
    ) {
        this.companyProfileService = companyProfileService;
        this.eligibilityService = eligibilityService;
        this.confirmationStatementSubmissionsRepository = confirmationStatementSubmissionsRepository;
    }

    public ResponseEntity<Object> createConfirmationStatement(Transaction transaction) throws ServiceException {
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

        var newSubmission = new ConfirmationStatementSubmission();
        var insertedSubmission = confirmationStatementSubmissionsRepository.insert(newSubmission);

        String createdUri = "/transactions/" + transaction.getId() + "/confirmation-statement/" + insertedSubmission.getId();
        insertedSubmission.setLinks(Collections.singletonMap("self", createdUri));

        var updatedSubmission = confirmationStatementSubmissionsRepository.save(insertedSubmission);

        LOGGER.info("Confirmation Statement created for transaction id: {} with Submission id: {}", transaction.getId(), updatedSubmission.getId());
        return ResponseEntity.created(URI.create(createdUri)).body("Created");
    }
}
