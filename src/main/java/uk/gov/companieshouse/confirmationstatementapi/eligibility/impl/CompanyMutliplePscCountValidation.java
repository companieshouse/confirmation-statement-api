package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.CompanyProfileApplicableEligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.service.PscService;

public class CompanyMutliplePscCountValidation extends CompanyProfileApplicableEligibilityRule {

    private final PscService pscService;

    @Override
    public void validate(CompanyProfileApi input) throws EligibilityException, ServiceException {

    }
}
