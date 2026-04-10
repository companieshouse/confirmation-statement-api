package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.psc.PscsApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.CompanyProfileApplicableEligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.service.PscService;
import uk.gov.companieshouse.confirmationstatementapi.utils.ApiLogger;

import java.time.LocalDate;
import java.util.Set;
import java.util.function.Supplier;

public class CompanySinglePscCountValidation extends CompanyProfileApplicableEligibilityRule {

    private final PscService pscService;

    public CompanySinglePscCountValidation(PscService pscService,
                                           Set<String> singlePscBaselineCompanyTypes,
                                           Set<String> singlePscTargetCompanyTypes,
                                           LocalDate singlePscActivationDate,
                                           Supplier<LocalDate> localDateNow) {
        super(singlePscBaselineCompanyTypes, singlePscTargetCompanyTypes, singlePscActivationDate, localDateNow);
        this.pscService = pscService;
    }

    @Override
    public void validateAgainstMadeUpDate(CompanyProfileApi companyProfile, LocalDate madeUpDate) throws EligibilityException, ServiceException {

        String companyNumber = companyProfile.getCompanyNumber();
        var pscs = pscService.getPSCsFromCHS(companyNumber);
        var activePscsCount = pscs.getActiveCount();

        if (companyApplicableForRule(companyProfile, madeUpDate)) {
            performSinglePscCheck(companyNumber, activePscsCount);
        }
    }


    public void performSinglePscCheck(String companyNumber, Long activePscsCount) throws EligibilityException {

        if (activePscsCount != null && activePscsCount > 1) {
            ApiLogger.info(String.format(
                    "Company PSCs validation failed (single PSC journey) for: %s",
                    companyNumber
            ));

            throw new EligibilityException(EligibilityStatusCode.INVALID_COMPANY_APPOINTMENTS_MORE_THAN_ONE_PSC);
        }

        ApiLogger.info(String.format(
                "Company PSCs validation passed (single PSC journey) for: %s",
                companyNumber
        ));

    }
}
