package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Description;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.service.ShareholderService;

@ExtendWith(MockitoExtension.class)
class CompanyShareholderCountValidationTest {

    private static final String COMPANY_NUMBER = "12345678";

    private static final String TARGET_ACTIVATION_DATE = "2026-01-01";
    private static final String MUD_BEFORE_TARGET_DATE = "2025-07-01";
    private static final String MUD_AFTER_TARGET_DATE = "2026-07-01";
    private static final String NOW_DATE = "2026-05-01";

    private final Supplier<LocalDate> supplyNowDate =
            () -> LocalDate.parse(NOW_DATE);

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
    @Description("MuD Before Target Activation Date, Company Type in Baseline Company Types, Should not throw exception on company with no shareholders")
    void validateAgainstMadeUpDateDoesNotThrowExceptionMuDBeforeTargetDateBaselineCompanyTypeNoShareholders() throws ServiceException {
        LocalDate mudDate = LocalDate.parse(MUD_BEFORE_TARGET_DATE);
        validation = initialiseValidation();
        profile.setType(TEST_BASELINE_COMPANY);
        mockShareholderCount(0);

        assertDoesNotThrow(() -> validation.validateAgainstMadeUpDate(profile, mudDate));
        verifyShareholderServiceCalledOnce();
    }

    @Test
    @Description("Before Target Activation Date, Company Type in Baseline Company Types, Should not throw exception on company with one shareholder")
    void validateAgainstMadeUpDateDoesNotThrowExceptionMuDBeforeTargetDateBaselineCompanyTypeOneShareholder() throws ServiceException {
        LocalDate mudDate = LocalDate.parse(MUD_BEFORE_TARGET_DATE);
        validation = initialiseValidation();
        profile.setType(TEST_BASELINE_COMPANY);
        mockShareholderCount(1);

        assertDoesNotThrow(() -> validation.validateAgainstMadeUpDate(profile, mudDate));
        verifyShareholderServiceCalledOnce();
    }

    @Test
    @Description("MuD Date Before Target Activation Date, Company Type in Baseline Company Types, Should throw exception on company with multiple shareholders")
    void validateAgainstMadeUpDateThrowsExceptionMuDBeforeTargetDateBaselineCompanyTypeMultipleShareholders() throws ServiceException {
        LocalDate mudDate = LocalDate.parse(MUD_BEFORE_TARGET_DATE);
        validation = initialiseValidation();
        profile.setType(TEST_BASELINE_COMPANY);
        mockShareholderCount(3);

        var ex = assertThrows(EligibilityException.class, () -> validation.validateAgainstMadeUpDate(profile, mudDate));

        assertEquals(EligibilityStatusCode.INVALID_COMPANY_APPOINTMENTS_MORE_THAN_ONE_SHAREHOLDER,
                ex.getEligibilityStatusCode());

        verifyShareholderServiceCalledOnce();
    }

    @Test
    @Description("MuD Before Target Activation Date, Company Type not in Baseline Company Types, Should not validate")
    void validateAgainstMadeUpDateNotRunMuDBeforeTargetDateNonBaselineCompanyType() throws ServiceException {
        LocalDate mudDate = LocalDate.parse(MUD_BEFORE_TARGET_DATE);
        validation = initialiseValidation();
        profile.setType(TEST_NON_BASELINE_TARGET_COMPANY);

        assertDoesNotThrow(() -> validation.validateAgainstMadeUpDate(profile, mudDate));

        verifyShareholderServiceNotCalled();
    }

    @Test
    @Description("MuD After Target Activation Date, Company Type in Target Company Types, Should not throw exception on company with no shareholders")
    void validateAgainstMadeUpDateDoesNotThrowExceptionMuDAfterTargetDateTargetCompanyTypeNoShareholders() throws ServiceException {
        LocalDate mudDate = LocalDate.parse(MUD_AFTER_TARGET_DATE);
        validation = initialiseValidation();
        profile.setType(TEST_TARGET_COMPANY);
        mockShareholderCount(0);

        assertDoesNotThrow(() -> validation.validateAgainstMadeUpDate(profile, mudDate));

        verifyShareholderServiceCalledOnce();
    }

    @Test
    @Description("MuD After Target Activation Date, Company Type in Target Company Types, Should not throw exception on company with one shareholder")
    void validateAgainstMadeUpDateDoesNotThrowExceptionMuDAfterTargetDateTargetCompanyTypeOneShareholder() throws ServiceException {
        LocalDate mudDate = LocalDate.parse(MUD_AFTER_TARGET_DATE);
        validation = initialiseValidation();
        profile.setType(TEST_TARGET_COMPANY);
        mockShareholderCount(1);

        assertDoesNotThrow(() -> validation.validateAgainstMadeUpDate(profile, mudDate));

        verifyShareholderServiceCalledOnce();
    }

