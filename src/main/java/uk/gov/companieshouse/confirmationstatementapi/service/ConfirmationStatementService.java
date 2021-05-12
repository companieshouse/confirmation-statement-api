package uk.gov.companieshouse.confirmationstatementapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.impl.CompanyStatusValidation;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConfirmationStatementService {

    private final CompanyProfileService companyProfileService;

    private final List<EligibilityRule<CompanyProfileApi>> eligibilityRules;

    @Autowired
    public ConfirmationStatementService(CompanyProfileService companyProfileService) {
        this.companyProfileService = companyProfileService;

        this.eligibilityRules = new ArrayList<>();
        this.eligibilityRules.add(new CompanyStatusValidation());
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

        return ResponseEntity.accepted().body("ACCEPTED");
    }
}
