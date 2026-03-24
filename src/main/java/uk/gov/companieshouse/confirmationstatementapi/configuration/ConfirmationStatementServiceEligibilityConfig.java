package uk.gov.companieshouse.confirmationstatementapi.configuration;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.impl.CompanyOfficerValidation;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.impl.CompanyPscCountValidation;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.impl.CompanyShareholderCountValidation;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.impl.CompanyStatusValidation;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.impl.CompanyTradedStatusValidation;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.impl.CompanyTypeCS01FilingNotRequiredValidation;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.impl.CompanyTypeValidationForWebFiling;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.impl.CompanyTypeValidationPaperOnly;
import uk.gov.companieshouse.confirmationstatementapi.service.CorporateBodyService;
import uk.gov.companieshouse.confirmationstatementapi.service.OfficerService;
import uk.gov.companieshouse.confirmationstatementapi.service.PscService;
import uk.gov.companieshouse.confirmationstatementapi.service.ShareholderService;

@Configuration
public class ConfirmationStatementServiceEligibilityConfig {

    @Autowired
    public Supplier<LocalDate> localDateNow;

    @Value("${ALLOWED_COMPANY_STATUSES}")
    private Set<String> allowedCompanyStatuses;

    @Value("${COMPANY_TYPES_CS01_FILING_NOT_REQUIRED}")
    private Set<String> companyTypesNotRequiredToFileCS01;

    @Value("${PAPER_ONLY_COMPANY_TYPES}")
    private Set<String> paperOnlyCompanyTypes;

    @Value("${WEB_FILING_COMPANY_TYPES}")
    private Set<String> webFilingCompanyTypes;

    @Value("${CS01_SHAREHOLDER_VALIDATION_COMPANY_TYPES_BASELINE}")
    private Set<String> cs01ShareholderCountValidationCompanyTypeBaselineSet;

    @Value("${CS01_SHAREHOLDER_VALIDATION_COMPANY_TYPES_TARGET}")
    private Set<String> cs01ShareholderCountValidationCompanyTypeTargetSet;

    @Value("${CS01_SHAREHOLDER_VALIDATION_TARGET_ACTIVATION_DATE:2021-06-09}")
    private LocalDate cs01ShareholderCountValidationTargetActivationDate;

    @Value("${CS01_SINGLE_PSC_VALIDATION_COMPANY_TYPES_BASELINE:}")
    private Set<String> cs01SinglePscValidationCompanyTypesBaseline;

    @Value("${CS01_SINGLE_PSC_VALIDATION_COMPANY_TYPES_TARGET:}")
    private Set<String> cs01SinglePscValidationCompanyTypesTarget;

    @Value("${CS01_SINGLE_PSC_VALIDATION_TARGET_ACTIVATION_DATE:2021-06-02}")
    private LocalDate cs01SinglePscValidationTargetActivationDate;

    @Value("${CS01_MULTIPLE_PSC_VALIDATION_COMPANY_TYPES_BASELINE:}")
    private Set<String> cs01MultiplePscValidationCompanyTypesBaseline;

    @Value("${CS01_MULTIPLE_PSC_VALIDATION_COMPANY_TYPES_TARGET:}")
    private Set<String> cs01MultiplePscValidationCompanyTypesTarget;

    @Value("${CS01_MULTIPLE_PSC_VALIDATION_TARGET_ACTIVATION_DATE:2021-06-02}")
    private LocalDate cs01MultiplePscValidationTargetActivationDate;

    @Value("${FEATURE_FLAG_TRADED_STATUS_VALIDATION_150621:true}")
    private boolean tradedStatusFeatureFlag;

    @Value("${FEATURE_FLAG_FIVE_OR_LESS_OFFICERS_JOURNEY_21102021:false}")
    private boolean multipleOfficerJourneyFeatureFlag;

    @Bean
    List<EligibilityRule<CompanyProfileApi>> confirmationStatementEligibilityRules(OfficerService officerService,
            PscService pscService, CorporateBodyService corporateBodyService, ShareholderService shareholderService) {
        var listOfRules = new ArrayList<EligibilityRule<CompanyProfileApi>>();

        var companyStatusValidation = new CompanyStatusValidation(allowedCompanyStatuses);
        var companyTypeValidationNoCS01Required = new CompanyTypeCS01FilingNotRequiredValidation(
                companyTypesNotRequiredToFileCS01);
        var companyTypeValidationForWebFiling = new CompanyTypeValidationForWebFiling(webFilingCompanyTypes);
        var companyTypeValidationPaperOnly = new CompanyTypeValidationPaperOnly(paperOnlyCompanyTypes);
        var companyOfficerValidation = new CompanyOfficerValidation(officerService, multipleOfficerJourneyFeatureFlag);
        var companyPscCountValidation = new CompanyPscCountValidation(pscService,
                cs01MultiplePscValidationCompanyTypesBaseline,
                cs01MultiplePscValidationCompanyTypesTarget,
                cs01MultiplePscValidationTargetActivationDate,
                localDateNow,
                true
        );
        var companyTradedStatusValidation = new CompanyTradedStatusValidation(corporateBodyService, tradedStatusFeatureFlag);
        var companyShareholderValidation = new CompanyShareholderCountValidation(shareholderService,
                cs01ShareholderCountValidationCompanyTypeBaselineSet,
                cs01ShareholderCountValidationCompanyTypeTargetSet,
                cs01ShareholderCountValidationTargetActivationDate, localDateNow);

        /* Check 1: Company Status */
        listOfRules.add(companyStatusValidation);

        /* Check 2: Company Type */
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
