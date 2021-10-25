package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;

import java.util.Set;

import static uk.gov.companieshouse.confirmationstatementapi.ConfirmationStatementApiApplication.LOGGER;

public class CompanyTypeCS01FilingNotRequiredValidation implements EligibilityRule<CompanyProfileApi> {

    private final Set<String> companyTypesNotRequiredToFile;

    public CompanyTypeCS01FilingNotRequiredValidation(Set<String> companyTypesNotRequiredToFile) {
        this.companyTypesNotRequiredToFile = companyTypesNotRequiredToFile;
    }

    @Override
    public void validate(CompanyProfileApi profileToValidate) throws EligibilityException {
        LOGGER.info(String.format("Validating Company Type is CS01 Required for: %s", profileToValidate.getCompanyNumber()));
        var type = profileToValidate.getType();

        if(companyTypesNotRequiredToFile.contains(type)) {
            LOGGER.info(String.format("Company Type validation is CS01 Required failed for: %s", profileToValidate.getCompanyNumber()));
            throw new EligibilityException(EligibilityStatusCode.INVALID_COMPANY_TYPE_CS01_FILING_NOT_REQUIRED);
        }
        LOGGER.info(String.format("Company Type validation is CS01 Required passed for: %s", profileToValidate.getCompanyNumber()));
    }
}
