package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.service.ShareholderService;

public class CompanyShareholderCountValidation implements EligibilityRule<CompanyProfileApi> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyShareholderCountValidation.class);

    private final ShareholderService shareholderService;

    public CompanyShareholderCountValidation(ShareholderService shareholderService) {
        this.shareholderService = shareholderService;
    }

    @Override
    public void validate(CompanyProfileApi companyProfile) throws EligibilityException {

        LOGGER.info("Validating Company shareholder count for: {}", companyProfile.getCompanyNumber());

        // Exclude companies limited by guarantee.
        if (!companyProfile.getType().contains("private-limited-guarant-nsc")) {
            var coNumber = companyProfile.getCompanyNumber();
            var count = shareholderService.getShareholderCount(coNumber);

            if (count > 1) {
                LOGGER.info("Company shareholder count for {} failed with {} shareholders", coNumber, count);
                throw new EligibilityException(
                        EligibilityStatusCode.INVALID_COMPANY_APPOINTMENTS_MORE_THAN_ONE_SHAREHOLDER);
            }

            LOGGER.info("Company shareholder count validation successful for {} with value {}", coNumber, count);
        }
    }

}