    @Test
    @Description("MuD After Target Activation Date, Company Type in Target Company Types, Should throw exception on company with multiple shareholders")
    void validateAgainstMadeUpDateThrowsExceptionMuDAfterTargetDateTargetCompanyTypeMultipleShareholders() throws ServiceException {
        LocalDate mudDate = LocalDate.parse(MUD_AFTER_TARGET_DATE);
        validation = initialiseValidation();
        profile.setType(TEST_TARGET_COMPANY);
        mockShareholderCount(3);

        var ex = assertThrows(EligibilityException.class, () -> validation.validateAgainstMadeUpDate(profile, mudDate));

        assertEquals(EligibilityStatusCode.INVALID_COMPANY_APPOINTMENTS_MORE_THAN_ONE_SHAREHOLDER,
                ex.getEligibilityStatusCode());

        verifyShareholderServiceCalledOnce();
    }

    @Test
    @Description("MuD After Target Activation Date, Company Type not in Target Company Types, Should not validate")
    void validateAgainstMadeUpDateNotRunMuDAfterTargetDateNonTargetCompanyType() throws ServiceException {
        LocalDate mudDate = LocalDate.parse(MUD_AFTER_TARGET_DATE);
        validation = initialiseValidation();
        profile.setType(TEST_NON_BASELINE_TARGET_COMPANY);
        assertDoesNotThrow(() -> validation.validateAgainstMadeUpDate(profile, mudDate));
        verifyShareholderServiceNotCalled();
    }


    @Test
    @Description("No MuD, now date After Target Activation Date, Company Type in Target Company Types, Should not throw exception on company with no shareholders")
    void validateAgainstMadeUpDateDoesNotThrowExceptionNoMuDNowAfterTargetDateTargetCompanyTypeNoShareholders() throws ServiceException {
        validation = initialiseValidation();
        profile.setType(TEST_TARGET_COMPANY);
        mockShareholderCount(0);

        assertDoesNotThrow(() -> validation.validateAgainstMadeUpDate(profile, null));

        verifyShareholderServiceCalledOnce();
    }

    @Test
    @Description("No MuD, now date After Target Activation Date, Company Type in Target Company Types, Should not throw exception on company with one shareholder")
    void validateAgainstMadeUpDateDoesNotThrowExceptionNoMuDNowAfterTargetDateTargetCompanyTypeOneShareholder() throws ServiceException {
        validation = initialiseValidation();
        profile.setType(TEST_TARGET_COMPANY);
        mockShareholderCount(1);

        assertDoesNotThrow(() -> validation.validateAgainstMadeUpDate(profile, null));

        verifyShareholderServiceCalledOnce();
    }

    @Test
    @Description("No MuD, now date After Target Activation Date, Company Type in Target Company Types, Should throw exception on company with multiple shareholders")
    void validateAgainstMadeUpDateThrowsExceptionNoMuDNowAfterTargetDateTargetCompanyTypeMultipleShareholders() throws ServiceException {
        validation = initialiseValidation();
        profile.setType(TEST_TARGET_COMPANY);
        mockShareholderCount(3);

        var ex = assertThrows(EligibilityException.class, () -> validation.validateAgainstMadeUpDate(profile, null));

        assertEquals(EligibilityStatusCode.INVALID_COMPANY_APPOINTMENTS_MORE_THAN_ONE_SHAREHOLDER,
                ex.getEligibilityStatusCode());

        verifyShareholderServiceCalledOnce();
    }

    @Test
    @Description("No MuD, now date After Target Activation Date, Company Type not in Target Company Types, Should not validate")
    void validateAgainstMadeUpDateNotRunNoMuDNowAfterTargetDateNonTargetCompanyType() throws ServiceException {
        validation = initialiseValidation();
        profile.setType(TEST_NON_BASELINE_TARGET_COMPANY);
        assertDoesNotThrow(() -> validation.validateAgainstMadeUpDate(profile, null));
        verifyShareholderServiceNotCalled();
    }

    private CompanyShareholderCountValidation initialiseValidation() {
        return new CompanyShareholderCountValidation(shareholderService,
                Arrays.stream(COMPANY_TYPES_BASELINE).collect(Collectors.toSet()),
                Arrays.stream(COMPANY_TYPES_TARGET).collect(Collectors.toSet()),
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
