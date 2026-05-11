package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.psc.PscsApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.service.PscService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CompanyPscCountValidationTest {

    private static final String COMPANY_NUMBER = "12345678";
    private static final LocalDate MADE_UP_DATE = LocalDate.of(2026, 4, 1);

    @Mock
    private PscService pscService;

    @Mock
    private CompanyMultiplePscCountValidation multiplePscValidation;

    @Mock
    private CompanySinglePscCountValidation singlePscValidation;

    private CompanyPscCountValidation companyPscCountValidation;
    private CompanyProfileApi companyProfileApi;
    private PscsApi pscsApi;

    @BeforeEach
    void setUp() throws ServiceException {
        companyProfileApi = new CompanyProfileApi();
        companyProfileApi.setCompanyNumber(COMPANY_NUMBER);

        pscsApi = new PscsApi();
        pscsApi.setActiveCount(2L);

        when(pscService.getPSCsFromCHS(COMPANY_NUMBER)).thenReturn(pscsApi);

        companyPscCountValidation =
                new CompanyPscCountValidation(
                        pscService,
                        multiplePscValidation,
                        singlePscValidation
                );
    }

    @Test
    void delegatesToMultiplePscValidationWhenEligible() throws ServiceException, EligibilityException {

        when(multiplePscValidation.isEligibleForMultiplePscCheck(
                companyProfileApi, MADE_UP_DATE))
                .thenReturn(true);

        assertDoesNotThrow(() ->
                companyPscCountValidation.validateAgainstMadeUpDate(
                        companyProfileApi, MADE_UP_DATE));

        verify(multiplePscValidation)
                .validateAgainstMadeUpDate(companyProfileApi, MADE_UP_DATE);

        verify(singlePscValidation, never())
                .validateAgainstMadeUpDate(any(), any());
    }

    @Test
    void delegatesToSinglePscValidationWhenNotEligibleForMultiple() throws ServiceException, EligibilityException {

        when(multiplePscValidation.isEligibleForMultiplePscCheck(
                companyProfileApi, MADE_UP_DATE))
                .thenReturn(false);

        assertDoesNotThrow(() ->
                companyPscCountValidation.validateAgainstMadeUpDate(
                        companyProfileApi, MADE_UP_DATE));

        verify(singlePscValidation)
                .validateAgainstMadeUpDate(companyProfileApi, MADE_UP_DATE);

        verify(multiplePscValidation, never())
                .validateAgainstMadeUpDate(any(), any());
    }

    @Test
    void propagatesExceptionFromSinglePscValidation() throws ServiceException, EligibilityException {

        when(multiplePscValidation.isEligibleForMultiplePscCheck(
                companyProfileApi, MADE_UP_DATE))
                .thenReturn(false);

        doThrow(new EligibilityException(
                EligibilityStatusCode.INVALID_COMPANY_APPOINTMENTS_MORE_THAN_ONE_PSC))
                .when(singlePscValidation)
                .validateAgainstMadeUpDate(companyProfileApi, MADE_UP_DATE);

        var ex = assertThrows(
                EligibilityException.class,
                () -> companyPscCountValidation.validateAgainstMadeUpDate(
                        companyProfileApi, MADE_UP_DATE)
        );

        assertEquals(
                EligibilityStatusCode.INVALID_COMPANY_APPOINTMENTS_MORE_THAN_ONE_PSC,
                ex.getEligibilityStatusCode()
        );
    }

    @Test
    void propagatesExceptionFromMultiplePscValidation() throws ServiceException, EligibilityException {

        when(multiplePscValidation.isEligibleForMultiplePscCheck(
                companyProfileApi, MADE_UP_DATE))
                .thenReturn(true);

        doThrow(new EligibilityException(
                EligibilityStatusCode.INVALID_COMPANY_APPOINTMENTS_MORE_THAN_FIVE_PSCS))
                .when(multiplePscValidation)
                .validateAgainstMadeUpDate(companyProfileApi, MADE_UP_DATE);

        var ex = assertThrows(
                EligibilityException.class,
                () -> companyPscCountValidation.validateAgainstMadeUpDate(
                        companyProfileApi, MADE_UP_DATE)
        );

        assertEquals(
                EligibilityStatusCode.INVALID_COMPANY_APPOINTMENTS_MORE_THAN_FIVE_PSCS,
                ex.getEligibilityStatusCode()
        );
    }

    @AfterEach
    void verifyInteractions() throws ServiceException {
        verify(pscService).getPSCsFromCHS(COMPANY_NUMBER);
        verifyNoMoreInteractions(pscService, singlePscValidation, multiplePscValidation);
    }
}
