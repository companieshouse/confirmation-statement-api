package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.CompanyProfileApplicableEligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.service.OfficerService;
import uk.gov.companieshouse.confirmationstatementapi.utils.ApiLogger;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

public class CompanyMultipleOfficerValidation extends CompanyProfileApplicableEligibilityRule {

    private final OfficerService officerService;

    public CompanyMultipleOfficerValidation(OfficerService officerService,
                                            Set<String> multipleOfficerBaselineCompanyTypes,
                                            Set<String> multipleOfficerTargetCompanyTypes,
                                            LocalDate multipleOfficerActivationDate,
                                            Supplier<LocalDate> localDateNow){
        super(multipleOfficerBaselineCompanyTypes, multipleOfficerTargetCompanyTypes,  multipleOfficerActivationDate, localDateNow);
        this.officerService = officerService;
    }

    @Override
    public void validateAgainstMadeUpDate(CompanyProfileApi companyProfile, LocalDate madeUpDate) throws EligibilityException, ServiceException {
        String companyNumber = companyProfile.getCompanyNumber();
        var officers = officerService.getOfficers(companyNumber);
        var activeOfficersCount = officers.getActiveCount();

        if (isEligibleForMultipleOfficerCheck(companyProfile, madeUpDate)) {
            performMultipleOfficerCheck(companyNumber, activeOfficersCount);
        }
    }

    public void performMultipleOfficerCheck(String companyNumber, Long activeOfficersCount) throws EligibilityException {
        if(Objects.isNull(activeOfficersCount) || activeOfficersCount == 0 || activeOfficersCount > 5) {
            ApiLogger.info(String.format("Company Officers validation failed for: %s. No officers found or more than 5 active officers, activeOfficersCount = %s.", companyNumber, activeOfficersCount));
            throw new EligibilityException(EligibilityStatusCode.INVALID_COMPANY_APPOINTMENTS_INVALID_NUMBER_OF_OFFICERS);
        }
    }

    public boolean isEligibleForMultipleOfficerCheck (CompanyProfileApi companyProfile, LocalDate madeUpDate) {
        return companyApplicableForRule(companyProfile, madeUpDate);
    }
}
