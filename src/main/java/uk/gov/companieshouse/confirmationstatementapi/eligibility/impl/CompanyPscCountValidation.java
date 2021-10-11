package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.service.PscService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

public class CompanyPscCountValidation implements EligibilityRule<CompanyProfileApi> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyPscCountValidation.class.getName());

    private final PscService pscService;

    private final boolean companyPscCountValidationFeatureFlag;

    public CompanyPscCountValidation(PscService pscService, boolean companyPscCountValidationFeatureFlag) {
        this.pscService = pscService;
        this.companyPscCountValidationFeatureFlag = companyPscCountValidationFeatureFlag;
    }

    @Override
    public void validate(CompanyProfileApi profileToValidate) throws EligibilityException, ServiceException {
        LOGGER.info(String.format("Validating Company PSCs for: %s", profileToValidate.getCompanyNumber()));
        if (!companyPscCountValidationFeatureFlag) {
            LOGGER.debug("Company PSC Count FEATURE FLAG off skipping validation");
            return;
        }
        var count = pscService.getPSCsFromCHS(profileToValidate.getCompanyNumber()).getActiveCount();
        if (count != null && count > 1) {
            LOGGER.info(String.format("Company PSCs validation failed for: %s", profileToValidate.getCompanyNumber()));
            throw new EligibilityException(EligibilityStatusCode.INVALID_COMPANY_APPOINTMENTS_MORE_THAN_ONE_PSC);
        }
        LOGGER.info(String.format("Company PSCs validation passed for: %s", profileToValidate.getCompanyNumber()));
    }

}
