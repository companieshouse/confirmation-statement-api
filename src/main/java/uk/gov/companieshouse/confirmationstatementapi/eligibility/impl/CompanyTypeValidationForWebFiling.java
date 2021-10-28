package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.utils.ApiLogger;

import java.util.HashMap;
import java.util.Set;

public class CompanyTypeValidationForWebFiling implements EligibilityRule<CompanyProfileApi> {

    private final Set<String> webFilingTypes;

    public CompanyTypeValidationForWebFiling(Set<String> webFilingTypes) {
        this.webFilingTypes = webFilingTypes;
    }

    @Override
    public void validate(CompanyProfileApi profileToValidate) throws EligibilityException {
        var logMap = new HashMap<String, Object>();
        logMap.put("companyProfile", profileToValidate);
        ApiLogger.info(String.format("Validating Company Type Should Use Web Filing for: %s", profileToValidate.getCompanyNumber()), logMap);
        if (webFilingTypes.contains(profileToValidate.getType())) {
            ApiLogger.info(String.format("Company Type validation Should Use Web Filing failed for: %s", profileToValidate.getCompanyNumber()), logMap);
            throw new EligibilityException(EligibilityStatusCode.INVALID_COMPANY_TYPE_USE_WEB_FILING);
        }
        ApiLogger.info(String.format("Company Type validation Should Use Web Filing passed for: %s", profileToValidate.getCompanyNumber()), logMap);
    }
}
