package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.officers.CompanyOfficerApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityFailureReason;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.service.OfficerService;

import java.util.List;

public class CompanyOfficerValidation implements EligibilityRule<CompanyProfileApi> {

    private final OfficerService officerService;

    @Autowired
    public CompanyOfficerValidation(OfficerService officerService){
        this.officerService = officerService;
    }

    @Override
    public void validate(CompanyProfileApi companyProfileApi) throws EligibilityException, ServiceException {
        var officers = officerService.getOfficers(companyProfileApi.getCompanyNumber());
        var officerCount = getOfficerCount(officers.getItems(), officers.getActiveCount());
        if (officerCount != null && officerCount > 1) {
            throw new EligibilityException(EligibilityFailureReason.INVALID_OFFICER_COUNT);
        }
    }

    public Long getOfficerCount(List<CompanyOfficerApi> officers, Long activeCount) {
        for(CompanyOfficerApi i: officers) {
            if (i.getOfficerRole().getOfficerRole().contains("secretary")) activeCount--;
        }
        return activeCount;
    }
}
