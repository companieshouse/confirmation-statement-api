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
    public DirectorsIdentityVerifiedValidation(OfficerService officerService, boolean multipleOfficerJourneyFlag) {
        this.officerService = officerService;
        this.directorsIdentityVerifiedFeatureFlag = multipleOfficerJourneyFlag;
        ApiLogger.debug(String.format("IDENTITY VERIFIED DIRECTORS FEATURE FLAG: %s", this.directorsIdentityVerifiedFeatureFlag));
    }

    @Override
    public void validate(CompanyProfileApi companyProfileApi) throws EligibilityException, ServiceException {
        ApiLogger.info(String.format("Validating Directors Identity Verification for: %s", companyProfileApi.getCompanyNumber()));
        OfficersApi officers = officerService.getOfficers(companyProfileApi.getCompanyNumber());

        if (directorsIdentityVerifiedFeatureFlag) {
            for (CompanyOfficerApi officer : officers.getItems()) {
                if (!isDirectorVerified(officer)) {
                    ApiLogger.info(String.format("Directors Identity Verification validation failed for: %s.", companyProfileApi.getCompanyNumber()));
                    throw new EligibilityException(EligibilityStatusCode.INVALID_DIRECTORS_NOT_ALL_IDENTITY_VERIFIED);
                }
            }
            ApiLogger.info(String.format("Directors Identity Verification validation passed for: %s", companyProfileApi.getCompanyNumber()));
        }
    }

    private boolean isDirectorVerified(CompanyOfficerApi officer) {
        if (officer.getResignedOn() != null) return true;

        var role = officer.getOfficerRole();
        if (role != OfficerRoleApi.DIRECTOR &&
            role != OfficerRoleApi.NOMINEE_DIRECTOR &&
            role != OfficerRoleApi.CORPORATE_DIRECTOR) {
            return true;
        }

        var idvDetails = officer.getIdentityVerificationDetails();
        if (idvDetails == null) return false;

        var startOn = idvDetails.getAppointmentVerificationStartOn();
        var endOn = idvDetails.getAppointmentVerificationEndOn();
        if (startOn == null || endOn == null) {
            return false;
        }

        LocalDate today = LocalDate.now();
        return startOn.isBefore(today) && endOn.isAfter(today);
    }

}