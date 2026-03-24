package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.CompanyProfileApplicableEligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.service.PscService;
import uk.gov.companieshouse.confirmationstatementapi.utils.ApiLogger;

import java.time.LocalDate;
import java.util.Set;
import java.util.function.Supplier;

public class CompanyPscCountValidation extends CompanyProfileApplicableEligibilityRule {

    private final PscService pscService;

    private final boolean multiplePscRuleEnabled;

    public CompanyPscCountValidation(PscService pscService,
                                     Set<String> baselineCompanyTypes,
                                     Set<String> targetCompanyTypes,
                                     LocalDate activationDate,
                                     Supplier<LocalDate> localDateNow,
                                     boolean multiplePscRuleEnabled) {

        super(baselineCompanyTypes, targetCompanyTypes, activationDate, localDateNow);
        this.pscService = pscService;
        this.multiplePscRuleEnabled = multiplePscRuleEnabled;
        ApiLogger.debug(String.format("MULTIPLE PSC JOURNEY FEATURE FLAG: %s", multiplePscRuleEnabled));
    }

    @Override
    public void validate(CompanyProfileApi companyProfile) throws EligibilityException, ServiceException {
        ApiLogger.info(String.format("Validating Company PSCs for: %s", companyProfile.getCompanyNumber()));
        if (!companyApplicableForRule(companyProfile)) {
            ApiLogger.debug("Company PSC Count FEATURE FLAG off skipping validation");
            return;
        }

        var activePscCount = pscService
                .getPSCsFromCHS(companyProfile.getCompanyNumber())
                .getActiveCount();

        if (activePscCount == null) {
            ApiLogger.debug("PSC count is null — treating as no PSCs");
            activePscCount = 0L;
        }

        if (multiplePscRuleEnabled) {
            if (activePscCount > 1) {
                ApiLogger.info(String.format("Company PSCs validation failed for: %s", companyProfile.getCompanyNumber()));
                throw new EligibilityException(EligibilityStatusCode.INVALID_COMPANY_APPOINTMENTS_MORE_THAN_ONE_PSC);
            }
        } else {
            if (activePscCount > 5) {
                ApiLogger.info(String.format("Company PSCs validation failed for: %s", companyProfile.getCompanyNumber()));
                throw new EligibilityException(EligibilityStatusCode.INVALID_COMPANY_APPOINTMENTS_MORE_THAN_FIVE_PSCS);
            }
        }
        ApiLogger.info(String.format("Company PSCs validation passed for: %s", companyProfile.getCompanyNumber()));
    }

}
