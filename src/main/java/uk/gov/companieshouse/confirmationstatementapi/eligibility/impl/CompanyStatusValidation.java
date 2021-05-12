package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityFailureReason;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;

import java.util.Set;

public class CompanyStatusValidation implements EligibilityRule<CompanyProfileApi> {

    private final Set<String> allowedStatuses;

    public CompanyStatusValidation(Set<String> allowedStatuses) {
        this.allowedStatuses = allowedStatuses;
    }

    @Override
    public void validate(CompanyProfileApi input) throws EligibilityException {
        var status = input.getCompanyStatus();

        if (!allowedStatuses.contains(status)) {
            throw new EligibilityException(EligibilityFailureReason.INVALID_COMPANY_STATUS);
        }
    }
}
