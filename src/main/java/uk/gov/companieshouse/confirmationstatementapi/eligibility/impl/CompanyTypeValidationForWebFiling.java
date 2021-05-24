package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;

import java.util.Set;

public class CompanyTypeValidationForWebFiling implements EligibilityRule<CompanyProfileApi> {

    private final Set<String> webFilingTypes;

    public CompanyTypeValidationForWebFiling(Set<String> webFilingTypes) {
        this.webFilingTypes = webFilingTypes;
    }

    @Override
    public void validate(CompanyProfileApi profileToValidate) throws EligibilityException {

        if (webFilingTypes.contains(profileToValidate.getType())) {
            throw new EligibilityException(EligibilityStatusCode.INVALID_COMPANY_TYPE_USE_WEB_FILING);
        }
    }
}
