package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

public class CompanyOfficerValidation extends CompanyProfileApplicableEligibilityRule {

    private final OfficerService officerService;

    private final Set<String> multipleOfficerBaselineCompanyTypes,
            multipleOfficerTargetCompanyTypes,
            singleOfficerBaselineCompanyTypes,
            singleOfficerTargetCompanyTypes;

    private final LocalDate multipleOfficerActivationDate, singleOfficerActivationDate;

    @Autowired
    public CompanyOfficerValidation(OfficerService officerService,
                                    Set<String> multipleOfficerBaselineCompanyTypes,
                                    Set<String> multipleOfficerTargetCompanyTypes,
                                    LocalDate multipleOfficerActivationDate,
                                    Set<String> singleOfficerBaselineCompanyTypes,
                                    Set<String> singleOfficerTargetCompanyTypes,
                                    LocalDate singleOfficerActivationDate,
                                    Supplier<LocalDate> localDateNow){
        super(multipleOfficerBaselineCompanyTypes, multipleOfficerTargetCompanyTypes,  multipleOfficerActivationDate, localDateNow);
        this.officerService = officerService;
        this.multipleOfficerBaselineCompanyTypes = multipleOfficerBaselineCompanyTypes;
        this.multipleOfficerTargetCompanyTypes = multipleOfficerTargetCompanyTypes;
        this.multipleOfficerActivationDate = multipleOfficerActivationDate;
        this.singleOfficerBaselineCompanyTypes = singleOfficerBaselineCompanyTypes;
        this.singleOfficerTargetCompanyTypes = singleOfficerTargetCompanyTypes;
        this.singleOfficerActivationDate = singleOfficerActivationDate;
    }

    @Override
    public void validateAgainstMadeUpDate(CompanyProfileApi companyProfile, LocalDate madeUpDate) throws EligibilityException, ServiceException {
        String companyNumber = companyProfile.getCompanyNumber();
        ApiLogger.info(String.format("Validating Company Officers for: %s", companyNumber));
        var officers = officerService.getOfficers(companyNumber);
        var activeOfficersCount = officers.getActiveCount();
        ApiLogger.debug(String.format("Company has %s active officers", activeOfficersCount));
        String passedCompanyOfficerValidationMessage = String.format("Company Officers validation passed for: %s", companyNumber);

        this.setApplicableCompanyTypes(multipleOfficerBaselineCompanyTypes, multipleOfficerTargetCompanyTypes, multipleOfficerActivationDate);
        if (companyApplicableForRule(companyProfile, madeUpDate)) {
            performMultipleOfficerCheck(companyNumber, activeOfficersCount);
            ApiLogger.info(passedCompanyOfficerValidationMessage);
            return;
        }

        this.setApplicableCompanyTypes(singleOfficerBaselineCompanyTypes, singleOfficerTargetCompanyTypes, singleOfficerActivationDate);
        if (companyApplicableForRule(companyProfile, madeUpDate)) {
            performSingleOfficerCheck(companyNumber, officers, activeOfficersCount);
        }
        ApiLogger.info(passedCompanyOfficerValidationMessage);
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

    public void performMultipleOfficerCheck(String companyNumber, Long activeOfficersCount) throws EligibilityException {
        if(Objects.isNull(activeOfficersCount) || activeOfficersCount == 0 || activeOfficersCount > 5) {
            ApiLogger.info(String.format("Company Officers validation failed for: %s. No officers found or more than 5 active officers, activeOfficersCount = %s.", companyNumber, activeOfficersCount));
            throw new EligibilityException(EligibilityStatusCode.INVALID_COMPANY_APPOINTMENTS_INVALID_NUMBER_OF_OFFICERS);
        }
    }

    public void performSingleOfficerCheck(String companyNumber, OfficersApi officers, Long activeOfficersCount) throws EligibilityException {
        var officerCheck = isOfficerDirector(officers.getItems(), activeOfficersCount);
        if(!officerCheck) {
            ApiLogger.info(String.format("Company Officers validation failed for: %s", companyNumber));
            throw new EligibilityException(EligibilityStatusCode.INVALID_COMPANY_APPOINTMENTS_INVALID_NUMBER_OF_OFFICERS);
        }
    }
}
