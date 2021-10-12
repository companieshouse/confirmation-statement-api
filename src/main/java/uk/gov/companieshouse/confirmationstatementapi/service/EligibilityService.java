package uk.gov.companieshouse.confirmationstatementapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.model.response.CompanyValidationResponse;

import java.util.List;

import static uk.gov.companieshouse.confirmationstatementapi.ConfirmationStatementApiApplication.LOGGER;

@Service
public class EligibilityService {

    private final List<EligibilityRule<CompanyProfileApi>> eligibilityRules;

    @Autowired
    public EligibilityService(@Qualifier("confirmation-statement-eligibility-rules")
                                                    List<EligibilityRule<CompanyProfileApi>> eligibilityRules){
        this.eligibilityRules = eligibilityRules;
    }

    public CompanyValidationResponse checkCompanyEligibility(CompanyProfileApi companyProfile) throws ServiceException {
        var response = new CompanyValidationResponse();
        try {
            for (EligibilityRule<CompanyProfileApi> eligibilityRule : eligibilityRules) {
                eligibilityRule.validate(companyProfile);
            }
        } catch (EligibilityException e) {
            LOGGER.info(String.format("Company %s ineligible to use the service because %s",  companyProfile.getCompanyNumber(), e.getEligibilityStatusCode()));
            response.setEligibilityStatusCode(e.getEligibilityStatusCode());
            return response;
        }
        response.setEligibilityStatusCode(EligibilityStatusCode.COMPANY_VALID_FOR_SERVICE);
        return response;
    }
}
