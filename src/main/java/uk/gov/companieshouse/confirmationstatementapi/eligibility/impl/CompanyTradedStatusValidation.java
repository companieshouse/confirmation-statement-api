package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.model.CompanyTradedStatusType;
import uk.gov.companieshouse.confirmationstatementapi.service.CorporateBodyService;
import uk.gov.companieshouse.confirmationstatementapi.utils.ApiLogger;

public class CompanyTradedStatusValidation implements EligibilityRule<CompanyProfileApi> {

    private final CorporateBodyService corporateBodyService;
    private final boolean tradedStatusEligibilityFlag;

    public CompanyTradedStatusValidation(CorporateBodyService corporateBodyService, boolean tradedStatusEligibilityFlag) {
        this.corporateBodyService = corporateBodyService;
        this.tradedStatusEligibilityFlag = tradedStatusEligibilityFlag;
    }

    @Override
    public void validate(CompanyProfileApi profileToValidate) throws EligibilityException {
        var companyNumber = profileToValidate.getCompanyNumber();
        ApiLogger.info(String.format("Validating Company Traded Status for: %s", companyNumber));
        if (!tradedStatusEligibilityFlag) {
            ApiLogger.debug("TRADED STATUS VALIDATION FEATURE FLAG off skipping validation");
            return;
        }

        var companyTradedStatus = corporateBodyService.getCompanyTradedStatus(companyNumber);

        if(CompanyTradedStatusType.NOT_ADMITTED_TO_TRADING != companyTradedStatus) {
            ApiLogger.info(String.format("Company traded status validation failed for %s with value %s", companyNumber, companyTradedStatus));
            throw new EligibilityException(EligibilityStatusCode.INVALID_COMPANY_TRADED_STATUS_USE_WEBFILING);
        }
        ApiLogger.info(String.format("Company traded status validation successful for %s with value %s", companyNumber, companyTradedStatus));
    }
}
