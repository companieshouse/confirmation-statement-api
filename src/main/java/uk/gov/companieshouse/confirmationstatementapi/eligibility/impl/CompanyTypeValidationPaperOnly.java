package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.utils.ApiLogger;

import java.util.Set;

public class CompanyTypeValidationPaperOnly implements EligibilityRule<CompanyProfileApi> {

    @Autowired
    private ApiLogger apiLogger;

    private final Set<String> paperOnlyCompanyTypes;

    public CompanyTypeValidationPaperOnly(Set<String> paperOnlyCompanyTypes) {
        this.paperOnlyCompanyTypes = paperOnlyCompanyTypes;
    }

    @Override
    public void validate(CompanyProfileApi profileToValidate) throws EligibilityException {
        apiLogger.info(String.format("Validating Company Type Paper Filing Only for: %s", profileToValidate.getCompanyNumber()));
        var companyType = profileToValidate.getType();

        if (paperOnlyCompanyTypes.contains(companyType)) {
            apiLogger.info(String.format("Company Type Paper Filing Only failed for: %s", profileToValidate.getCompanyNumber()));
            throw new EligibilityException(EligibilityStatusCode.INVALID_COMPANY_TYPE_PAPER_FILING_ONLY);
        }
        apiLogger.info(String.format("Company Type Paper Filing Only passed for: %s", profileToValidate.getCompanyNumber()));
    }
}
