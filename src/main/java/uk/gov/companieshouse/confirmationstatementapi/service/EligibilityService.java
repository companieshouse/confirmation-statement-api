package uk.gov.companieshouse.confirmationstatementapi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.model.response.CompanyValidationResponse;
import uk.gov.companieshouse.confirmationstatementapi.utils.ApiLogger;

@Service
public class EligibilityService {

    private final List<EligibilityRule<CompanyProfileApi>> eligibilityRules;

    @Autowired
    public EligibilityService(List<EligibilityRule<CompanyProfileApi>> eligibilityRules){
        this.eligibilityRules = eligibilityRules;
    }

    public CompanyValidationResponse checkCompanyEligibility(CompanyProfileApi companyProfile) throws ServiceException {
        var response = new CompanyValidationResponse();
        try {
            for (EligibilityRule<CompanyProfileApi> eligibilityRule : eligibilityRules) {
                eligibilityRule.validate(companyProfile);
            }
        } catch (EligibilityException e) {
            ApiLogger.info(String.format("Company %s ineligible to use the service because %s",  companyProfile.getCompanyNumber(), e.getEligibilityStatusCode()));
            response.setEligibilityStatusCode(e.getEligibilityStatusCode());
            return response;
        }
        response.setEligibilityStatusCode(EligibilityStatusCode.COMPANY_VALID_FOR_SERVICE);
        return response;
    }
}
