package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.officers.CompanyOfficerApi;
import uk.gov.companieshouse.api.model.officers.OfficerRoleApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.service.OfficerService;
import uk.gov.companieshouse.confirmationstatementapi.utils.ApiLogger;

import java.util.List;

public class CompanyOfficerValidation implements EligibilityRule<CompanyProfileApi> {

    @Autowired
    private ApiLogger apiLogger;

    private final OfficerService officerService;

    private final boolean officerValidationFlag;

    @Autowired
    public CompanyOfficerValidation(OfficerService officerService, boolean officerValidationFlag){
        this.officerService = officerService;
        this.officerValidationFlag = officerValidationFlag;
    }

    @Override
    public void validate(CompanyProfileApi companyProfileApi) throws EligibilityException, ServiceException {
        apiLogger.info(String.format("Validating Company Officers for: %s", companyProfileApi.getCompanyNumber()));
        if (!officerValidationFlag) {
            apiLogger.debug("OFFICER VALIDATION FEATURE FLAG off skipping validation");
            return;
        }
        var officers = officerService.getOfficers(companyProfileApi.getCompanyNumber());
        var officerCheck = isOfficerDirector(officers.getItems(), officers.getActiveCount());
        if (!officerCheck) {
            apiLogger.info(String.format("Company Officers validation failed for: %s", companyProfileApi.getCompanyNumber()));
            throw new EligibilityException(EligibilityStatusCode.INVALID_COMPANY_APPOINTMENTS_INVALID_NUMBER_OF_OFFICERS);
        }
        apiLogger.info(String.format("Company Officers validation passed for: %s", companyProfileApi.getCompanyNumber()));
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
}
