package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.psc.PscsApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.service.PscService;

import java.time.LocalDate;
import java.util.Set;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class CompanyMultiplePscCountValidationTest {

    private static final String MUD_BEFORE_ACTIVATION_DATE = "2025-07-01";
    private static final String MUD_AFTER_ACTIVATION_DATE = "2026-07-01";
    private static final String NOW_DATE = "2026-05-01";

    private final Supplier<LocalDate> supplyNowDate =
            () -> LocalDate.parse(NOW_DATE);

    private static final Set<String> MULTIPLE_COMPANY_TYPES_BASELINE = Set.of("comp1", "comp2", "comp3");
    private static final Set<String> MULTIPLE_COMPANY_TYPES_TARGET = Set.of("comp4", "comp5", "comp6");
    private static final String MULTIPLE_COMPANY_TYPES_ACTIVATION_DATE = "2026-03-01";

    private static final String COMPANY_NUMBER = "12345678";

    @Mock
    private PscService pscService;

    private CompanyMultiplePscCountValidation companyMultiplePscCountValidation;
    private CompanyProfileApi companyProfileApi;
    private PscsApi pscsApi;

    private LocalDate multipleTargetActivationDate;
    private LocalDate mudBeforeActivationDate;
    private LocalDate mudAfterActivationDate;

    @BeforeEach
    void init() {
        multipleTargetActivationDate = LocalDate.parse(MULTIPLE_COMPANY_TYPES_ACTIVATION_DATE);
        mudBeforeActivationDate = LocalDate.parse(MUD_BEFORE_ACTIVATION_DATE);
        mudAfterActivationDate = LocalDate.parse(MUD_AFTER_ACTIVATION_DATE);

        companyProfileApi = new CompanyProfileApi();
        companyProfileApi.setCompanyNumber(COMPANY_NUMBER);

        pscsApi = new PscsApi();

        companyMultiplePscCountValidation =
                new CompanyMultiplePscCountValidation(
                        pscService,
                        MULTIPLE_COMPANY_TYPES_BASELINE,
                        MULTIPLE_COMPANY_TYPES_TARGET,
                        multipleTargetActivationDate,
                        supplyNowDate
                );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(pscService);
    }

    @ParameterizedTest
    @CsvSource({
            "true, comp1",
            "false, comp4"
    })
    void validateDoesNotThrowExceptionOnOneToFivePscs(boolean isMudBeforeActivationDate,
                                                      String baselineOrTargetCompanyType)
            throws ServiceException {

        companyProfileApi.setType(baselineOrTargetCompanyType);
        pscsApi.setActiveCount(5L);

        when(pscService.getPSCsFromCHS(COMPANY_NUMBER)).thenReturn(pscsApi);

        assertDoesNotThrow(() ->
                companyMultiplePscCountValidation.validateAgainstMadeUpDate(
                        companyProfileApi,
                        getBeforeOrAfterMadeUpDate(isMudBeforeActivationDate)
                )
        );
    }

    @ParameterizedTest
    @CsvSource({
            "true, comp2",
            "false, comp5"
    })
    void validateThrowsExceptionOnZeroPscs(boolean isMudBeforeActivationDate,
                                           String baselineOrTargetCompanyType)
            throws ServiceException {

        companyProfileApi.setType(baselineOrTargetCompanyType);
        pscsApi.setActiveCount(0L);

        when(pscService.getPSCsFromCHS(COMPANY_NUMBER)).thenReturn(pscsApi);

        var ex = assertThrows(
                EligibilityException.class,
                () -> companyMultiplePscCountValidation.validateAgainstMadeUpDate(
                        companyProfileApi,
                        getBeforeOrAfterMadeUpDate(isMudBeforeActivationDate)
                )
        );

        assertEquals(
                EligibilityStatusCode.INVALID_COMPANY_APPOINTMENTS_MORE_THAN_FIVE_PSCS,
                ex.getEligibilityStatusCode()
        );
    }

    @ParameterizedTest
    @CsvSource({
            "true, comp3",
            "false, comp6"
    })
    void validateThrowsExceptionOnMoreThanFivePscs(boolean isMudBeforeActivationDate,
                                                   String baselineOrTargetCompanyType)
            throws ServiceException {

        companyProfileApi.setType(baselineOrTargetCompanyType);
        pscsApi.setActiveCount(6L);

        when(pscService.getPSCsFromCHS(COMPANY_NUMBER)).thenReturn(pscsApi);

        var ex = assertThrows(
                EligibilityException.class,
                () -> companyMultiplePscCountValidation.validateAgainstMadeUpDate(
                        companyProfileApi,
                        getBeforeOrAfterMadeUpDate(isMudBeforeActivationDate)
                )
        );

        assertEquals(
                EligibilityStatusCode.INVALID_COMPANY_APPOINTMENTS_MORE_THAN_FIVE_PSCS,
                ex.getEligibilityStatusCode()
        );
    }

    @ParameterizedTest
    @CsvSource({
            "true, comp2",
            "false, comp5"
    })
    void validateThrowsExceptionOnNullPscCount(boolean isMudBeforeActivationDate,
                                               String baselineOrTargetCompanyType)
            throws ServiceException {

        companyProfileApi.setType(baselineOrTargetCompanyType);
        pscsApi.setActiveCount(null);

        when(pscService.getPSCsFromCHS(COMPANY_NUMBER)).thenReturn(pscsApi);

        var ex = assertThrows(
                EligibilityException.class,
                () -> companyMultiplePscCountValidation.validateAgainstMadeUpDate(
                        companyProfileApi,
                        getBeforeOrAfterMadeUpDate(isMudBeforeActivationDate)
                )
        );

        assertEquals(
                EligibilityStatusCode.INVALID_COMPANY_APPOINTMENTS_MORE_THAN_FIVE_PSCS,
                ex.getEligibilityStatusCode()
        );
    }

    private LocalDate getBeforeOrAfterMadeUpDate(boolean isMudBeforeActivationDate) {
        return isMudBeforeActivationDate ? mudBeforeActivationDate : mudAfterActivationDate;
    }
}
