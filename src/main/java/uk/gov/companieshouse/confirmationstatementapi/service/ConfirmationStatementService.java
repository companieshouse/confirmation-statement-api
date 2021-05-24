package uk.gov.companieshouse.confirmationstatementapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.confirmationstatementapi.ConfirmationStatementApiApplication;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.model.response.EligibilityFailureResponse;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Service
public class ConfirmationStatementService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfirmationStatementApiApplication.APP_NAME);

    private final CompanyProfileService companyProfileService;
    private final EligibilityService eligibilityService;

    @Autowired
    public ConfirmationStatementService(CompanyProfileService companyProfileService,
                                        EligibilityService eligibilityService) {
        this.companyProfileService = companyProfileService;
        this.eligibilityService = eligibilityService;
    }

    public ResponseEntity<Object> createConfirmationStatement(Transaction transaction) throws ServiceException {
        var companyProfile = companyProfileService.getCompanyProfile(transaction.getCompanyNumber());
        Optional<EligibilityFailureResponse> validationErrorResponseBody = eligibilityService.checkCompanyEligibility(companyProfile, transaction) ;
        if(validationErrorResponseBody.isPresent()) {
            return ResponseEntity.badRequest().body(validationErrorResponseBody.get());
        }

        String createdUri = "/transactions/" + transaction.getId() + "/confirmation-statement/";

        LOGGER.info("Confirmation Statement created for transaction id: " + transaction.getId());
        return ResponseEntity.created(URI.create(createdUri)).body("Created");
    }
}
