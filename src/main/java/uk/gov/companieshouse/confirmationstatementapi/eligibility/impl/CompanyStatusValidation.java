package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import org.springframework.beans.factory.annotation.Value;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityFailureReason;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;

import java.util.Set;

public class CompanyStatusValidation implements EligibilityRule<CompanyProfileApi> {

    @Value("${ALLOWED_COMPANY_STATUSES}")
    private Set<String> allowedStatuses;

    @Override
    public void validate(CompanyProfileApi input) throws EligibilityException {
        var status = input.getCompanyStatus();

        if (!allowedStatuses.contains(status)) {
            throw new EligibilityException(EligibilityFailureReason.INVALID_COMPANY_STATUS);
        }
    }
}
