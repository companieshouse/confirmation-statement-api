package uk.gov.companieshouse.confirmationstatementapi.eligibility;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.CollectionUtils;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;

import java.time.LocalDate;
import java.util.Set;
import java.util.function.Supplier;

@ExtendWith(MockitoExtension.class)
public class CompanyProfileApplicableEligibilityRuleTest {

    private static final String TARGET_ACTIVATION_DATE = "2026-01-01";
    private static final String BEFORE_TARGET_DATE = "2025-07-01";
    private static final String AFTER_TARGET_DATE = "2026-07-01";

    private String nowDate;
    private final Supplier<LocalDate> supplyNowDate =
            () -> LocalDate.parse(nowDate);

    private static final String[] COMPANY_TYPES_BASELINE = {"comp1","comp2","comp3"};
    private static final String[] COMPANY_TYPES_TARGET = {"comp4","comp5","comp6"};

    private CompanyProfileApplicableEligibilityRule testRule;
    private LocalDate targetActivationDate;

    @BeforeEach
    public void setUp() throws Exception {
        targetActivationDate = LocalDate.parse(TARGET_ACTIVATION_DATE);
    }

    @Test
    public void testCompanyApplicableForRuleReturnsTrueAfterActivationDateCompanyInTarget() {
        nowDate = AFTER_TARGET_DATE;
        testRule = initialiseTestRule();
        Assertions.assertTrue(testRule.companyApplicableForRule(createTestCompanyProfileApi("comp5")));
    }

    @Test
    public void testCompanyApplicableForRuleReturnsFalseAfterActivationDateCompanyNotInTarget() {
        nowDate = AFTER_TARGET_DATE;
        testRule = initialiseTestRule();
        Assertions.assertFalse(testRule.companyApplicableForRule(createTestCompanyProfileApi("comp2")));
    }

    @Test
    public void testCompanyApplicableForRuleReturnsTrueBeforeActivationDateCompanyInBaseline() {
        nowDate = BEFORE_TARGET_DATE;
        testRule = initialiseTestRule();
        Assertions.assertTrue(testRule.companyApplicableForRule(createTestCompanyProfileApi("comp2")));
    }

    @Test
    public void testCompanyApplicableForRuleReturnsFalseBeforeActivationDateCompanyNotInBaseline() {
        nowDate = BEFORE_TARGET_DATE;
        testRule = initialiseTestRule();
        Assertions.assertFalse(testRule.companyApplicableForRule(createTestCompanyProfileApi("comp5")));
    }

    private CompanyProfileApplicableEligibilityRule initialiseTestRule() {
        return new TestCompanyProfileApplicableEligibilityRule(CollectionUtils.toSet(COMPANY_TYPES_BASELINE),
                CollectionUtils.toSet(COMPANY_TYPES_TARGET), targetActivationDate,
                supplyNowDate);
    }

    private CompanyProfileApi createTestCompanyProfileApi(String type) {
        CompanyProfileApi companyProfileApi = new CompanyProfileApi();
        companyProfileApi.setType(type);
        return companyProfileApi;
    }

    private static class TestCompanyProfileApplicableEligibilityRule extends CompanyProfileApplicableEligibilityRule {

        TestCompanyProfileApplicableEligibilityRule(Set<String> baselineCompanyTypes, Set<String> targetCompanyTypes,
                                                    LocalDate activationDate, Supplier<LocalDate> now) {
            super(baselineCompanyTypes, targetCompanyTypes, activationDate, now);
        }

        @Override
        public void validate(CompanyProfileApi input) throws EligibilityException, ServiceException {

        }
    }

} 
