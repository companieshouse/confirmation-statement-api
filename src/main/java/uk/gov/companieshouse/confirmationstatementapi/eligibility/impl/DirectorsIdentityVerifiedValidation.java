package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.officers.CompanyOfficerApi;
import uk.gov.companieshouse.api.model.officers.OfficerRoleApi;
import uk.gov.companieshouse.api.model.officers.OfficersApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.service.OfficerService;
import uk.gov.companieshouse.confirmationstatementapi.utils.ApiLogger;

public class DirectorsIdentityVerifiedValidation implements EligibilityRule<CompanyProfileApi> {

    private final OfficerService officerService;

    private final boolean directorsIdentityVerifiedFeatureFlag;

    @Autowired
    public DirectorsIdentityVerifiedValidation(OfficerService officerService, boolean directorsIdentityVerifiedFeatureFlag) {
        this.officerService = officerService;
        this.directorsIdentityVerifiedFeatureFlag = directorsIdentityVerifiedFeatureFlag;
    }

    @Override
    public void validate(CompanyProfileApi companyProfileApi) throws EligibilityException, ServiceException {
        if (!directorsIdentityVerifiedFeatureFlag) {
            ApiLogger.debug("DIRECTORS IDENTITY VERIFIED FEATURE FLAG off skipping validation");
            return;
        }
        ApiLogger.info(String.format("Validating Directors Identity Verification for: %s", companyProfileApi.getCompanyNumber()));

        OfficersApi officers = officerService.getOfficers(companyProfileApi.getCompanyNumber());
        for (CompanyOfficerApi officer : officers.getItems()) {
            if (!isDirectorVerified(officer)) {
                ApiLogger.info(String.format("Directors Identity Verification validation failed for: %s.", companyProfileApi.getCompanyNumber()));
                throw new EligibilityException(EligibilityStatusCode.INVALID_DIRECTORS_NOT_ALL_IDENTITY_VERIFIED);
            }
        }
        ApiLogger.info(String.format("Directors Identity Verification validation passed for: %s", companyProfileApi.getCompanyNumber()));
    }

    private boolean isDirectorVerified(CompanyOfficerApi officer) {
        if (officer.getResignedOn() != null) return true;
        if (officer.getOfficerRole() != OfficerRoleApi.DIRECTOR) return true;

        var idvDetails = officer.getIdentityVerificationDetails();
        if (idvDetails == null) return false;

        var startOn = idvDetails.getAppointmentVerificationStartOn();
        var endOn = idvDetails.getAppointmentVerificationEndOn();
        if (startOn == null || endOn == null) return false;

        LocalDate today = LocalDate.now();
        return startOn.minusDays(1).isBefore(today) && endOn.isAfter(today);
    }

}