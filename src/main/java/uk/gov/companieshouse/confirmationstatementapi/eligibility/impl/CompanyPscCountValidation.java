package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.CompanyProfileApplicableEligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.service.PscService;
import uk.gov.companieshouse.confirmationstatementapi.utils.ApiLogger;

import java.time.LocalDate;

public class CompanyPscCountValidation extends CompanyProfileApplicableEligibilityRule {

    private final PscService pscService;
    private final CompanyMultiplePscCountValidation companyMultiplePscCountValidation;
    private final CompanySinglePscCountValidation companySinglePscCountValidation;



    public CompanyPscCountValidation(PscService pscService,
                                     CompanyMultiplePscCountValidation companyMultiplePscCountValidation,
                                     CompanySinglePscCountValidation companySinglePscCountValidation) {

        super(null, null, null, null);
        this.pscService = pscService;
        this.companyMultiplePscCountValidation = companyMultiplePscCountValidation;
        this.companySinglePscCountValidation = companySinglePscCountValidation;
    }

    @Override
    protected boolean companyApplicableForRule(CompanyProfileApi companyProfile, LocalDate madeUpDate) {
        return true;
    }

    @Override
    public void validateAgainstMadeUpDate(CompanyProfileApi companyProfile, LocalDate madeUpDate) throws EligibilityException, ServiceException {

        String companyNumber = companyProfile.getCompanyNumber();
        ApiLogger.info(String.format("Validating Company PSCs for: %s", companyNumber));
        var pscs = pscService.getPSCsFromCHS(companyNumber);
        var activePscsCount = pscs.getActiveCount();
        ApiLogger.debug(String.format("Company has %s active PSCs", activePscsCount));

        if (companyMultiplePscCountValidation.isEligibleForMultiplePscCheck(companyProfile, madeUpDate)) {
            companyMultiplePscCountValidation.validateAgainstMadeUpDate(companyProfile, madeUpDate);
        } else {
            companySinglePscCountValidation.validateAgainstMadeUpDate(companyProfile, madeUpDate);
        }

        ApiLogger.info(String.format("Company PSCs validation passed for: %s", companyNumber));
    }

}
