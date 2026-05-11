package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class CompanySinglePscCountValidationTest {

    private static final String MUD_BEFORE_ACTIVATION_DATE = "2025-07-01";
    private static final String MUD_AFTER_ACTIVATION_DATE = "2026-07-01";
    private static final String NOW_DATE = "2026-05-01";

    private final Supplier<LocalDate> supplyNowDate =
            () -> LocalDate.parse(NOW_DATE);

    private static final Set<String> SINGLE_COMPANY_TYPES_BASELINE =
            Set.of("comp7", "comp8", "comp9");

    private static final Set<String> SINGLE_COMPANY_TYPES_TARGET =
            Set.of("comp10", "comp11", "comp12", "limited-partnership");

    private static final String SINGLE_COMPANY_TYPES_ACTIVATION_DATE = "2026-01-01";
    private static final String COMPANY_NUMBER = "12345678";

    @Mock
    private PscService pscService;

    private CompanySinglePscCountValidation companySinglePscCountValidation;
    private CompanyProfileApi companyProfileApi;
    private PscsApi pscsApi;

    private LocalDate singleTargetActivationDate;
    private LocalDate mudBeforeActivationDate;
    private LocalDate mudAfterActivationDate;

    @BeforeEach
    void init() {
        singleTargetActivationDate = LocalDate.parse(SINGLE_COMPANY_TYPES_ACTIVATION_DATE);
        mudBeforeActivationDate = LocalDate.parse(MUD_BEFORE_ACTIVATION_DATE);
        mudAfterActivationDate = LocalDate.parse(MUD_AFTER_ACTIVATION_DATE);

        companyProfileApi = new CompanyProfileApi();
        companyProfileApi.setCompanyNumber(COMPANY_NUMBER);

        pscsApi = new PscsApi();

        companySinglePscCountValidation =
                new CompanySinglePscCountValidation(
                        pscService,
                        SINGLE_COMPANY_TYPES_BASELINE,
                        SINGLE_COMPANY_TYPES_TARGET,
                        singleTargetActivationDate,
                        supplyNowDate
                );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(pscService);
    }

    @ParameterizedTest
    @CsvSource({
            "true, comp7",
            "false, comp10",
            "false, limited-partnership"
    })
    void validateDoesNotThrowExceptionOnZeroOrOnePsc(boolean isMudBeforeActivationDate,
                                                     String baselineOrTargetCompanyType)
            throws ServiceException {

        companyProfileApi.setType(baselineOrTargetCompanyType);
        pscsApi.setActiveCount(1L);

        when(pscService.getPSCsFromCHS(COMPANY_NUMBER)).thenReturn(pscsApi);

        assertDoesNotThrow(() ->
                companySinglePscCountValidation.validateAgainstMadeUpDate(
                        companyProfileApi,
                        getBeforeOrAfterMadeUpDate(isMudBeforeActivationDate)
                )
        );
    }

    @ParameterizedTest
    @CsvSource({
            "true, comp8",
            "false, comp11",
            "false, limited-partnership"
    })
    void validateThrowsExceptionOnMoreThanOnePsc(boolean isMudBeforeActivationDate,
                                                 String baselineOrTargetCompanyType)
            throws ServiceException {

        companyProfileApi.setType(baselineOrTargetCompanyType);
        pscsApi.setActiveCount(2L);

        when(pscService.getPSCsFromCHS(COMPANY_NUMBER)).thenReturn(pscsApi);

        var ex = assertThrows(
                EligibilityException.class,
                () -> companySinglePscCountValidation.validateAgainstMadeUpDate(
                        companyProfileApi,
                        getBeforeOrAfterMadeUpDate(isMudBeforeActivationDate)
                )
        );

        assertEquals(
                EligibilityStatusCode.INVALID_COMPANY_APPOINTMENTS_MORE_THAN_ONE_PSC,
                ex.getEligibilityStatusCode()
        );
    }

    @Test
    void validateDoesNotThrowExceptionWhenPscCountIsNull() throws ServiceException {
        companyProfileApi.setType("comp7");
        pscsApi.setActiveCount(null);

        when(pscService.getPSCsFromCHS(COMPANY_NUMBER)).thenReturn(pscsApi);

        assertDoesNotThrow(() ->
                companySinglePscCountValidation.validateAgainstMadeUpDate(
                        companyProfileApi,
                        mudBeforeActivationDate
                )
        );
    }

    private LocalDate getBeforeOrAfterMadeUpDate(boolean isMudBeforeActivationDate) {
        return isMudBeforeActivationDate ? mudBeforeActivationDate : mudAfterActivationDate;
    }
}

