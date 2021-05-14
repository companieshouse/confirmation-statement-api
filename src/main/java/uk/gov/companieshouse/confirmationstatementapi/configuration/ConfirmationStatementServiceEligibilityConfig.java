package uk.gov.companieshouse.confirmationstatementapi.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.impl.CompanyStatusValidation;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.impl.CompanyTypeValidationForWebFiling;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Configuration
public class ConfirmationStatementServiceEligibilityConfig {

    @Value("${ALLOWED_COMPANY_STATUSES}")
    Set<String> allowedCompanyStatuses;

    @Value("${WEB_FILING_COMPANY_TYPES}")
    Set<String> webFilingCompanyTypes;

    @Bean
    @Qualifier("confirmation-statement-eligibility-rules")
    List<EligibilityRule<CompanyProfileApi>> confirmationStatementEligibilityRules() {
        var listOfRules = new ArrayList<EligibilityRule<CompanyProfileApi>>();

        var companyStatusValidation = new CompanyStatusValidation(allowedCompanyStatuses);
        var companyTypeValidationForWebFiling = new CompanyTypeValidationForWebFiling(webFilingCompanyTypes);

        listOfRules.add(companyStatusValidation);
        listOfRules.add(companyTypeValidationForWebFiling);

        return listOfRules;
    }
}
