package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.service.ShareholderService;
import uk.gov.companieshouse.confirmationstatementapi.utils.ApiLogger;

public class CompanyShareholderCountValidation implements EligibilityRule<CompanyProfileApi> {

    private final ShareholderService shareholderService;
    private final boolean validationFlag;

    public CompanyShareholderCountValidation(ShareholderService shareholderService, boolean validationFlag) {
        this.shareholderService = shareholderService;
        this.validationFlag = validationFlag;
    }

    @Override
    public void validate(CompanyProfileApi companyProfile) throws EligibilityException, ServiceException {
        if (!validationFlag) {
            ApiLogger.debug("SHAREHOLDER COUNT VALIDATION FEATURE FLAG off skipping validation");
            return;
        }

        ApiLogger.info(String.format("Validating Company shareholder count for: %s", companyProfile.getCompanyNumber()));

        // Exclude companies limited by guarantee.
        if (!companyProfile.getType().contains("private-limited-guarant-nsc")) {
            var coNumber = companyProfile.getCompanyNumber();
            var count = shareholderService.getShareholderCount(coNumber);

            if (count > 1) {
                ApiLogger.info(String.format("Company shareholder count for %s failed with %s shareholders", coNumber, count));
                throw new EligibilityException(
                        EligibilityStatusCode.INVALID_COMPANY_APPOINTMENTS_MORE_THAN_ONE_SHAREHOLDER);
            }

            ApiLogger.info(String.format("Company shareholder count validation successful for %s with value %s", coNumber, count));
        }
    }

}
