package uk.gov.companieshouse.confirmationstatementapi.eligibility;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;
import java.util.function.Supplier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.CollectionUtils;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;

@ExtendWith(MockitoExtension.class)
class CompanyProfileApplicableEligibilityRuleTest {

    private static final String TARGET_ACTIVATION_DATE = "2026-01-01";
    private static final String NOW_DATE = "2026-05-01";
    private static final String MUD_BEFORE_TARGET_DATE = "2025-07-01";
    private static final String MUD_AFTER_TARGET_DATE = "2026-07-01";

    private final Supplier<LocalDate> supplyNowDate =
            () -> LocalDate.parse(NOW_DATE);

    private static final String[] COMPANY_TYPES_BASELINE = {"comp1","comp2","comp3"};
    private static final String[] COMPANY_TYPES_TARGET = {"comp4","comp5","comp6"};

    private CompanyProfileApplicableEligibilityRule testRule;
    private LocalDate targetActivationDate;

    @BeforeEach
    public void setUp() {
        targetActivationDate = LocalDate.parse(TARGET_ACTIVATION_DATE);
    }

    @Test
    void testCompanyApplicableForRuleReturnsTrueMuDAfterActivationDateCompanyInTarget() {
        testRule = initialiseTestRule();
        Assertions.assertTrue(testRule.companyApplicableForRule(createTestCompanyProfileApi("comp5"),
                LocalDate.parse(MUD_AFTER_TARGET_DATE)));
    }

    @Test
    void testCompanyApplicableForRuleReturnsFalseMuDAfterActivationDateCompanyNotInTarget() {
        testRule = initialiseTestRule();
        Assertions.assertFalse(testRule.companyApplicableForRule(createTestCompanyProfileApi("comp2"),
                LocalDate.parse(MUD_AFTER_TARGET_DATE)));
    }

    @Test
    void testCompanyApplicableForRuleReturnsTrueMuDBeforeActivationDateCompanyInBaseline() {
        testRule = initialiseTestRule();
        Assertions.assertTrue(testRule.companyApplicableForRule(createTestCompanyProfileApi("comp2"),
                LocalDate.parse(MUD_BEFORE_TARGET_DATE)));
    }

    @Test
    void testCompanyApplicableForRuleReturnsFalseMuDBeforeActivationDateCompanyNotInBaseline() {
        testRule = initialiseTestRule();
        Assertions.assertFalse(testRule.companyApplicableForRule(createTestCompanyProfileApi("comp5"),
                LocalDate.parse(MUD_BEFORE_TARGET_DATE)));
    }

    @Test
    void testCompanyApplicableForRuleReturnsFalseMuDCompanyTypesEmpty() {
        testRule = new TestCompanyProfileApplicableEligibilityRule(Collections.emptySet(),
                Collections.emptySet(), targetActivationDate,
                supplyNowDate);
        Assertions.assertFalse(testRule.companyApplicableForRule(createTestCompanyProfileApi("comp5"),
                LocalDate.parse(MUD_BEFORE_TARGET_DATE)));
    }

    @Test
    void testCompanyApplicableForRuleReturnsFalseCompanyProfileNull() {
        testRule = new TestCompanyProfileApplicableEligibilityRule(Collections.emptySet(),
                Collections.emptySet(), targetActivationDate,
                supplyNowDate);
        Assertions.assertFalse(testRule.companyApplicableForRule(null,
                LocalDate.parse(MUD_BEFORE_TARGET_DATE)));
    }

    @Test
    void testCompanyApplicableForRuleReturnsFalseNoMuDCompanyNotInTarget() {
        testRule = initialiseTestRule();
        Assertions.assertFalse(testRule.companyApplicableForRule(createTestCompanyProfileApi("comp2"),
                null));
    }

    @Test
    void testCompanyApplicableForRuleReturnsTrueNoMuDCompanyInTarget() {
        testRule = initialiseTestRule();
        Assertions.assertTrue(testRule.companyApplicableForRule(createTestCompanyProfileApi("comp5"),
                null));
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
        public void validateAgainstMadeUpDate(CompanyProfileApi input, LocalDate madeUpDate) throws EligibilityException, ServiceException {
            /*
                Not called within the CompanyProfileApplicableEligibilityRuleTest tests
             */
        }
    }

} 
