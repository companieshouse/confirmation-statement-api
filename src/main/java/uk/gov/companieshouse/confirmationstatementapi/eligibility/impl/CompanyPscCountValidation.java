package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.service.PscService;

public class CompanyPscCountValidation implements EligibilityRule<CompanyProfileApi> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyPscCountValidation.class);

    private final PscService pscService;

    private final boolean companyPscCountValidationFeatureFlag;

    public CompanyPscCountValidation(PscService pscService, boolean companyPscCountValidationFeatureFlag) {
        this.pscService = pscService;
        this.companyPscCountValidationFeatureFlag = companyPscCountValidationFeatureFlag;
    }

    @Override
    public void validate(CompanyProfileApi profileToValidate) throws EligibilityException, ServiceException {
        if (!companyPscCountValidationFeatureFlag) {
            LOGGER.debug("Company PSC Count FEATURE FLAG off skipping validation");
            return;
        }
        LOGGER.info("Validating Company PSCs for: {}", profileToValidate.getCompanyNumber());
        var count = pscService.getPscs(profileToValidate.getCompanyNumber()).getActiveCount();
        if (count != null && count > 1) {
            LOGGER.info("Company PSCs validation failed for: {}", profileToValidate.getCompanyNumber());
            throw new EligibilityException(EligibilityStatusCode.INVALID_COMPANY_APPOINTMENTS_MORE_THAN_ONE_PSC);
        }
        LOGGER.info("Company PSCs validation passed for: {}", profileToValidate.getCompanyNumber());
    }

}
