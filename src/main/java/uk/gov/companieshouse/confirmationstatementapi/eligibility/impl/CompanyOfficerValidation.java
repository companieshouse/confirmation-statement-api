package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.officers.CompanyOfficerApi;
import uk.gov.companieshouse.api.model.officers.OfficerRoleApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.service.OfficerService;

import java.util.List;

public class CompanyOfficerValidation implements EligibilityRule<CompanyProfileApi> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyOfficerValidation.class);

    private final OfficerService officerService;

    private Boolean officerValidationFlag;

    @Autowired
    public CompanyOfficerValidation(OfficerService officerService, Boolean officerValidationFlag){
        this.officerService = officerService;
        this.officerValidationFlag = officerValidationFlag;
    }

    @Override
    public void validate(CompanyProfileApi companyProfileApi) throws EligibilityException, ServiceException {
        LOGGER.info("Validating Company Officers for: {}", companyProfileApi.getCompanyNumber());
        if (!Boolean.TRUE.equals(officerValidationFlag)) {
            LOGGER.debug("OFFICER VALIDATION FEATURE FLAG off skipping validation");
            return;
        }
        var officers = officerService.getOfficers(companyProfileApi.getCompanyNumber());
        var officerCount = getOfficerCount(officers.getItems());
        if (officerCount != null && officerCount > 1) {
            LOGGER.info("Company Officers validation failed for: {}", companyProfileApi.getCompanyNumber());
            throw new EligibilityException(EligibilityStatusCode.INVALID_COMPANY_APPOINTMENTS_MORE_THAN_ONE_OFFICER);
        }
        LOGGER.info("Company Officers validation passed for: {}", companyProfileApi.getCompanyNumber());
    }

    public Long getOfficerCount(List<CompanyOfficerApi> officers) {
        Long officerCount = 0L;
        for(CompanyOfficerApi officer: officers) {
            var role = officer.getOfficerRole();
            if (role == OfficerRoleApi.DIRECTOR || role == OfficerRoleApi.NOMINEE_DIRECTOR || role == OfficerRoleApi.CORPORATE_DIRECTOR) {
                officerCount++;
            }
        }
        return officerCount;
    }

    public void setOfficerValidationFlag(Boolean officerValidationFlag) {
        this.officerValidationFlag = officerValidationFlag;
    }
}
