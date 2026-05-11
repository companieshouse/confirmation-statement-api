package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import java.time.LocalDate;
import java.util.Set;
import java.util.function.Supplier;

import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.CompanyProfileApplicableEligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.model.CompanyTradedStatusType;
import uk.gov.companieshouse.confirmationstatementapi.service.CorporateBodyService;
import uk.gov.companieshouse.confirmationstatementapi.utils.ApiLogger;

public class CompanyTradedStatusValidation extends CompanyProfileApplicableEligibilityRule {

    private final CorporateBodyService corporateBodyService;

    public CompanyTradedStatusValidation(CorporateBodyService corporateBodyService, 
                                        Set<String> baselineCompanyTypes, 
                                        Set<String> targetCompanyTypes, 
                                        LocalDate activationDate,
                                        Supplier<LocalDate> localDateNow) {

        super(baselineCompanyTypes, targetCompanyTypes, activationDate, localDateNow);
        this.corporateBodyService = corporateBodyService;
    }

    @Override
    public void validateAgainstMadeUpDate(CompanyProfileApi profileToValidate, LocalDate madeUpDate) throws EligibilityException, ServiceException {
        var companyNumber = profileToValidate.getCompanyNumber();
        ApiLogger.info(String.format("Validating Company Traded Status for: %s", companyNumber));
        if (!companyApplicableForRule(profileToValidate, madeUpDate)) {
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
