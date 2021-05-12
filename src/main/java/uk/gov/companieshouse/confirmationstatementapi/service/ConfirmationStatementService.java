package uk.gov.companieshouse.confirmationstatementapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.confirmationstatementapi.ConfirmationStatementApiApplication;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.net.URI;
import java.util.List;

@Service
public class ConfirmationStatementService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfirmationStatementApiApplication.APP_NAME);

    private final CompanyProfileService companyProfileService;

    private final List<EligibilityRule<CompanyProfileApi>> eligibilityRules;

    @Autowired
    public ConfirmationStatementService(CompanyProfileService companyProfileService,
                                        @Qualifier("confirmation-statement-eligibility-rules") List<EligibilityRule<CompanyProfileApi>> eligibilityRules) {
        this.companyProfileService = companyProfileService;
        this.eligibilityRules = eligibilityRules;
    }

    public ResponseEntity<Object> createConfirmationStatement(Transaction transaction) throws ServiceException {
        var companyProfile = companyProfileService.getCompanyProfile(transaction.getCompanyNumber());

        try {
            for (EligibilityRule<CompanyProfileApi> eligibilityRule : eligibilityRules) {
                eligibilityRule.validate(companyProfile);
            }
        } catch (EligibilityException e) {
            LOGGER.info(String.format("Company %s ineligible to use the service because %s", transaction.getCompanyNumber(), e.getEligibilityFailureReason().toString()));
            return ResponseEntity.badRequest().body(e.getEligibilityFailureReason());
        }

        String createdUri = "/transactions/" + transaction.getId() + "/confirmation-statement/";

        LOGGER.info("Confirmation Statement created for transaction id: " + transaction.getId());
        return ResponseEntity.created(URI.create(createdUri)).body("Created");
    }
}
