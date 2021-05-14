package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityFailureReason;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;

import java.util.Set;

public class CompanyTypeValidationForWebFiling implements EligibilityRule<CompanyProfileApi> {

    private final Set<String> allowedTypes;

    public CompanyTypeValidationForWebFiling(Set<String> allowedTypes) {
        this.allowedTypes = allowedTypes;
    }

    @Override
    public void validate(CompanyProfileApi profileToValidate) throws EligibilityException {
        var status = profileToValidate.getType();

        if (!allowedTypes.contains(status)) {
            throw new EligibilityException(EligibilityFailureReason.INVALID_COMPANY_TYPE_FOR_WEB_FILING);
        }
    }
}
