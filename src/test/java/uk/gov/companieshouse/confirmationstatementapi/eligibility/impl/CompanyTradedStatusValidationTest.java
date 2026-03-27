package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

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
import uk.gov.companieshouse.confirmationstatementapi.model.CompanyTradedStatusType;
import uk.gov.companieshouse.confirmationstatementapi.service.CorporateBodyService;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
class CompanyTradedStatusValidationTest {

    private static final String COMPANY_NUMBER = "12345678";

    private static final String TARGET_ACTIVATION_DATE = "2021-06-15";
    private static final String BEFORE_TARGET_DATE = "2021-06-01";
    private static final String AFTER_TARGET_DATE = "2021-06-20";

    private String nowDate;
    private final Supplier<LocalDate> supplyNowDate =
            () -> LocalDate.parse(nowDate);

    private static final String[] COMPANY_TYPES_BASELINE = {"comp1","comp2","comp3"};
    private static final String[] COMPANY_TYPES_TARGET = {"comp4","comp5","comp6"};

    private static final String TEST_BASELINE_COMPANY = "comp2";
    private static final String TEST_TARGET_COMPANY = "comp5";
    private static final String TEST_NON_BASELINE_TARGET_COMPANY = "comp7";        

    private LocalDate targetActivationDate;

    @Mock
    private CompanyProfileApi companyProfileApi;

    @Mock
    private CorporateBodyService corporateBodyService;

    private CompanyTradedStatusValidation companyTradedStatusValidation;

    @BeforeEach
    void beforeEach() {
        targetActivationDate = LocalDate.parse(TARGET_ACTIVATION_DATE);
        
        companyProfileApi = new CompanyProfileApi();
        companyProfileApi.setCompanyNumber(COMPANY_NUMBER);
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(corporateBodyService);
    }

    @Test
    @Description("Should not throw when traded status is valid before activation date")
    void validateDoesNotThrowOnValidTradedStatus() throws ServiceException {
        nowDate = BEFORE_TARGET_DATE;
        companyTradedStatusValidation = initialiseValidation();
        companyProfileApi.setType(TEST_BASELINE_COMPANY);

        when(corporateBodyService.getCompanyTradedStatus(COMPANY_NUMBER))
                .thenReturn(CompanyTradedStatusType.NOT_ADMITTED_TO_TRADING);
        
        assertDoesNotThrow(() -> companyTradedStatusValidation.validate(companyProfileApi));
    }

    @Test
    @Description("Should skip validation when feature flag is off")
    void validateDoesNotRunWhenFlagOff() {
        nowDate = BEFORE_TARGET_DATE;
        companyProfileApi.setType(TEST_BASELINE_COMPANY);

        companyTradedStatusValidation = new CompanyTradedStatusValidation(corporateBodyService, 
                                                Set.of(), 
                                                Set.of(), 
                                                targetActivationDate, supplyNowDate);

        assertDoesNotThrow(() -> companyTradedStatusValidation.validate(companyProfileApi));
        verifyNoInteractions(corporateBodyService);
    }

    @Test
    @Description("Should validate baseline company type before activation date without exceptions")
    void validateBaselineBeforeActivationDate() {
        nowDate = TARGET_ACTIVATION_DATE;
        companyProfileApi.setType(TEST_BASELINE_COMPANY);

        companyTradedStatusValidation = initialiseValidation();

        assertDoesNotThrow(() -> companyTradedStatusValidation.validate(companyProfileApi));
    }

    @Test
    @Description("Should skip validation when company type is not included in applicable types")
    void validateDoesNotRunWhenCompanyTypeNotIncluded() {
        nowDate = BEFORE_TARGET_DATE;
        companyProfileApi.setType(TEST_NON_BASELINE_TARGET_COMPANY);

        companyTradedStatusValidation = initialiseValidation();

        assertDoesNotThrow(() -> companyTradedStatusValidation.validate(companyProfileApi));
        verifyNoInteractions(corporateBodyService);
    }

