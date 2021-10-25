package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyPscCountValidationTest {

    private CompanyPscCountValidation companyPscCountValidation;
    private CompanyProfileApi companyProfileApi;
    private PscsApi pscsApi;

    @Mock
    PscService pscService;

    @BeforeEach
    void setUp() {
        pscsApi = new PscsApi();
        companyProfileApi = new CompanyProfileApi();
    }

    @Test
    void validateDoesNotThrowOnSinglePSCTest() throws ServiceException {
        when(pscService.getPSCsFromCHS(any())).thenReturn(pscsApi);
        companyPscCountValidation = new CompanyPscCountValidation(pscService, true, false);
        pscsApi.setActiveCount(1L);

        assertDoesNotThrow(() -> companyPscCountValidation.validate(companyProfileApi));
    }

    @Test
    void validateDoesNotThrowOnZeroPSCTest() throws ServiceException {
        when(pscService.getPSCsFromCHS(any())).thenReturn(pscsApi);
        companyPscCountValidation = new CompanyPscCountValidation(pscService, true, false);

        pscsApi.setActiveCount(0L);

        assertDoesNotThrow(() -> companyPscCountValidation.validate(companyProfileApi));
    }

    @Test
    void validateDoesNotThrowOnNullPSCTest() throws ServiceException {
        when(pscService.getPSCsFromCHS(any())).thenReturn(pscsApi);
        companyPscCountValidation = new CompanyPscCountValidation(pscService, true, false);

        pscsApi.setActiveCount(null);
        assertDoesNotThrow(() -> companyPscCountValidation.validate(companyProfileApi));
    }

    @Test
    void validateThrowsOnMultiplePSCsTest() throws ServiceException {
        when(pscService.getPSCsFromCHS(any())).thenReturn(pscsApi);
        companyPscCountValidation = new CompanyPscCountValidation(pscService, true, false);

        pscsApi.setActiveCount(2L);

        var ex = assertThrows(EligibilityException.class, 
                () -> companyPscCountValidation.validate(companyProfileApi));

        assertEquals(EligibilityStatusCode.INVALID_COMPANY_APPOINTMENTS_MORE_THAN_ONE_PSC,
                ex.getEligibilityStatusCode());

    }

    @Test
    void validateThrowsOnMoreThanFivePSCsTestMultipleJourneyOn() throws ServiceException {
        when(pscService.getPSCsFromCHS(any())).thenReturn(pscsApi);
        companyPscCountValidation = new CompanyPscCountValidation(pscService, true, true);
        pscsApi.setActiveCount(6L);
        var ex = assertThrows(EligibilityException.class,
                () -> companyPscCountValidation.validate(companyProfileApi));
        assertEquals(EligibilityStatusCode.INVALID_COMPANY_APPOINTMENTS_MORE_THAN_FIVE_PSCS,
                ex.getEligibilityStatusCode());
    }

    @Test
    void validateDoesNotThrowOnFivePSCsTestMultipleJourneyOn() throws ServiceException {
        when(pscService.getPSCsFromCHS(any())).thenReturn(pscsApi);
        companyPscCountValidation = new CompanyPscCountValidation(pscService, true, true);
        pscsApi.setActiveCount(5L);
        assertDoesNotThrow(() -> companyPscCountValidation.validate(companyProfileApi));
    }

    @Test
    void validateDoesNotThrowOnNullPSCTestMultipleJourneyOn() throws ServiceException {
        when(pscService.getPSCsFromCHS(any())).thenReturn(pscsApi);
        companyPscCountValidation = new CompanyPscCountValidation(pscService, true, true);

        pscsApi.setActiveCount(null);
        assertDoesNotThrow(() -> companyPscCountValidation.validate(companyProfileApi));
    }

    @Test
    void validateDoesNotExecuteWhenFlagIsOff() throws ServiceException, EligibilityException {
        companyPscCountValidation = new CompanyPscCountValidation(pscService, false, false);

        companyPscCountValidation.validate(companyProfileApi);
        verifyNoInteractions(pscService);
    }

}
