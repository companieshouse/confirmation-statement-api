package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.ConfirmationStatementApiApplication;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.util.Set;

public class CompanyStatusValidation implements EligibilityRule<CompanyProfileApi> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfirmationStatementApiApplication.APP_NAME);

    private final Set<String> allowedStatuses;

    public CompanyStatusValidation(Set<String> allowedStatuses) {
        this.allowedStatuses = allowedStatuses;
    }

    @Override
    public void validate(CompanyProfileApi profileToValidate) throws EligibilityException {
        LOGGER.info("Validating Company Status for: " + profileToValidate.getCompanyNumber());
        var status = profileToValidate.getCompanyStatus();

        if (!allowedStatuses.contains(status)) {
            LOGGER.info("Company Status validation failed for: " + profileToValidate.getCompanyNumber());
            throw new EligibilityException(EligibilityStatusCode.INVALID_COMPANY_STATUS);
        }
        LOGGER.info("Company Status validation passed for: " + profileToValidate.getCompanyNumber());
    }
}
