package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.context.annotation.Description;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Collectors;

class CompanyLimitedPartnershipSubTypeValidationTest {

    private static final String COMPANY_NUMBER = "12345678";

    private static final String TARGET_ACTIVATION_DATE = "2026-01-01";
    private static final String MUD_BEFORE_TARGET_DATE = "2025-07-01";
    private static final String MUD_AFTER_TARGET_DATE = "2026-07-01";
    private static final String NOW_DATE = "2026-05-01";

    private final Supplier<LocalDate> supplyNowDate =
            () -> LocalDate.parse(NOW_DATE);

    private static final String[] COMPANY_TYPES_BASELINE = {"comp1", "comp2", "comp3"};
    private static final String[] COMPANY_TYPES_TARGET = {"comp4", "comp5", "comp6"};

    private static final String TEST_BASELINE_COMPANY = "comp2";
    private static final String TEST_TARGET_COMPANY = "comp5";
    private static final String TEST_NON_BASELINE_TARGET_COMPANY = "comp7";

    private static final String INVALID_SUB_TYPE = "no a limited company";

    private CompanyProfileApi profile;
    private LocalDate targetActivationDate;
    private LocalDate mudDate;

    private CompanyLimitedPartnershipSubTypeValidation companyLimitedPartnershipSubTypeValidation;

    @BeforeEach
    public void setUp() {
        targetActivationDate = LocalDate.parse(TARGET_ACTIVATION_DATE);

        profile = new CompanyProfileApi();
        profile.setCompanyNumber(COMPANY_NUMBER);
    }

    // MuD before Target Date Tests
    @ParameterizedTest
    @CsvSource({
            "lp",
            "slp",
            "pflp",
            "spflp"
    })
    @Description("MadeUpDate Before Target Activation Date, Company Type in Baseline Company Types, " +
            "Should not throw exception on company with valid limited-partnership subtype")
    void validateAgainstMadeUpDateDoesNotThrowExceptionMuDBeforeTargetDateBaselineCompanyTypeValidSubType(String testSubType) {
        initialiseValidation(MUD_BEFORE_TARGET_DATE, TEST_BASELINE_COMPANY, testSubType);

        assertDoesNotThrow(() -> companyLimitedPartnershipSubTypeValidation.validateAgainstMadeUpDate(profile, mudDate));
    }

    @Test
    @Description("MadeUpDate Before Target Activation Date, Company Type in Baseline Company Types, " +
            "Should throw exception on company with invalid limited partnership subtype")
    void validateAgainstMadeUpDateDoesThrowExceptionMuDBeforeTargetDateBaselineCompanyTypeInvalidSubType() {
        initialiseValidation(MUD_BEFORE_TARGET_DATE, TEST_BASELINE_COMPANY, INVALID_SUB_TYPE);

        var ex = assertThrows(EligibilityException.class, () ->
                companyLimitedPartnershipSubTypeValidation.validateAgainstMadeUpDate(profile, mudDate));

        assertEquals(EligibilityStatusCode.INVALID_LIMITED_PARTNERSHIP_SUB_TYPE,
                ex.getEligibilityStatusCode());
    }

    @Test
    @Description("MadeUpDate Before Target Activation Date, Company Type in Baseline Company Types, " +
            "Should throw exception on company with no limited partnership subtype")
    void validateAgainstMadeUpDateDoesThrowExceptionMuDBeforeTargetDateBaselineCompanyTypeNoSubType() {
        initialiseValidation(MUD_BEFORE_TARGET_DATE, TEST_BASELINE_COMPANY, null);

        var ex = assertThrows(EligibilityException.class, () ->
                companyLimitedPartnershipSubTypeValidation.validateAgainstMadeUpDate(profile, mudDate));

        assertEquals(EligibilityStatusCode.INVALID_LIMITED_PARTNERSHIP_SUB_TYPE,
                ex.getEligibilityStatusCode());
    }

    @Test
    @Description("Made up Date Before Target Activation Date, Company Type not in Baseline Company Types, Should not validate")
    void validateAgainstMadeUpDateNotRunMuDBeforeTargetDateNonBaselineCompanyType() {
        initialiseValidation(MUD_BEFORE_TARGET_DATE, TEST_NON_BASELINE_TARGET_COMPANY, INVALID_SUB_TYPE);

        assertDoesNotThrow(() -> companyLimitedPartnershipSubTypeValidation.validateAgainstMadeUpDate(profile, mudDate));
    }

    // MuD after Target Date Tests
    @ParameterizedTest
    @CsvSource({
            "lp",
            "slp",
            "pflp",
            "spflp"
    })
    @Description("MadeUpDate After Target Activation Date, Company Type in Baseline Company Types, " +
            "Should not throw exception on company with valid limited-partnership subtype")
    void validateAgainstMadeUpDateDoesNotThrowExceptionMuDAfterTargetDateBaselineCompanyTypeValidSubType(String testSubType) {
        initialiseValidation(MUD_AFTER_TARGET_DATE, TEST_TARGET_COMPANY, testSubType);

        assertDoesNotThrow(() -> companyLimitedPartnershipSubTypeValidation.validateAgainstMadeUpDate(profile, mudDate));
    }

