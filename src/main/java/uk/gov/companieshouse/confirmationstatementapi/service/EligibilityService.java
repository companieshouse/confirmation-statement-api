package uk.gov.companieshouse.confirmationstatementapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.ConfirmationStatementApiApplication;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.model.response.CompanyValidationResponse;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.util.List;

@Service
public class EligibilityService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfirmationStatementApiApplication.APP_NAME);
    private final List<EligibilityRule<CompanyProfileApi>> eligibilityRules;

    @Autowired
    public EligibilityService(@Qualifier("confirmation-statement-eligibility-rules")
                                                    List<EligibilityRule<CompanyProfileApi>> eligibilityRules){
        this.eligibilityRules = eligibilityRules;
    }

    public CompanyValidationResponse checkCompanyEligibility(CompanyProfileApi companyProfile) throws ServiceException {
        CompanyValidationResponse response = new CompanyValidationResponse();
        try {
            for (EligibilityRule<CompanyProfileApi> eligibilityRule : eligibilityRules) {
                eligibilityRule.validate(companyProfile);
            }
        } catch (EligibilityException e) {
            LOGGER.info(String.format("Company %s ineligible to use the service because %s", companyProfile.getCompanyNumber(), e.getEligibilityStatusCode().toString()));
            response.setEligibilityStatusCode(e.getEligibilityStatusCode());
            return response;
        }
        response.setEligibilityStatusCode(EligibilityStatusCode.COMPANY_VALID_FOR_SERVICE);
        return response;
    }
}
