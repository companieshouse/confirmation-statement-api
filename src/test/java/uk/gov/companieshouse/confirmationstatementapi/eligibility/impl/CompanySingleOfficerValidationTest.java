package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.officers.CompanyOfficerApi;
import uk.gov.companieshouse.api.model.officers.OfficerRoleApi;
import uk.gov.companieshouse.api.model.officers.OfficersApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.service.OfficerService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanySingleOfficerValidationTest {

    private static final String MUD_BEFORE_ACTIVATION_DATE = "2025-07-01";
    private static final String MUD_AFTER_ACTIVATION_DATE = "2026-07-01";
    private static final String NOW_DATE = "2026-05-01";

    private final Supplier<LocalDate> supplyNowDate =
            () -> LocalDate.parse(NOW_DATE);

    private static final Set<String> SINGLE_COMPANY_TYPES_BASELINE = Set.of("comp7","comp8","comp9");
    private static final Set<String> SINGLE_COMPANY_TYPES_TARGET = Set.of("comp10","comp11","comp12","limitied-partnership");
    private static final String SINGLE_COMPANY_TYPES_ACTIVATION_DATE = "2026-01-01";

    private static final String COMPANY_NUMBER = "12345678";
    private static final List<CompanyOfficerApi> OFFICER_LIST = new ArrayList<>();

    OfficersApi mockOfficers = new OfficersApi();
    CompanyProfileApi companyProfileApi = new CompanyProfileApi();

    @Mock
    private OfficerService officerService;
    private CompanySingleOfficerValidation companySingleOfficerValidation;
    private LocalDate singleTargetActivationDate;
    private LocalDate mudBeforeActivationDate;
    private LocalDate mudAfterActivationDate;

    @BeforeEach
    void init() {
        OFFICER_LIST.clear();
        singleTargetActivationDate = LocalDate.parse(SINGLE_COMPANY_TYPES_ACTIVATION_DATE);
        mudBeforeActivationDate = LocalDate.parse(MUD_BEFORE_ACTIVATION_DATE);
        mudAfterActivationDate = LocalDate.parse(MUD_AFTER_ACTIVATION_DATE);
        CompanyOfficerApi mockOfficer = new CompanyOfficerApi();
        mockOfficer.setOfficerRole(OfficerRoleApi.DIRECTOR);
        OFFICER_LIST.add(mockOfficer);
        companyProfileApi.setCompanyNumber(COMPANY_NUMBER);

        companySingleOfficerValidation = new CompanySingleOfficerValidation(officerService,
                SINGLE_COMPANY_TYPES_BASELINE,
                SINGLE_COMPANY_TYPES_TARGET,
                singleTargetActivationDate,
                supplyNowDate);

    }

    @ParameterizedTest
    @CsvSource({
            "true, comp7",
            "false, comp10",
            "false, limitied-partnership"
    })
    void validateDoesNotThrowExceptionOnSingleOfficerCompanyWithDirectorWhenPerformSingleOfficerCheck(boolean isMudBeforeActivationDate, String baselineOrTargetCompanyType) throws ServiceException {
        companyProfileApi.setType(baselineOrTargetCompanyType);
        mockOfficers.setItems(OFFICER_LIST);
        mockOfficers.setActiveCount((long) OFFICER_LIST.size());

        when(officerService.getOfficers(COMPANY_NUMBER)).thenReturn(mockOfficers);

       assertDoesNotThrow(() -> companySingleOfficerValidation.validateAgainstMadeUpDate(companyProfileApi, getBeforeOrAfterMadeUpDate(isMudBeforeActivationDate)));
    }

    @ParameterizedTest
    @CsvSource({
            "true, comp8",
            "false, comp11",
            "false, limitied-partnership"
    })
    void validateThrowsExceptionOnSingleOfficerCompanyWithSecretaryWhenPerformSingleOfficerCheck(boolean isMudBeforeActivationDate, String baselineOrTargetCompanyType) throws ServiceException {
        OFFICER_LIST.clear();
        companyProfileApi.setType(baselineOrTargetCompanyType);
        CompanyOfficerApi mockOfficer = new CompanyOfficerApi();
        mockOfficer.setOfficerRole(OfficerRoleApi.SECRETARY);
        OFFICER_LIST.add(mockOfficer);
        mockOfficers.setItems(OFFICER_LIST);
        mockOfficers.setActiveCount((long) OFFICER_LIST.size());

        when(officerService.getOfficers(COMPANY_NUMBER)).thenReturn(mockOfficers);

        var ex = assertThrows(EligibilityException.class, () -> companySingleOfficerValidation.validateAgainstMadeUpDate(companyProfileApi, getBeforeOrAfterMadeUpDate(isMudBeforeActivationDate)));

        assertEquals(EligibilityStatusCode.INVALID_COMPANY_APPOINTMENTS_INVALID_NUMBER_OF_OFFICERS,ex.getEligibilityStatusCode() );
    }

    @ParameterizedTest
    @CsvSource({
            "true, comp9",
            "false, comp12",
            "false, limitied-partnership"
    })
    void validateThrowsExceptionOnMultipleOfficerCompanyWhenPerformSingleOfficerCheck(boolean isMudBeforeActivationDate, String baselineOrTargetCompanyType) throws ServiceException {
        companyProfileApi.setType(baselineOrTargetCompanyType);
        CompanyOfficerApi director = new CompanyOfficerApi();
        director.setOfficerRole(OfficerRoleApi.CORPORATE_DIRECTOR);
        OFFICER_LIST.add(director);
        mockOfficers.setItems(OFFICER_LIST);
        mockOfficers.setActiveCount((long) OFFICER_LIST.size());

        when(officerService.getOfficers(COMPANY_NUMBER)).thenReturn(mockOfficers);

        var ex = assertThrows(EligibilityException.class, () -> companySingleOfficerValidation.validateAgainstMadeUpDate(companyProfileApi, getBeforeOrAfterMadeUpDate(isMudBeforeActivationDate)));

        assertEquals(EligibilityStatusCode.INVALID_COMPANY_APPOINTMENTS_INVALID_NUMBER_OF_OFFICERS,ex.getEligibilityStatusCode() );
    }

    @ParameterizedTest
    @CsvSource({
            "true, comp7",
            "false, comp10",
            "false, limitied-partnership"
    })
    void validateThrowsExceptionOnSingleDirectorCompanyWithSecretariesWhenPerformSingleOfficerCheck(boolean isMudBeforeActivationDate, String baselineOrTargetCompanyType) throws ServiceException {
        companyProfileApi.setType(baselineOrTargetCompanyType);
        CompanyOfficerApi secretary = new CompanyOfficerApi();
        secretary.setOfficerRole(OfficerRoleApi.SECRETARY);
        OFFICER_LIST.add(secretary);
        mockOfficers.setItems(OFFICER_LIST);
        mockOfficers.setActiveCount((long) OFFICER_LIST.size());

        when(officerService.getOfficers(COMPANY_NUMBER)).thenReturn(mockOfficers);

        var ex = assertThrows(EligibilityException.class, () -> companySingleOfficerValidation.validateAgainstMadeUpDate(companyProfileApi, getBeforeOrAfterMadeUpDate(isMudBeforeActivationDate)));

        assertEquals(EligibilityStatusCode.INVALID_COMPANY_APPOINTMENTS_INVALID_NUMBER_OF_OFFICERS,ex.getEligibilityStatusCode() );
    }

    @Test
    void isOfficerDirectorReturnsFalseForMultipleOfficerCompany() {
        CompanyOfficerApi director = new CompanyOfficerApi();
        CompanyOfficerApi director2 = new CompanyOfficerApi();
        CompanyOfficerApi secretary = new CompanyOfficerApi();
        director.setOfficerRole(OfficerRoleApi.NOMINEE_DIRECTOR);
        director2.setOfficerRole(OfficerRoleApi.CORPORATE_DIRECTOR);
        secretary.setOfficerRole(OfficerRoleApi.SECRETARY);

        OFFICER_LIST.add(director);
        OFFICER_LIST.add(director2);
        OFFICER_LIST.add(secretary);
        mockOfficers.setItems(OFFICER_LIST);
        mockOfficers.setActiveCount((long) OFFICER_LIST.size());

        var result = companySingleOfficerValidation.isOfficerDirector(mockOfficers.getItems(), mockOfficers.getActiveCount());
        assertFalse(result);
    }

    @Test
    void isOfficerDirectorReturnsTrueForCompanyWithTwoDirectorsOneRetired() {
        CompanyOfficerApi director = new CompanyOfficerApi();
        director.setOfficerRole(OfficerRoleApi.NOMINEE_DIRECTOR);
        director.setResignedOn(LocalDate.now());

        OFFICER_LIST.add(director);
        mockOfficers.setItems(OFFICER_LIST);
        mockOfficers.setActiveCount(1L);

        var result = companySingleOfficerValidation.isOfficerDirector(mockOfficers.getItems(), mockOfficers.getActiveCount());
        assertTrue(result);
    }

    @Test
    void isOfficerDirectorReturnsFalseForCompanyWithOneSecretary() {
        OFFICER_LIST.clear();
        CompanyOfficerApi mockOfficer = new CompanyOfficerApi();
        mockOfficer.setOfficerRole(OfficerRoleApi.SECRETARY);
        OFFICER_LIST.add(mockOfficer);
        mockOfficers.setItems(OFFICER_LIST);
        mockOfficers.setActiveCount((long) OFFICER_LIST.size());

        var result = companySingleOfficerValidation.isOfficerDirector(mockOfficers.getItems(), mockOfficers.getActiveCount());
        assertFalse(result);
    }

    @ParameterizedTest
    @CsvSource({
            "true, comp9",
            "false, comp12",
            "false, limitied-partnership"
    })
    void validateThrowsExceptionOnCompanyWithZeroOfficersWhenPerformSingleOfficerCheck(boolean isMudBeforeActivationDate, String baselineOrTargetCompanyType) throws ServiceException {
        companyProfileApi.setType(baselineOrTargetCompanyType);
        OfficersApi officersApi = new OfficersApi();
        officersApi.setItems(Collections.emptyList());
        officersApi.setActiveCount(0L);
        when(officerService.getOfficers(COMPANY_NUMBER)).thenReturn(officersApi);
        var result = companySingleOfficerValidation.isOfficerDirector(mockOfficers.getItems(), mockOfficers.getActiveCount());
        var ex = assertThrows(EligibilityException.class, () -> companySingleOfficerValidation.validateAgainstMadeUpDate(companyProfileApi, getBeforeOrAfterMadeUpDate(isMudBeforeActivationDate)));

        assertEquals(EligibilityStatusCode.INVALID_COMPANY_APPOINTMENTS_INVALID_NUMBER_OF_OFFICERS,ex.getEligibilityStatusCode() );
        assertFalse(result);
    }

    private LocalDate getBeforeOrAfterMadeUpDate(boolean isMudBeforeActivationDate) {
        return isMudBeforeActivationDate ? mudBeforeActivationDate : mudAfterActivationDate;
    }
}
