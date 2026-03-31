package uk.gov.companieshouse.confirmationstatementapi.configuration;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.impl.CompanyLimitedPartnershipSubTypeValidation;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.impl.CompanyMultipleOfficerValidation;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.impl.CompanyOfficerValidation;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.impl.CompanyPscCountValidation;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.impl.CompanyShareholderCountValidation;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.impl.CompanySingleOfficerValidation;
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

    private final Supplier<LocalDate> localDateNow;
    
    public ConfirmationStatementServiceEligibilityConfig(@Qualifier("localDateNow")Supplier<LocalDate> localDateNow) {
        this.localDateNow = localDateNow;
    }
    
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

    @Value("${CS01_SINGLE_OFFICER_VALIDATION_COMPANY_TYPES_BASELINE}")
    private Set<String> cs01SingleOfficerValidationCompanyTypeBaselineSet;

    @Value("${CS01_SINGLE_OFFICER_VALIDATION_COMPANY_TYPES_TARGET}")
    private Set<String> cs01SingleOfficerValidationCompanyTypeTargetSet;

    @Value("${CS01_SINGLE_OFFICER_VALIDATION_TARGET_ACTIVATION_DATE:2021-06-01}")
    private LocalDate cs01SingleOfficerValidationTargetActivationDate;

    @Value("${CS01_MULTIPLE_OFFICER_VALIDATION_COMPANY_TYPES_BASELINE}")
    private Set<String> cs01MultipleOfficerValidationCompanyTypeBaselineSet;

    @Value("${CS01_MULTIPLE_OFFICER_VALIDATION_COMPANY_TYPES_TARGET}")
    private Set<String> cs01MultipleOfficerValidationCompanyTypeTargetSet;

    @Value("${CS01_MULTIPLE_OFFICER_VALIDATION_TARGET_ACTIVATION_DATE:2021-10-21}")
    private LocalDate cs01MultipleOfficerValidationTargetActivationDate;

    @Value("${FEATURE_FLAG_PSC_VALIDATION_02062021:true}")
    private boolean pscValidationFeatureFlag;

    @Value("${CS01_TRADED_STATUS_VALIDATION_COMPANY_TYPES_BASELINE}")
    private Set<String> cs01TradedStatusValidationCompanyTypesBaselineSet;

    @Value("${CS01_TRADED_STATUS_VALIDATION_COMPANY_TYPES_TARGET}")
    private Set<String> cs01TradedStatusValidationCompanyTypesTargetSet;

    @Value("${CS01_TRADED_STATUS_VALIDATION_TARGET_ACTIVATION_DATE:2021-06-15}")
    private LocalDate cs01TradedStatusValidationTargetActivationDate;

    @Value("${FEATURE_FLAG_FIVE_OR_LESS_OFFICERS_JOURNEY_21102021:false}")
    private boolean multipleOfficerJourneyFeatureFlag;

    @Value("${CS01_LP_SUBTYPE_VALIDATION_COMPANY_TYPES_BASELINE}")
    private Set<String> cs01LPSubtypeValidationCompanyTypesBaselineSet;

    @Value("${CS01_LP_SUBTYPE_VALIDATION_COMPANY_TYPES_TARGET}")
    private Set<String> cs01LPSubtypeValidationCompanyTypesTargetSet;

    @Value("${CS01_LP_SUBTYPES_VALIDATION_TARGET_ACTIVATION_DATE:2099-12-31}")
    private LocalDate cs01LPSubtypeValidationTargetActivationDate;

    @Bean
    List<EligibilityRule<CompanyProfileApi>> confirmationStatementEligibilityRules(OfficerService officerService,
            PscService pscService, CorporateBodyService corporateBodyService, ShareholderService shareholderService) {
        var listOfRules = new ArrayList<EligibilityRule<CompanyProfileApi>>();

        var companyStatusValidation = new CompanyStatusValidation(allowedCompanyStatuses);
        var companyTypeValidationNoCS01Required = new CompanyTypeCS01FilingNotRequiredValidation(
                companyTypesNotRequiredToFileCS01);
        var companyTypeValidationForWebFiling = new CompanyTypeValidationForWebFiling(webFilingCompanyTypes);
        var companyTypeValidationPaperOnly = new CompanyTypeValidationPaperOnly(paperOnlyCompanyTypes);

        var companyMultipleOfficerValidation = new CompanyMultipleOfficerValidation(officerService,
                cs01MultipleOfficerValidationCompanyTypeBaselineSet,
                cs01MultipleOfficerValidationCompanyTypeTargetSet,
                cs01MultipleOfficerValidationTargetActivationDate,
                localDateNow);
        var companySingleOfficerValidation = new CompanySingleOfficerValidation(officerService,
                cs01SingleOfficerValidationCompanyTypeBaselineSet,
                cs01SingleOfficerValidationCompanyTypeTargetSet,
                cs01SingleOfficerValidationTargetActivationDate,
                localDateNow);
        var companyOfficerValidation = new CompanyOfficerValidation(officerService,
                companyMultipleOfficerValidation,
                companySingleOfficerValidation);

        var companyPscCountValidation = new CompanyPscCountValidation(pscService, pscValidationFeatureFlag, multipleOfficerJourneyFeatureFlag);
        var companyShareholderValidation = new CompanyShareholderCountValidation(shareholderService,
                cs01ShareholderCountValidationCompanyTypeBaselineSet,
                cs01ShareholderCountValidationCompanyTypeTargetSet,
                cs01ShareholderCountValidationTargetActivationDate, localDateNow);
        var companyTradedStatusValidation = new CompanyTradedStatusValidation(corporateBodyService, 
                cs01TradedStatusValidationCompanyTypesBaselineSet, 
                cs01TradedStatusValidationCompanyTypesTargetSet, 
                cs01TradedStatusValidationTargetActivationDate, localDateNow);
        var companyLimitedPartnershipSubTypeValidation = new CompanyLimitedPartnershipSubTypeValidation(
                cs01LPSubtypeValidationCompanyTypesBaselineSet,
                cs01LPSubtypeValidationCompanyTypesTargetSet,
                cs01LPSubtypeValidationTargetActivationDate, localDateNow);

        /* Check 1: Company Status */
        listOfRules.add(companyStatusValidation);

        /* Check 2: Company Type */
        listOfRules.add(companyTypeValidationNoCS01Required);
        listOfRules.add(companyTypeValidationForWebFiling);
        listOfRules.add(companyTypeValidationPaperOnly);
        listOfRules.add(companyLimitedPartnershipSubTypeValidation);

        /* Check 3: Officer -> Shareholder -> PSC */
        listOfRules.add(companyOfficerValidation);
        listOfRules.add(companyShareholderValidation);
        listOfRules.add(companyPscCountValidation);

        /* Check 4: Company traded status */
        listOfRules.add(companyTradedStatusValidation);

        return listOfRules;
    }
}