    @Test
    @Description("Should validate target company type after activation date without throwing")
    void validateTargetCompanyTypeAfterActivationDateRuns() throws ServiceException {
        nowDate = AFTER_TARGET_DATE;
        companyProfileApi.setType(TEST_TARGET_COMPANY);

        companyTradedStatusValidation = initialiseValidation();

        when(corporateBodyService.getCompanyTradedStatus(COMPANY_NUMBER))
            .thenReturn(CompanyTradedStatusType.NOT_ADMITTED_TO_TRADING);

        assertDoesNotThrow(() -> companyTradedStatusValidation.validate(companyProfileApi));

        verify(corporateBodyService).getCompanyTradedStatus(COMPANY_NUMBER);
    }

    @Test
    @Description("Should validate target company type exactly on activation date without throwing")
    void validateTargetCompanyTypeOnActivationDateRuns() throws ServiceException {
        nowDate = TARGET_ACTIVATION_DATE;
        companyProfileApi.setType(TEST_TARGET_COMPANY);

        companyTradedStatusValidation = initialiseValidation();

        when(corporateBodyService.getCompanyTradedStatus(COMPANY_NUMBER))
            .thenReturn(CompanyTradedStatusType.NOT_ADMITTED_TO_TRADING);

        assertDoesNotThrow(() -> companyTradedStatusValidation.validate(companyProfileApi));

        verify(corporateBodyService).getCompanyTradedStatus(COMPANY_NUMBER);
    }

    @Test
    @Description("Should validate when baseline types are empty and target types are present")
    void validateTargetCompanyTypeEmptyBaselineTypesRuns() throws ServiceException {
        nowDate = AFTER_TARGET_DATE;
        companyProfileApi.setType(TEST_TARGET_COMPANY);

        companyTradedStatusValidation = new CompanyTradedStatusValidation(corporateBodyService, 
                                                Set.of(),
                                                getTargetLineCompanyTypesSet(), 
                                                targetActivationDate, supplyNowDate);


        when(corporateBodyService.getCompanyTradedStatus(COMPANY_NUMBER))
            .thenReturn(CompanyTradedStatusType.NOT_ADMITTED_TO_TRADING);

        assertDoesNotThrow(() -> companyTradedStatusValidation.validate(companyProfileApi));
        verify(corporateBodyService).getCompanyTradedStatus(COMPANY_NUMBER);
    }

    @Test
    @Description("Should validate when target types are empty and baseline types are present")
    void validateBaselineCompanyTypeEmptyTargetTypesRuns() throws ServiceException {
        nowDate = BEFORE_TARGET_DATE;
        companyProfileApi.setType(TEST_BASELINE_COMPANY);

        companyTradedStatusValidation = new CompanyTradedStatusValidation(corporateBodyService, 
                                                getBaseLineCompanyTypesSet(),
                                                Set.of(), 
                                                targetActivationDate, supplyNowDate);

        when(corporateBodyService.getCompanyTradedStatus(COMPANY_NUMBER))
            .thenReturn(CompanyTradedStatusType.NOT_ADMITTED_TO_TRADING);

        assertDoesNotThrow(() -> companyTradedStatusValidation.validate(companyProfileApi));
        verify(corporateBodyService).getCompanyTradedStatus(COMPANY_NUMBER);
    }

    @Test
    @Description("Should throw EligibilityException when traded status is invalid")
    void validateDoesThrowOnInvalidTradedStatus() throws ServiceException {
        nowDate = AFTER_TARGET_DATE;
        companyProfileApi.setType(TEST_TARGET_COMPANY);

        companyTradedStatusValidation = initialiseValidation();

        when(corporateBodyService.getCompanyTradedStatus(COMPANY_NUMBER))
                .thenReturn(CompanyTradedStatusType.ADMITTED_TO_TRADING_AND_DTR5_APPLIED);

        var ex = assertThrows(EligibilityException.class,
                () -> companyTradedStatusValidation.validate(companyProfileApi));

        assertEquals(EligibilityStatusCode.INVALID_COMPANY_TRADED_STATUS_USE_WEBFILING,
                ex.getEligibilityStatusCode());
    }

