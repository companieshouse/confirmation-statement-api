package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import static uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode.INVALID_LIMITED_PARTNERSHIP_SUB_TYPE;
import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.LIST_LIMITED_PARTNERSHIP_SUBTYPES;

import java.time.LocalDate;
import java.util.Set;
import java.util.function.Supplier;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.CompanyProfileApplicableEligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.utils.ApiLogger;

public class CompanyLimitedPartnershipSubTypeValidation extends CompanyProfileApplicableEligibilityRule {

    public CompanyLimitedPartnershipSubTypeValidation(Set<String> baselineCompanyTypes, Set<String> targetCompanyTypes, LocalDate activationDate, Supplier<LocalDate> localDateNow) {
        super(baselineCompanyTypes, targetCompanyTypes, activationDate, localDateNow);
    }

    @Override
    public void validateAgainstMadeUpDate(CompanyProfileApi companyProfile, LocalDate madeUpDate) throws EligibilityException {
        if (!companyApplicableForRule(companyProfile, madeUpDate)) {
            ApiLogger.debug("Company Type not of correct type for Limited Partnership Sub Type validation, skipping validation");
            return;
        }

        String subType = companyProfile.getSubtype();

        ApiLogger.info(String.format("Validating Limited Partnership subtype (%s) for: %s", subType, companyProfile.getCompanyNumber()));

        if (subType == null || !LIST_LIMITED_PARTNERSHIP_SUBTYPES.contains(subType)) {
            ApiLogger.info(String.format("Limited Partnership subtype (%s) for company (%s) invalid", subType,
                    companyProfile.getCompanyNumber()));

            throw new EligibilityException(INVALID_LIMITED_PARTNERSHIP_SUB_TYPE);
        }
    }
}
