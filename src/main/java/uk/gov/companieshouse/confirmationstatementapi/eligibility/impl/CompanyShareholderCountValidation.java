package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import java.time.LocalDate;
import java.util.Set;
import java.util.function.Supplier;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.CompanyProfileApplicableEligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.service.ShareholderService;
import uk.gov.companieshouse.confirmationstatementapi.utils.ApiLogger;

public class CompanyShareholderCountValidation extends CompanyProfileApplicableEligibilityRule {

    private final ShareholderService shareholderService;

    public CompanyShareholderCountValidation(ShareholderService shareholderService,
                                             Set<String> baselineCompanyTypes,
                                             Set<String> targetCompanyTypes,
                                             LocalDate activationDate, Supplier<LocalDate> localDateNow) {
        super(baselineCompanyTypes, targetCompanyTypes, activationDate, localDateNow);

        this.shareholderService = shareholderService;
    }

    @Override
    public void validateAgainstMadeUpDate(CompanyProfileApi companyProfile, LocalDate madeUpDate) throws EligibilityException, ServiceException {
        if (!companyApplicableForRule(companyProfile, madeUpDate)) {
            ApiLogger.debug("SHAREHOLDER COUNT VALIDATION FEATURE FLAG off skipping validation");
            return;
        }

        ApiLogger.info(String.format("Validating Company shareholder count for: %s", companyProfile.getCompanyNumber()));

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
