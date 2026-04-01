package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.officers.CompanyOfficerApi;
import uk.gov.companieshouse.api.model.officers.OfficerRoleApi;
import uk.gov.companieshouse.api.model.officers.OfficersApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.CompanyProfileApplicableEligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.service.OfficerService;
import uk.gov.companieshouse.confirmationstatementapi.utils.ApiLogger;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class CompanySingleOfficerValidation extends CompanyProfileApplicableEligibilityRule {

    private final OfficerService officerService;

    public CompanySingleOfficerValidation(OfficerService officerService,
                                          Set<String> singleOfficerBaselineCompanyTypes,
                                          Set<String> singleOfficerTargetCompanyTypes,
                                          LocalDate singleOfficerActivationDate,
                                          Supplier<LocalDate> localDateNow){
        super(singleOfficerBaselineCompanyTypes, singleOfficerTargetCompanyTypes,  singleOfficerActivationDate, localDateNow);
        this.officerService = officerService;
    }

    @Override
    public void validateAgainstMadeUpDate(CompanyProfileApi companyProfile, LocalDate madeUpDate) throws EligibilityException, ServiceException {
        String companyNumber = companyProfile.getCompanyNumber();
        var officers = officerService.getOfficers(companyNumber);
        var activeOfficersCount = officers.getActiveCount();

        if (companyApplicableForRule(companyProfile, madeUpDate)) {
            performSingleOfficerCheck(companyNumber, officers, activeOfficersCount);
        }
    }

    public boolean isOfficerDirector(List<CompanyOfficerApi> officers, Long activeCount) {
        // Check If Single Officer Company has Director
        if (officers != null && activeCount == 1) {
            for(CompanyOfficerApi officer: officers) {
                var role = officer.getOfficerRole();
                if ((role == OfficerRoleApi.DIRECTOR || role == OfficerRoleApi.NOMINEE_DIRECTOR || role == OfficerRoleApi.CORPORATE_DIRECTOR) && officer.getResignedOn() == null) {
                    // returns true if officer is DIRECTOR, NOMINEE_DIRECTOR or CORPORATE_DIRECTOR and active
                    return true;
                }
            }
        }
        // returns false if officer isn't DIRECTOR, NOMINEE_DIRECTOR or CORPORATE_DIRECTOR, isn't active, null or 0
        return false;
    }

    public void performSingleOfficerCheck(String companyNumber, OfficersApi officers, Long activeOfficersCount) throws EligibilityException {
        var officerCheck = isOfficerDirector(officers.getItems(), activeOfficersCount);
        if(!officerCheck) {
            ApiLogger.info(String.format("Company Officers validation failed for: %s", companyNumber));
            throw new EligibilityException(EligibilityStatusCode.INVALID_COMPANY_APPOINTMENTS_INVALID_NUMBER_OF_OFFICERS);
        }
    }
}
