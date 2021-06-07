package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.service.ShareholderService;

public class CompanyShareholderCountValidation implements EligibilityRule<CompanyProfileApi> {

    private final ShareholderService shareholderService;

    public CompanyShareholderCountValidation(ShareholderService shareholderService) {
        this.shareholderService = shareholderService;
    }

    @Override
    public void validate(CompanyProfileApi companyProfile) throws EligibilityException {

        // Exclude companies limited by guarantee ie.
        // 'private-limited-guarant-nsc-limited-exemption' and
        // 'private-limited-guarant-nsc'
        if (!companyProfile.getType().contains("private-limited-guarant-nsc")) {
            var count = shareholderService.getShareholderCount(companyProfile.getCompanyNumber());
            if (count > 1)
                throw new EligibilityException(
                        EligibilityStatusCode.INVALID_COMPANY_APPOINTMENTS_MORE_THAN_ONE_SHAREHOLDER);
        }
    }

}
