package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.CompanyProfileApplicableEligibilityRule;
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


    public CompanyPscCountValidation(PscService pscService,
                                     Set<String> baselineCompanyTypes,
                                     Set<String> targetCompanyTypes,
                                     LocalDate activationDate,
                                     Supplier<LocalDate> localDateNow) {

        super(baselineCompanyTypes, targetCompanyTypes, activationDate, localDateNow);
        this.pscService = pscService;
    }

    @Override
    public void validate(CompanyProfileApi companyProfile) throws EligibilityException, ServiceException {
        ApiLogger.info(String.format("Validating Company PSCs for: %s", companyProfile.getCompanyNumber()));
        if (!companyApplicableForRule(companyProfile)) {
            ApiLogger.debug("Company PSC Count FEATURE FLAG off skipping validation");
            return;
        }

        String companyNumber = companyProfile.getCompanyNumber();

        Long activePscCount = pscService
                .getPSCsFromCHS(companyNumber)
                .getActiveCount();

        if (activePscCount == null) {
            ApiLogger.debug("PSC count is null — treating as no PSCs");
            activePscCount = 0L;
        }

        if (multiplePscRuleEnabled) {
            performMultiplePscCheck(companyNumber, activePscCount);
            return;
        }
        performSinglePscCheck(companyNumber, activePscCount);

        ApiLogger.info(String.format("Company PSCs validation passed for: %s", companyProfile.getCompanyNumber()));
    }


    /**
     * Multiple PSC check
     * Valid when count is between 1 and 5.
     * Fail when count == 0 OR count > 5
     */
    public void performMultiplePscCheck(String companyNumber, Long activePscCount)
            throws EligibilityException {

        ApiLogger.info("Running MULTIPLE PSC validation for: " + companyNumber);

        if (activePscCount == 0 || activePscCount > 5) {
            ApiLogger.info(String.format(
                    "Multiple PSC validation FAILED for %s. Active PSCs: %s",
                    companyNumber, activePscCount));

            throw new EligibilityException(
                    EligibilityStatusCode.INVALID_COMPANY_APPOINTMENTS_MORE_THAN_FIVE_PSCS
            );
        }

        ApiLogger.info("Multiple PSC validation PASSED for: " + companyNumber);
    }

    /**
     * Single PSC check
     * Valid only when count == 1
     */
    public void performSinglePscCheck(String companyNumber, Long activePscCount)
            throws EligibilityException {

        ApiLogger.info("Running SINGLE PSC validation for: " + companyNumber);

        if (activePscCount != 1) {
            ApiLogger.info(String.format(
                    "Single PSC validation FAILED for %s. Active PSCs: %s",
                    companyNumber, activePscCount));

            throw new EligibilityException(
                    EligibilityStatusCode.INVALID_COMPANY_APPOINTMENTS_MORE_THAN_ONE_PSC
            );
        }

        ApiLogger.info("Single PSC validation PASSED for: " + companyNumber);
    }


}
