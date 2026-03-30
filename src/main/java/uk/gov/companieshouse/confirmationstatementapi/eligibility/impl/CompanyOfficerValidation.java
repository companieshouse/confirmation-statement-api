package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.CompanyProfileApplicableEligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.service.OfficerService;
import uk.gov.companieshouse.confirmationstatementapi.utils.ApiLogger;

import java.time.LocalDate;

public class CompanyOfficerValidation extends CompanyProfileApplicableEligibilityRule {

    private final OfficerService officerService;
    private final CompanyMultipleOfficerValidation companyMultipleOfficerValidation;
    private final CompanySingleOfficerValidation companySingleOfficerValidation;

    public CompanyOfficerValidation(OfficerService officerService,
                                    CompanyMultipleOfficerValidation companyMultipleOfficerValidation,
                                    CompanySingleOfficerValidation companySingleOfficerValidation) {
        super(null, null, null, null);
        this.officerService = officerService;
        this.companyMultipleOfficerValidation = companyMultipleOfficerValidation;
        this.companySingleOfficerValidation = companySingleOfficerValidation;

    }

    @Override
    protected boolean companyApplicableForRule(CompanyProfileApi companyProfile, LocalDate madeUpDate) {
        return true;
    }

    @Override
    public void validateAgainstMadeUpDate(CompanyProfileApi companyProfile, LocalDate madeUpDate) throws EligibilityException, ServiceException {
        String companyNumber = companyProfile.getCompanyNumber();
        ApiLogger.info(String.format("Validating Company Officers for: %s", companyNumber));
        var officers = officerService.getOfficers(companyNumber);
        var activeOfficersCount = officers.getActiveCount();
        ApiLogger.debug(String.format("Company has %s active officers", activeOfficersCount));

        if (companyMultipleOfficerValidation.isEligibleForMultipleOfficerCheck(companyProfile, madeUpDate)) {
            companyMultipleOfficerValidation.validateAgainstMadeUpDate(companyProfile, madeUpDate);
        } else {
            companySingleOfficerValidation.validateAgainstMadeUpDate(companyProfile, madeUpDate);
        }

        ApiLogger.info(String.format("Company Officers validation passed for: %s", companyNumber));
    }
}
