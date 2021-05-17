package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityFailureReason;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;

import java.util.Set;

public class CompanyTypeValidationPaperOnly implements EligibilityRule<CompanyProfileApi> {

    private final Set<String> paperOnlyCompanyTypes;

    public CompanyTypeValidationPaperOnly(Set<String> paperOnlyCompanyTypes) {
        this.paperOnlyCompanyTypes = paperOnlyCompanyTypes;
    }

    @Override
    public void validate(CompanyProfileApi profileToValidate) throws EligibilityException {
        var companyType = profileToValidate.getType();

        if (paperOnlyCompanyTypes.contains(companyType)) {
            throw new EligibilityException(EligibilityFailureReason.INVALID_COMPANY_TYPE_PAPER_ONLY);
        }
    }
}
