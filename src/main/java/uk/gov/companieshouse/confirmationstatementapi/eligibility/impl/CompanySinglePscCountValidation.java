package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.CompanyProfileApplicableEligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;

public class CompanySinglePscCountValidation extends CompanyProfileApplicableEligibilityRule {
    @Override
    public void validate(CompanyProfileApi input) throws EligibilityException, ServiceException {

    }
}