    @Test
    @Description("Should use madeUpToDate before activation date even if nowDate is after")
    void validateUsesMadeUpToDateBeforeActivation() throws ServiceException {
        nowDate = AFTER_TARGET_DATE;
        LocalDate madeUpToDate = LocalDate.parse(BEFORE_TARGET_DATE);

        companyProfileApi.setType(TEST_BASELINE_COMPANY);
        companyTradedStatusValidation = initialiseValidation();

        when(corporateBodyService.getCompanyTradedStatus(COMPANY_NUMBER))
                .thenReturn(CompanyTradedStatusType.NOT_ADMITTED_TO_TRADING);

        assertDoesNotThrow(() ->
                companyTradedStatusValidation.validateAgainstMadeUpDate(companyProfileApi, madeUpToDate));

        verify(corporateBodyService).getCompanyTradedStatus(COMPANY_NUMBER);
    }

    @Test
    @Description("Should validate when madeUpToDate is after activation date")
    void validateRunsWhenMadeUpToDateAfterActivation() throws ServiceException {
        nowDate = BEFORE_TARGET_DATE;
        LocalDate madeUpToDate = LocalDate.parse(AFTER_TARGET_DATE);

        companyProfileApi.setType(TEST_TARGET_COMPANY);
        companyTradedStatusValidation = initialiseValidation();

        when(corporateBodyService.getCompanyTradedStatus(COMPANY_NUMBER))
                .thenReturn(CompanyTradedStatusType.NOT_ADMITTED_TO_TRADING);

        assertDoesNotThrow(() ->
                companyTradedStatusValidation.validateAgainstMadeUpDate(companyProfileApi, madeUpToDate));

        verify(corporateBodyService).getCompanyTradedStatus(COMPANY_NUMBER);
    }

    @Test
    @Description("Should skip validation when madeUpToDate is before activation for target type")
    void validateSkipsWhenMadeUpToDateBeforeActivationForTargetType() {
        nowDate = AFTER_TARGET_DATE;
        LocalDate madeUpToDate = LocalDate.parse(BEFORE_TARGET_DATE);

        companyProfileApi.setType(TEST_TARGET_COMPANY);
        companyTradedStatusValidation = initialiseValidation();

        assertDoesNotThrow(() ->
                companyTradedStatusValidation.validateAgainstMadeUpDate(companyProfileApi, madeUpToDate));

        verifyNoInteractions(corporateBodyService);
    }

    @Test
    @Description("Should throw when madeUpToDate triggers invalid traded status")
    void validateThrowsWhenInvalidTradedStatusUsingMadeUpToDate() throws ServiceException {
        nowDate = BEFORE_TARGET_DATE;
        LocalDate madeUpToDate = LocalDate.parse(AFTER_TARGET_DATE);

        companyProfileApi.setType(TEST_TARGET_COMPANY);
        companyTradedStatusValidation = initialiseValidation();

        when(corporateBodyService.getCompanyTradedStatus(COMPANY_NUMBER))
                .thenReturn(CompanyTradedStatusType.ADMITTED_TO_TRADING_AND_DTR5_APPLIED);

        var ex = assertThrows(EligibilityException.class,
                () -> companyTradedStatusValidation.validateAgainstMadeUpDate(companyProfileApi, madeUpToDate));

        assertEquals(EligibilityStatusCode.INVALID_COMPANY_TRADED_STATUS_USE_WEBFILING,
                ex.getEligibilityStatusCode());
    }

    private CompanyTradedStatusValidation initialiseValidation() {        
        return new CompanyTradedStatusValidation(corporateBodyService, 
                getBaseLineCompanyTypesSet(),
                getTargetLineCompanyTypesSet(), 
                targetActivationDate, supplyNowDate);
    }

    private Set<String> getBaseLineCompanyTypesSet() {
        return Arrays.stream(COMPANY_TYPES_BASELINE).collect(Collectors.toSet());
    }

    private Set<String> getTargetLineCompanyTypesSet() {
        return Arrays.stream(COMPANY_TYPES_TARGET).collect(Collectors.toSet());
    }
}
