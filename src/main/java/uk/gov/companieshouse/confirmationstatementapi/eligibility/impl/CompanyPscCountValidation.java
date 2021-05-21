package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityFailureReason;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.service.PscService;
public class CompanyPscCountValidation implements EligibilityRule<CompanyProfileApi> {

    private final PscService pscService;

    public CompanyPscCountValidation(PscService pscService) {
        this.pscService = pscService;
    }

    @Override
    public void validate(CompanyProfileApi profileToValidate) throws EligibilityException, ServiceException {
        var count = pscService.getPscs(profileToValidate.getCompanyNumber()).getActiveCount();
        if(count != null && count > 1) {
            throw new EligibilityException(EligibilityFailureReason.COMPANY_HAS_MULTIPLE_PSCS);
        }
        
    }
    
}
