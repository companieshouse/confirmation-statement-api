package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import static java.time.LocalDate.*;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.officers.CompanyOfficerApi;
import uk.gov.companieshouse.api.model.officers.OfficersApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.service.OfficerService;
import uk.gov.companieshouse.confirmationstatementapi.utils.ApiLogger;

public class IdentityVerifiedOfficersValidation implements EligibilityRule<CompanyProfileApi> {

    private final OfficerService officerService;

    private final boolean identityVerifiedOfficersJourneyFeatureFlag;

    @Autowired
    public IdentityVerifiedOfficersValidation(OfficerService officerService, boolean multipleOfficerJourneyFlag){
        this.officerService = officerService;
        this.identityVerifiedOfficersJourneyFeatureFlag = multipleOfficerJourneyFlag;
        ApiLogger.debug(String.format("IDENTITY VERIFIED OFFICERS FEATURE FLAG: %s", this.identityVerifiedOfficersJourneyFeatureFlag));
    }

    @Override
    public void validate(CompanyProfileApi companyProfileApi) throws EligibilityException, ServiceException {
        ApiLogger.info(String.format("Validating Identity Verification of Officers for: %s", companyProfileApi.getCompanyNumber()));
        OfficersApi officers = officerService.getOfficers(companyProfileApi.getCompanyNumber());

        if(identityVerifiedOfficersJourneyFeatureFlag) {
            for (CompanyOfficerApi officer : officers.getItems()) {
                if (!isOfficerVerified(officer)) {
                    ApiLogger.info(String.format("Officers Identity Verification validation failed for: %s.", companyProfileApi.getCompanyNumber()));
                    throw new EligibilityException(EligibilityStatusCode.INVALID_COMPANY_APPOINTMENTS_NOT_ALL_IDENTITY_VERIFIED);
                }
                ApiLogger.info(String.format("Officers Identity Verification validation passed for: %s", companyProfileApi.getCompanyNumber()));
            }
        }
    }

    private boolean isOfficerVerified(CompanyOfficerApi officer) {
        var idvDetails = officer.getIdentityVerificationDetails();
        return (idvDetails != null)
                && (idvDetails.getAppointmentVerificationStartOn().isBefore(LocalDate.now()))
                && (idvDetails.getAppointmentVerificationEndOn().isAfter(LocalDate.now()));
    }

}