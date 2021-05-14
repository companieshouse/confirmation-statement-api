package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import java.util.Set;

import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityFailureReason;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;

public class CompanyTypeValidation implements EligibilityRule<CompanyProfileApi> {

    private final Set<String> allowedTypes;

    public CompanyTypeValidation(Set<String> allowedTypes) {
        this.allowedTypes = allowedTypes;
    }

    @Override
    public void validate(CompanyProfileApi profileToValidate) throws EligibilityException {
        var type = profileToValidate.getType();

        if(!allowedTypes.contains(type)) {
            throw new EligibilityException(EligibilityFailureReason.INVALID_COMPANY_TYPE);
        }
    }
}
