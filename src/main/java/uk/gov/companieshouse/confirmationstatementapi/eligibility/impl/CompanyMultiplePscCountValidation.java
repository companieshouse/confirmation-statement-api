package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.CompanyProfileApplicableEligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.service.PscService;
import uk.gov.companieshouse.confirmationstatementapi.utils.ApiLogger;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

public class CompanyMultiplePscCountValidation extends CompanyProfileApplicableEligibilityRule {

    private final PscService pscService;

    public CompanyMultiplePscCountValidation(PscService pscService,
                                             Set<String> multiplePscBaselineCompanyTypes,
                                             Set<String> multiplePscTargetCompanyTypes,
                                             LocalDate multiplePscActivationDate,
                                             Supplier<LocalDate> localDateNow) {
        super(multiplePscBaselineCompanyTypes, multiplePscTargetCompanyTypes, multiplePscActivationDate, localDateNow);
        this.pscService = pscService;
    }

    @Override
    public void validateAgainstMadeUpDate(CompanyProfileApi companyProfile, LocalDate madeUpDate) throws EligibilityException, ServiceException {

        String companyNumber = companyProfile.getCompanyNumber();
        var pscs = pscService.getPSCsFromCHS(companyNumber);
        var activePscsCount = pscs.getActiveCount();

        if (isEligibleForMultiplePscCheck(companyProfile, madeUpDate)) {
            performMultiplePscCheck(companyNumber, activePscsCount);
        }
    }

    public boolean isEligibleForMultiplePscCheck (CompanyProfileApi companyProfile, LocalDate madeUpDate) {
        return companyApplicableForRule(companyProfile, madeUpDate);
    }

    public void performMultiplePscCheck(String companyNumber, Long activePscsCount) throws EligibilityException {
        if(Objects.isNull(activePscsCount) || activePscsCount == 0 || activePscsCount > 5) {
            ApiLogger.info(String.format(
                    "Company PSC validation failed for: %s. No PSCs found or more than 5 active PSCs, activePscsCount = %s.",
                    companyNumber,
                    activePscsCount));
            throw new EligibilityException(EligibilityStatusCode.INVALID_COMPANY_APPOINTMENTS_MORE_THAN_FIVE_PSCS);
        }
    }

}
