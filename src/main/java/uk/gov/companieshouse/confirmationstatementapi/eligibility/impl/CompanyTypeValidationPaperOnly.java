package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;

import java.util.Set;

import static uk.gov.companieshouse.confirmationstatementapi.ConfirmationStatementApiApplication.LOGGER;

public class CompanyTypeValidationPaperOnly implements EligibilityRule<CompanyProfileApi> {

    private final Set<String> paperOnlyCompanyTypes;

    public CompanyTypeValidationPaperOnly(Set<String> paperOnlyCompanyTypes) {
        this.paperOnlyCompanyTypes = paperOnlyCompanyTypes;
    }

    @Override
    public void validate(CompanyProfileApi profileToValidate) throws EligibilityException {
        LOGGER.info(String.format("Validating Company Type Paper Filing Only for: %s", profileToValidate.getCompanyNumber()));
        var companyType = profileToValidate.getType();

        if (paperOnlyCompanyTypes.contains(companyType)) {
            LOGGER.info(String.format("Company Type Paper Filing Only failed for: %s", profileToValidate.getCompanyNumber()));
            throw new EligibilityException(EligibilityStatusCode.INVALID_COMPANY_TYPE_PAPER_FILING_ONLY);
        }
        LOGGER.info(String.format("Company Type Paper Filing Only passed for: %s", profileToValidate.getCompanyNumber()));
    }
}
