package uk.gov.companieshouse.confirmationstatementapi.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.impl.CompanyPscCountValidation;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.impl.CompanyShareholderCountValidation;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.impl.CompanyStatusValidation;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.impl.CompanyTradedStatusValidation;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.impl.CompanyTypeCS01FilingNotRequiredValidation;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.impl.CompanyTypeValidationForWebFiling;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.impl.CompanyTypeValidationPaperOnly;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.impl.CompanyOfficerValidation;
import uk.gov.companieshouse.confirmationstatementapi.service.CorporateBodyService;
import uk.gov.companieshouse.confirmationstatementapi.service.OfficerService;
import uk.gov.companieshouse.confirmationstatementapi.service.PscService;
import uk.gov.companieshouse.confirmationstatementapi.service.ShareholderService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Configuration
public class ConfirmationStatementServiceEligibilityConfig {

    @Value("${ALLOWED_COMPANY_STATUSES}")
    private Set<String> allowedCompanyStatuses;

    @Value("${COMPANY_TYPES_CS01_FILING_NOT_REQUIRED}")
    private Set<String> companyTypesNotRequiredToFileCS01;

    @Value("${PAPER_ONLY_COMPANY_TYPES}")
    private Set<String> paperOnlyCompanyTypes;

    @Value("${WEB_FILING_COMPANY_TYPES}")
    private Set<String> webFilingCompanyTypes;

    @Value("${FEATURE_FLAG_OFFICER_VALIDATION_01062021:true}")
    private boolean officerValidationFlag;

    @Value("${FEATURE_FLAG_SHAREHOLDER_COUNT_VALIDATION_09062021:true}")
    private boolean shareholderCountalidationFeatureFlag;

    @Value("${FEATURE_FLAG_PSC_VALIDATION_02062021:true}")
    private boolean pscValidationFeatureFlag;

    @Bean
    @Qualifier("confirmation-statement-eligibility-rules")
    List<EligibilityRule<CompanyProfileApi>> confirmationStatementEligibilityRules(OfficerService officerService,
            PscService pscService, CorporateBodyService corporateBodyService, ShareholderService shareholderService) {
        var listOfRules = new ArrayList<EligibilityRule<CompanyProfileApi>>();

        var companyStatusValidation = new CompanyStatusValidation(allowedCompanyStatuses);
        var companyTypeValidationNoCS01Required = new CompanyTypeCS01FilingNotRequiredValidation(
                companyTypesNotRequiredToFileCS01);
        var companyTypeValidationForWebFiling = new CompanyTypeValidationForWebFiling(webFilingCompanyTypes);
        var companyTypeValidationPaperOnly = new CompanyTypeValidationPaperOnly(paperOnlyCompanyTypes);
        var companyOfficerValidation = new CompanyOfficerValidation(officerService, officerValidationFlag);
        var companyPscCountValidation = new CompanyPscCountValidation(pscService, pscValidationFeatureFlag);
        var companyTradedStatusValidation = new CompanyTradedStatusValidation(corporateBodyService);
        var companyShareholderValidation = new CompanyShareholderCountValidation(shareholderService, shareholderCountalidationFeatureFlag);

        listOfRules.add(companyStatusValidation);
        listOfRules.add(companyTypeValidationNoCS01Required);
        listOfRules.add(companyTypeValidationForWebFiling);
        listOfRules.add(companyTypeValidationPaperOnly);

                /* Check 3: Officer -> Shareholder -> PSC */
                listOfRules.add(companyOfficerValidation);
                listOfRules.add(companyShareholderValidation);
                listOfRules.add(companyPscCountValidation);

                /* Check 4: Company traded status */
                listOfRules.add(companyTradedStatusValidation);

                return listOfRules;
        }
}
