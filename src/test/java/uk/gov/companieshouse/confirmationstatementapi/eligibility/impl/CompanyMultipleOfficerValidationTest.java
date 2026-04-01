package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;


import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyMultipleOfficerValidationTest {

    private static final String MUD_BEFORE_ACTIVATION_DATE = "2025-07-01";
    private static final String MUD_AFTER_ACTIVATION_DATE = "2026-07-01";
    private static final String NOW_DATE = "2026-05-01";

    private final Supplier<LocalDate> supplyNowDate =
            () -> LocalDate.parse(NOW_DATE);

    private static final Set<String> MULTIPLE_COMPANY_TYPES_BASELINE = Set.of("comp1","comp2","comp3");
    private static final Set<String> MULTIPLE_COMPANY_TYPES_TARGET = Set.of("comp4","comp5","comp6");
    private static final String MULTIPLE_COMPANY_TYPES_ACTIVATION_DATE = "2026-03-01";

    private static final String COMPANY_NUMBER = "12345678";
    private static final List<CompanyOfficerApi> OFFICER_LIST = new ArrayList<>();

    OfficersApi mockOfficers = new OfficersApi();
    CompanyProfileApi companyProfileApi = new CompanyProfileApi();

    @Mock
    private OfficerService officerService;
    private CompanyMultipleOfficerValidation companyMultipleOfficerValidation;
    private LocalDate multipleTargetActivationDate;
    private LocalDate mudBeforeActivationDate;
    private LocalDate mudAfterActivationDate;

    @BeforeEach
    void init() {
        OFFICER_LIST.clear();
        multipleTargetActivationDate = LocalDate.parse(MULTIPLE_COMPANY_TYPES_ACTIVATION_DATE);
        mudBeforeActivationDate = LocalDate.parse(MUD_BEFORE_ACTIVATION_DATE);
        mudAfterActivationDate = LocalDate.parse(MUD_AFTER_ACTIVATION_DATE);
        CompanyOfficerApi mockOfficer = new CompanyOfficerApi();
        mockOfficer.setOfficerRole(OfficerRoleApi.DIRECTOR);
        OFFICER_LIST.add(mockOfficer);
        companyProfileApi.setCompanyNumber(COMPANY_NUMBER);

        companyMultipleOfficerValidation = new CompanyMultipleOfficerValidation(officerService,
                MULTIPLE_COMPANY_TYPES_BASELINE,
                MULTIPLE_COMPANY_TYPES_TARGET,
                multipleTargetActivationDate,
                supplyNowDate);
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(officerService);
    }

    @ParameterizedTest
    @CsvSource({
            "true, comp1",
            "false, comp4"
    })
    void validateDoesNotThrowExceptionOnFiveOrLessOfficersWhenPerformMultipleOfficerCheck(boolean isMudBeforeActivationDate, String baselineOrTargetCompanyType) throws ServiceException {
        companyProfileApi.setType(baselineOrTargetCompanyType);
        CompanyOfficerApi director = new CompanyOfficerApi();
        director.setOfficerRole(OfficerRoleApi.CORPORATE_DIRECTOR);
        OFFICER_LIST.add(director);
        mockOfficers.setItems(OFFICER_LIST);
        mockOfficers.setActiveCount(5L);

        when(officerService.getOfficers(COMPANY_NUMBER)).thenReturn(mockOfficers);

        assertDoesNotThrow(() -> companyMultipleOfficerValidation.validateAgainstMadeUpDate(companyProfileApi, getBeforeOrAfterMadeUpDate(isMudBeforeActivationDate)));
    }

    @ParameterizedTest
    @CsvSource({
            "true, comp2",
            "false, comp5"
    })
    void validateThrowsExceptionOnZeroOfficersWhenPerformMultipleOfficerCheck(boolean isMudBeforeActivationDate, String baselineOrTargetCompanyType) throws ServiceException {
        companyProfileApi.setType(baselineOrTargetCompanyType);
        CompanyOfficerApi director = new CompanyOfficerApi();
        director.setOfficerRole(OfficerRoleApi.CORPORATE_DIRECTOR);
        OFFICER_LIST.add(director);
        mockOfficers.setItems(OFFICER_LIST);
        mockOfficers.setActiveCount(0L);

        when(officerService.getOfficers(COMPANY_NUMBER)).thenReturn(mockOfficers);

        var ex = assertThrows(EligibilityException.class, () -> companyMultipleOfficerValidation.validateAgainstMadeUpDate(companyProfileApi, getBeforeOrAfterMadeUpDate(isMudBeforeActivationDate)));
        assertEquals(EligibilityStatusCode.INVALID_COMPANY_APPOINTMENTS_INVALID_NUMBER_OF_OFFICERS,ex.getEligibilityStatusCode() );
    }

    @ParameterizedTest
    @CsvSource({
            "true, comp3",
            "false, comp6"
    })
    void validateThrowsExceptionOnMoreThanFiveOfficersWhenPerformMultipleOfficerCheck(boolean isMudBeforeActivationDate, String baselineOrTargetCompanyType) throws ServiceException {
        companyProfileApi.setType(baselineOrTargetCompanyType);
        CompanyOfficerApi director = new CompanyOfficerApi();
        director.setOfficerRole(OfficerRoleApi.CORPORATE_DIRECTOR);
        OFFICER_LIST.add(director);
        mockOfficers.setItems(OFFICER_LIST);
        mockOfficers.setActiveCount(6L);

        when(officerService.getOfficers(COMPANY_NUMBER)).thenReturn(mockOfficers);

        var ex = assertThrows(EligibilityException.class, () -> companyMultipleOfficerValidation.validateAgainstMadeUpDate(companyProfileApi, getBeforeOrAfterMadeUpDate(isMudBeforeActivationDate)));

        assertEquals(EligibilityStatusCode.INVALID_COMPANY_APPOINTMENTS_INVALID_NUMBER_OF_OFFICERS,ex.getEligibilityStatusCode() );
    }

    @ParameterizedTest
    @CsvSource({
            "true, comp2",
            "false, comp5"
    })
    void validateThrowsExceptionOnCompanyWithNullOfficerListWhenPerformMultipleOfficerCheck(boolean isMudBeforeActivationDate, String baselineOrTargetCompanyType) throws ServiceException {
        companyProfileApi.setType(baselineOrTargetCompanyType);
        OfficersApi officersApi = new OfficersApi();
        officersApi.setItems(null);
        officersApi.setActiveCount(null);
        when(officerService.getOfficers(COMPANY_NUMBER)).thenReturn(officersApi);
        var ex = assertThrows(EligibilityException.class, () -> companyMultipleOfficerValidation.validateAgainstMadeUpDate(companyProfileApi, getBeforeOrAfterMadeUpDate(isMudBeforeActivationDate)));

        assertEquals(EligibilityStatusCode.INVALID_COMPANY_APPOINTMENTS_INVALID_NUMBER_OF_OFFICERS,ex.getEligibilityStatusCode() );
    }

    private LocalDate getBeforeOrAfterMadeUpDate(boolean isMudBeforeActivationDate) {
        return isMudBeforeActivationDate ? mudBeforeActivationDate : mudAfterActivationDate;
    }
}
