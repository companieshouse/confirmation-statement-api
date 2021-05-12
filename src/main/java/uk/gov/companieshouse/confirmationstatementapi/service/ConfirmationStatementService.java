package uk.gov.companieshouse.confirmationstatementapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;

import java.net.URI;
import java.util.List;

@Service
public class ConfirmationStatementService {

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
            return ResponseEntity.badRequest().body(e.getEligibilityFailureReason());
        }

        String createdUri = "/transactions/" + transaction.getId() + "/confirmation-statement/";

        return ResponseEntity.created(URI.create(createdUri)).body("Created");
    }
}
