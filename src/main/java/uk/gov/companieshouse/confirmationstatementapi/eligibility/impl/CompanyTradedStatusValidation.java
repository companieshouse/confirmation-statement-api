package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.service.CorporateBodyService;

public class CompanyTradedStatusValidation implements EligibilityRule<CompanyProfileApi> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyTradedStatusValidation.class);

    private final CorporateBodyService corporateBodyService;

    public CompanyTradedStatusValidation(CorporateBodyService corporateBodyService) {
        this.corporateBodyService = corporateBodyService;
    }

    @Override
    public void validate(CompanyProfileApi profileToValidate) throws EligibilityException {
        var companyNumber = profileToValidate.getCompanyNumber();
        LOGGER.info("Validating Company Traded Status for: {}", companyNumber);

        var companyTradedStatus = corporateBodyService.getCompanyTradedStatus(companyNumber);

        if(companyTradedStatus != 0) {
            LOGGER.info("Company traded status validation failed for {} with value {}", companyNumber, companyTradedStatus);
            throw new EligibilityException(EligibilityStatusCode.INVALID_COMPANY_TRADED_STATUS_USE_WEBFILING);
        }
        LOGGER.info("Company traded status validation successful for {} with value {}", companyNumber, companyTradedStatus);
    }
}
