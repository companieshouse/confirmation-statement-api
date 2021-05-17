package uk.gov.companieshouse.confirmationstatementapi.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.impl.CompanyStatusValidation;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.impl.CompanyTypeValidationPaperOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Configuration
public class ConfirmationStatementServiceEligibilityConfig {

    @Value("${ALLOWED_COMPANY_STATUSES}")
    Set<String> allowedCompanyStatuses;

    @Value("${PAPER_ONLY_COMPANY_TYPES}")
    Set<String> paperOnlyCompanyTypes;

    @Bean
    @Qualifier("confirmation-statement-eligibility-rules")
    List<EligibilityRule<CompanyProfileApi>> confirmationStatementEligibilityRules() {
        var listOfRules = new ArrayList<EligibilityRule<CompanyProfileApi>>();

        var companyStatusValidation = new CompanyStatusValidation(allowedCompanyStatuses);
        var companyTypeValidationPaperOnly = new CompanyTypeValidationPaperOnly(paperOnlyCompanyTypes);

        listOfRules.add(companyStatusValidation);
        listOfRules.add(companyTypeValidationPaperOnly);

        return listOfRules;
    }
}