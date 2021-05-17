package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import java.util.Set;

import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityFailureReason;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;

public class CompanyTypeCS01FilingNotRequiredValidation implements EligibilityRule<CompanyProfileApi> {

    private final Set<String> companyTypesNotRequiredToFile;

    public CompanyTypeCS01FilingNotRequiredValidation(Set<String> companyTypesNotRequiredToFile) {
        this.companyTypesNotRequiredToFile = companyTypesNotRequiredToFile;
    }

    @Override
    public void validate(CompanyProfileApi profileToValidate) throws EligibilityException {
        var type = profileToValidate.getType();

        if(companyTypesNotRequiredToFile.contains(type)) {
            throw new EligibilityException(EligibilityFailureReason.INVALID_COMPANY_TYPE_CS01_FILING_NOT_REQUIRED);
        }
    }
}
