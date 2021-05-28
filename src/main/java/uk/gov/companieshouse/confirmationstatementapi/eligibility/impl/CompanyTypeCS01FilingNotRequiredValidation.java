package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;

import java.util.Set;

public class CompanyTypeCS01FilingNotRequiredValidation implements EligibilityRule<CompanyProfileApi> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyTypeCS01FilingNotRequiredValidation.class);

    private final Set<String> companyTypesNotRequiredToFile;

    public CompanyTypeCS01FilingNotRequiredValidation(Set<String> companyTypesNotRequiredToFile) {
        this.companyTypesNotRequiredToFile = companyTypesNotRequiredToFile;
    }

    @Override
    public void validate(CompanyProfileApi profileToValidate) throws EligibilityException {
        LOGGER.info("Validating Company Type is CS01 Required for: {}", profileToValidate.getCompanyNumber());
        var type = profileToValidate.getType();

        if(companyTypesNotRequiredToFile.contains(type)) {
            LOGGER.info("Company Type validation is CS01 Required failed for: {}", profileToValidate.getCompanyNumber());
            throw new EligibilityException(EligibilityStatusCode.INVALID_COMPANY_TYPE_CS01_FILING_NOT_REQUIRED);
        }
        LOGGER.info("Company Type validation is CS01 Required passed for: {}", profileToValidate.getCompanyNumber());
    }
}
