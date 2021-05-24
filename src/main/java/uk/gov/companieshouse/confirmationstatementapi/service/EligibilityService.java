package uk.gov.companieshouse.confirmationstatementapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.confirmationstatementapi.ConfirmationStatementApiApplication;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.model.response.EligibilityFailureResponse;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class EligibilityService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfirmationStatementApiApplication.APP_NAME);
    private final List<EligibilityRule<CompanyProfileApi>> eligibilityRules;

    @Autowired
    public EligibilityService(@Qualifier("confirmation-statement-eligibility-rules")
                                                    List<EligibilityRule<CompanyProfileApi>> eligibilityRules){
        this.eligibilityRules = eligibilityRules;
    }

    public Optional<EligibilityFailureResponse> checkCompanyEligibility(CompanyProfileApi companyProfile) {
        return checkCompanyEligibility(companyProfile, null);
    }

    public Optional<EligibilityFailureResponse> checkCompanyEligibility(CompanyProfileApi companyProfile, Transaction transaction) {
        try {
            for (EligibilityRule<CompanyProfileApi> eligibilityRule : eligibilityRules) {
                eligibilityRule.validate(companyProfile);
            }
        } catch (EligibilityException e) {
            String companyNumber = (transaction != null)?  transaction.getCompanyNumber() : companyProfile.getCompanyNumber();
            LOGGER.info(String.format("Company %s ineligible to use the service because %s", companyNumber, e.getEligibilityFailureReason().toString()));

            return Optional.of(new EligibilityFailureResponse(e.getEligibilityFailureReason()));
        }
        return Optional.empty();
    }
}
