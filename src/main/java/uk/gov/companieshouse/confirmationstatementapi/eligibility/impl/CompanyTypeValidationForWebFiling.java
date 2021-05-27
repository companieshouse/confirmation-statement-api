package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.ConfirmationStatementApiApplication;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.util.Set;

public class CompanyTypeValidationForWebFiling implements EligibilityRule<CompanyProfileApi> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfirmationStatementApiApplication.APP_NAME);

    private final Set<String> webFilingTypes;

    public CompanyTypeValidationForWebFiling(Set<String> webFilingTypes) {
        this.webFilingTypes = webFilingTypes;
    }

    @Override
    public void validate(CompanyProfileApi profileToValidate) throws EligibilityException {
        LOGGER.info("Validating Company Type Should Use Web Filing for: " + profileToValidate.getCompanyNumber());
        if (webFilingTypes.contains(profileToValidate.getType())) {
            LOGGER.info("Company Type validation Should Use Web Filing failed for: " + profileToValidate.getCompanyNumber());
            throw new EligibilityException(EligibilityStatusCode.INVALID_COMPANY_TYPE_USE_WEB_FILING);
        }
        LOGGER.info("Company Type validation Should Use Web Filing passed for: " + profileToValidate.getCompanyNumber());
    }
}
