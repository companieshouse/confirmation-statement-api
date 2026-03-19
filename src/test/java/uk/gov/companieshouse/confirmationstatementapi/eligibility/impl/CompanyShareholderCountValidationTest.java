package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.CollectionUtils;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Description;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.service.ShareholderService;

import java.time.LocalDate;
import java.util.function.Supplier;

@ExtendWith(MockitoExtension.class)
class CompanyShareholderCountValidationTest {

    private static final String COMPANY_NUMBER = "12345678";

    private static final String LTD_BY_GUARANTEE = "test-private-limited-guarant-nsc";

    private static final String TARGET_ACTIVATION_DATE = "2026-01-01";
    private static final String BEFORE_TARGET_DATE = "2025-07-01";
    private static final String AFTER_TARGET_DATE = "2026-07-01";

    private String nowDate;
    private final Supplier<LocalDate> supplyNowDate =
            () -> LocalDate.parse(nowDate);

    private static final String[] COMPANY_TYPES_BASELINE = {"comp1","comp2","comp3"};
    private static final String[] COMPANY_TYPES_TARGET = {"comp4","comp5","comp6"};

    private static final String TEST_BASELINE_COMPANY = "comp2";
    private static final String TEST_TARGET_COMPANY = "comp5";
    private static final String TEST_NON_BASELINE_TARGET_COMPANY = "comp7";

    private CompanyProfileApi profile;
    private CompanyShareholderCountValidation validation;
    private LocalDate targetActivationDate;

    @Mock
    private ShareholderService shareholderService;

    @BeforeEach
    void beforeEach() {
        targetActivationDate = LocalDate.parse(TARGET_ACTIVATION_DATE);

        profile = new CompanyProfileApi();
        profile.setCompanyNumber(COMPANY_NUMBER);
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(shareholderService);
    }

    @Test
    @Description("Before Target Activation Date, Company Type in Baseline Company Types, Should not throw exception on company with no shareholders")
    void validateDoesNotThrowExceptionBeforeTargetDateBaselineCompanyTypeNoShareholders() throws ServiceException {
        nowDate = BEFORE_TARGET_DATE;
        validation = initialiseValidation();
        profile.setType(TEST_BASELINE_COMPANY);
        mockShareholderCount(0);

        assertDoesNotThrow(() -> validation.validate(profile));
        verifyShareholderServiceCalledOnce();
    }

    @Test
    @Description("Before Target Activation Date, Company Type in Baseline Company Types, Should not throw exception on company with one shareholder")
    void validateDoesNotThrowExceptionBeforeTargetDateBaselineCompanyTypeOneShareholder() throws ServiceException {
        nowDate = BEFORE_TARGET_DATE;
        validation = initialiseValidation();
        profile.setType(TEST_BASELINE_COMPANY);
        mockShareholderCount(1);

        assertDoesNotThrow(() -> validation.validate(profile));
        verifyShareholderServiceCalledOnce();
    }

    @Test
    @Description("Before Target Activation Date, Company Type in Baseline Company Types, Should throw exception on company with multiple shareholders")
    void validateThrowsExceptionBeforeTargetDateBaselineCompanyTypeMultipleShareholders() throws ServiceException {
        nowDate = BEFORE_TARGET_DATE;
        validation = initialiseValidation();
        profile.setType(TEST_BASELINE_COMPANY);
        mockShareholderCount(3);

        var ex = assertThrows(EligibilityException.class, () -> validation.validate(profile));

        assertEquals(EligibilityStatusCode.INVALID_COMPANY_APPOINTMENTS_MORE_THAN_ONE_SHAREHOLDER,
                ex.getEligibilityStatusCode());

        verifyShareholderServiceCalledOnce();
    }

    @Test
    @Description("Before Target Activation Date, Company Type not in Baseline Company Types, Should not validate")
    void validateNotRunBeforeTargetDateNonBaselineCompanyType() throws ServiceException {
        nowDate = BEFORE_TARGET_DATE;
        validation = initialiseValidation();
        profile.setType(TEST_NON_BASELINE_TARGET_COMPANY);

        verifyShareholderServiceNotCalled();
    }

    @Test
    @Description("After Target Activation Date, Company Type in Target Company Types, Should not throw exception on company with no shareholders")
    void validateDoesNotThrowExceptionAfterTargetDateTargetCompanyTypeNoShareholders() throws ServiceException {
        nowDate = AFTER_TARGET_DATE;
        validation = initialiseValidation();
        profile.setType(TEST_TARGET_COMPANY);
        mockShareholderCount(0);

        assertDoesNotThrow(() -> validation.validate(profile));

        verifyShareholderServiceCalledOnce();
    }

    @Test
    @Description("After Target Activation Date, Company Type in Target Company Types, Should not throw exception on company with one shareholder")
    void validateDoesNotThrowExceptionAfterTargetDateTargetCompanyTypeOneShareholder() throws ServiceException {
        nowDate = AFTER_TARGET_DATE;
        validation = initialiseValidation();
        profile.setType(TEST_TARGET_COMPANY);
        mockShareholderCount(1);

        assertDoesNotThrow(() -> validation.validate(profile));

        verifyShareholderServiceCalledOnce();
    }

    @Test
    @Description("After Target Activation Date, Company Type in Target Company Types, Should throw exception on company with multiple shareholders")
    void validateThrowsExceptionAfterTargetDateTargetCompanyTypeMultipleShareholders() throws ServiceException {
        nowDate = AFTER_TARGET_DATE;
        validation = initialiseValidation();
        profile.setType(TEST_TARGET_COMPANY);
        mockShareholderCount(3);

        var ex = assertThrows(EligibilityException.class, () -> validation.validate(profile));

        assertEquals(EligibilityStatusCode.INVALID_COMPANY_APPOINTMENTS_MORE_THAN_ONE_SHAREHOLDER,
                ex.getEligibilityStatusCode());

        verifyShareholderServiceCalledOnce();
    }

    @Test
    @Description("After Target Activation Date, Company Type not in Target Company Types, Should not validate")
    void validateNotRunAfterTargetDateNonTargetCompanyType() throws ServiceException {
        nowDate = AFTER_TARGET_DATE;
        validation = initialiseValidation();
        profile.setType(TEST_NON_BASELINE_TARGET_COMPANY);

        verifyShareholderServiceNotCalled();
    }

    private CompanyShareholderCountValidation initialiseValidation() {
        return new CompanyShareholderCountValidation(shareholderService,
                CollectionUtils.toSet(COMPANY_TYPES_BASELINE),
                CollectionUtils.toSet(COMPANY_TYPES_TARGET),
                targetActivationDate,
                supplyNowDate
                );
    }

    private void mockShareholderCount(int count) throws ServiceException {
        when(shareholderService.getShareholderCount(COMPANY_NUMBER)).thenReturn(count);
    }

    private void verifyShareholderServiceNotCalled() throws ServiceException {
        verify(shareholderService, never()).getShareholderCount(COMPANY_NUMBER);
    }

    private void verifyShareholderServiceCalledOnce() throws ServiceException {
        verify(shareholderService).getShareholderCount(COMPANY_NUMBER);
    }
}
