package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.service.PscService;
import uk.gov.companieshouse.confirmationstatementapi.utils.ApiLogger;

public class CompanyPscCountValidation implements EligibilityRule<CompanyProfileApi> {

    private final PscService pscService;

    private final boolean companyPscCountValidationFeatureFlag;

    public CompanyPscCountValidation(PscService pscService, boolean companyPscCountValidationFeatureFlag) {
        this.pscService = pscService;
        this.companyPscCountValidationFeatureFlag = companyPscCountValidationFeatureFlag;
    }

    @Override
    public void validate(CompanyProfileApi profileToValidate) throws EligibilityException, ServiceException {
        ApiLogger.info(String.format("Validating Company PSCs for: %s", profileToValidate.getCompanyNumber()));
        if (!companyPscCountValidationFeatureFlag) {
            ApiLogger.debug("Company PSC Count FEATURE FLAG off skipping validation");
            return;
        }
        var count = pscService.getPSCsFromCHS(profileToValidate.getCompanyNumber()).getActiveCount();
        if (count != null && count > 1) {
            ApiLogger.info(String.format("Company PSCs validation failed for: %s", profileToValidate.getCompanyNumber()));
            throw new EligibilityException(EligibilityStatusCode.INVALID_COMPANY_APPOINTMENTS_MORE_THAN_ONE_PSC);
        }
        ApiLogger.info(String.format("Company PSCs validation passed for: %s", profileToValidate.getCompanyNumber()));
    }

}
