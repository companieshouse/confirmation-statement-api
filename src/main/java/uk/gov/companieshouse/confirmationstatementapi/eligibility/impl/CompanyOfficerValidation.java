package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.officers.CompanyOfficerApi;
import uk.gov.companieshouse.api.model.officers.OfficerRoleApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.service.OfficerService;

import java.util.List;

public class CompanyOfficerValidation implements EligibilityRule<CompanyProfileApi> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyOfficerValidation.class);

    private final OfficerService officerService;

    private final boolean officerValidationFlag;

    @Autowired
    public CompanyOfficerValidation(OfficerService officerService, boolean officerValidationFlag){
        this.officerService = officerService;
        this.officerValidationFlag = officerValidationFlag;
    }

    @Override
    public void validate(CompanyProfileApi companyProfileApi) throws EligibilityException, ServiceException {
        LOGGER.info("Validating Company Officers for: {}", companyProfileApi.getCompanyNumber());
        if (!officerValidationFlag) {
            LOGGER.debug("OFFICER VALIDATION FEATURE FLAG off skipping validation");
            return;
        }
        var officers = officerService.getOfficers(companyProfileApi.getCompanyNumber());
        var officerCheck = isOfficerDirector(officers.getItems(), officers.getActiveCount());
        if (!officerCheck) {
            LOGGER.info("Company Officers validation failed for: {}", companyProfileApi.getCompanyNumber());
            throw new EligibilityException(EligibilityStatusCode.INVALID_COMPANY_APPOINTMENTS_INVALID_NUMBER_OF_OFFICERS);
        }
        LOGGER.info("Company Officers validation passed for: {}", companyProfileApi.getCompanyNumber());
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
            // returns false is officer isn't DIRECTOR, NOMINEE_DIRECTOR or CORPORATE_DIRECTOR and  isn't active
            return false;
        } else {
            return activeCount == null || activeCount < 1;
            // returns true for null or 0 officers, false for more than one officer
        }
    }
}
