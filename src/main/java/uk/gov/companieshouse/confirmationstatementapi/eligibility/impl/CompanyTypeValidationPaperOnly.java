package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;

import java.util.Set;

public class CompanyTypeValidationPaperOnly implements EligibilityRule<CompanyProfileApi> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyTypeValidationPaperOnly.class);

    private final Set<String> paperOnlyCompanyTypes;

    public CompanyTypeValidationPaperOnly(Set<String> paperOnlyCompanyTypes) {
        this.paperOnlyCompanyTypes = paperOnlyCompanyTypes;
    }

    @Override
    public void validate(CompanyProfileApi profileToValidate) throws EligibilityException {
        LOGGER.info("Validating Company Type Paper Filing Only for: {}", profileToValidate.getCompanyNumber());
        var companyType = profileToValidate.getType();

        if (paperOnlyCompanyTypes.contains(companyType)) {
            LOGGER.info("Company Type Paper Filing Only failed for: {}", profileToValidate.getCompanyNumber());
            throw new EligibilityException(EligibilityStatusCode.INVALID_COMPANY_TYPE_PAPER_FILING_ONLY);
        }
        LOGGER.info("Company Type Paper Filing Only passed for: {}", profileToValidate.getCompanyNumber());
    }
}