    @Test
    @Description("MadeUpDate After Target Activation Date, Company Type in Baseline Company Types, " +
            "Should throw exception on company with invalid limited partnership subtype")
    void validateAgainstMadeUpDateDoesThrowExceptionMuDAfterTargetDateBaselineCompanyTypeInvalidSubType() {
        initialiseValidation(MUD_AFTER_TARGET_DATE, TEST_TARGET_COMPANY, INVALID_SUB_TYPE);

        var ex = assertThrows(EligibilityException.class, () ->
                companyLimitedPartnershipSubTypeValidation.validateAgainstMadeUpDate(profile, mudDate));

        assertEquals(EligibilityStatusCode.INVALID_LIMITED_PARTNERSHIP_SUB_TYPE,
                ex.getEligibilityStatusCode());
    }

    @Test
    @Description("MadeUpDate After Target Activation Date, Company Type in Baseline Company Types, " +
            "Should throw exception on company with no limited partnership subtype")
    void validateAgainstMadeUpDateDoesThrowExceptionMuDAfterTargetDateBaselineCompanyTypeNoSubType() {
        initialiseValidation(MUD_AFTER_TARGET_DATE, TEST_TARGET_COMPANY, null);

        var ex = assertThrows(EligibilityException.class, () ->
                companyLimitedPartnershipSubTypeValidation.validateAgainstMadeUpDate(profile, mudDate));

        assertEquals(EligibilityStatusCode.INVALID_LIMITED_PARTNERSHIP_SUB_TYPE,
                ex.getEligibilityStatusCode());
    }

    @Test
    @Description("Made up Date After Target Activation Date, Company Type not in Baseline Company Types, Should not validate")
    void validateAgainstMadeUpDateNotRunMuDAfterTargetDateNonBaselineCompanyType() {
        initialiseValidation(MUD_AFTER_TARGET_DATE, TEST_NON_BASELINE_TARGET_COMPANY, INVALID_SUB_TYPE);

        assertDoesNotThrow(() -> companyLimitedPartnershipSubTypeValidation.validateAgainstMadeUpDate(profile, mudDate));
    }

    // No MuD, now date after Target Date tests
    @ParameterizedTest
    @CsvSource({
            "lp",
            "slp",
            "pflp",
            "spflp"
    })
    @Description("No Made up Date, now After Target Activation Date, Company Type in Baseline Company Types, " +
            "Should not throw exception on company with valid limited-partnership subtype")
    void validateAgainstMadeUpDateDoesNotThrowExceptionNoMuDNowAfterTargetDateBaselineCompanyTypeValidSubType(String testSubType) {
        initialiseValidation(null, TEST_TARGET_COMPANY, testSubType);

        assertDoesNotThrow(() -> companyLimitedPartnershipSubTypeValidation.validateAgainstMadeUpDate(profile, mudDate));
    }

    @Test
    @Description("No Made up Date, now After Target Activation Date, Company Type in Baseline Company Types, " +
            "Should throw exception on company with invalid limited partnership subtype")
    void validateAgainstMadeUpDateDoesThrowExceptionNoMuDNowAfterTargetDateBaselineCompanyTypeInvalidSubType() {
        initialiseValidation(null, TEST_TARGET_COMPANY, INVALID_SUB_TYPE);

        var ex = assertThrows(EligibilityException.class, () ->
                companyLimitedPartnershipSubTypeValidation.validateAgainstMadeUpDate(profile, mudDate));

        assertEquals(EligibilityStatusCode.INVALID_LIMITED_PARTNERSHIP_SUB_TYPE,
                ex.getEligibilityStatusCode());
    }

    @Test
    @Description("No Made up Date, now After Target Activation Date, Company Type in Baseline Company Types, " +
            "Should throw exception on company with no limited partnership subtype")
    void validateAgainstMadeUpDateDoesThrowExceptionNoMuDNowAfterTargetDateBaselineCompanyTypeNoSubType() {
        initialiseValidation(null, TEST_TARGET_COMPANY, null);

        var ex = assertThrows(EligibilityException.class, () ->
                companyLimitedPartnershipSubTypeValidation.validateAgainstMadeUpDate(profile, mudDate));

        assertEquals(EligibilityStatusCode.INVALID_LIMITED_PARTNERSHIP_SUB_TYPE,
                ex.getEligibilityStatusCode());
    }

    @Test
    @Description("No Made up Date, now After Target Activation Date, Company Type not in Baseline Company Types, Should not validate")
    void validateAgainstMadeUpDateNotRunNoMuDNowAfterTargetDateNonBaselineCompanyType() {
        initialiseValidation(null, TEST_NON_BASELINE_TARGET_COMPANY, INVALID_SUB_TYPE);

        assertDoesNotThrow(() -> companyLimitedPartnershipSubTypeValidation.validateAgainstMadeUpDate(profile, mudDate));
    }


    private void initialiseValidation(String mudDateString, String testCompanyType, String testSubType) {
        mudDate = (mudDateString == null) ? null : LocalDate.parse(mudDateString);

        profile.setType(testCompanyType);
        profile.setSubtype(testSubType);

        companyLimitedPartnershipSubTypeValidation = new CompanyLimitedPartnershipSubTypeValidation(
                Arrays.stream(COMPANY_TYPES_BASELINE).collect(Collectors.toSet()),
                Arrays.stream(COMPANY_TYPES_TARGET).collect(Collectors.toSet()),
                targetActivationDate,
                supplyNowDate
        );
    }
}