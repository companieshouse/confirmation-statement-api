package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.utils.ApiLogger;

import java.util.Set;

public class CompanyStatusValidation implements EligibilityRule<CompanyProfileApi> {

    private final Set<String> allowedStatuses;

    public CompanyStatusValidation(Set<String> allowedStatuses) {
        this.allowedStatuses = allowedStatuses;
    }

    @Override
    public void validate(CompanyProfileApi profileToValidate) throws EligibilityException {
        ApiLogger.info(String.format("Validating Company Status for: %s", profileToValidate.getCompanyNumber()));
        var status = profileToValidate.getCompanyStatus();

        if (!allowedStatuses.contains(status)) {
            ApiLogger.info(String.format("Company Status validation failed for: %s", profileToValidate.getCompanyNumber()));
            throw new EligibilityException(EligibilityStatusCode.INVALID_COMPANY_STATUS);
        }
        ApiLogger.info(String.format("Company Status validation passed for: %s", profileToValidate.getCompanyNumber()));
    }
}
