package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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

@ExtendWith(MockitoExtension.class)
class CompanyPscCountValidationTest {

    private CompanyPscCountValidation companyPscCountValidation;
    private CompanyProfileApi companyProfileApi;
    private PscsApi pscsApi;

    @Mock
    PscService pscService;

    @BeforeEach
    void setUp() throws ServiceException {
        pscsApi = new PscsApi();
        companyProfileApi = new CompanyProfileApi();
        when(pscService.getPscs(any())).thenReturn(pscsApi);

        companyPscCountValidation = new CompanyPscCountValidation(pscService, true);
    }

    @Test
    void validateDoesNotThrowOnSinglePSCTest() {
        pscsApi.setActiveCount(1L);

        assertDoesNotThrow(() -> companyPscCountValidation.validate(companyProfileApi));
    }

    @Test
    void validateDoesNotThrowOnZeroPSCTest() {
        pscsApi.setActiveCount(0L);

        assertDoesNotThrow(() -> companyPscCountValidation.validate(companyProfileApi));
    }

    @Test
    void validateDoesNotThrowOnNullPSCTest() {
        pscsApi.setActiveCount(null);
        assertDoesNotThrow(() -> companyPscCountValidation.validate(companyProfileApi));
    }

    @Test
    void validateThrowsOnMultiplePSCsTest() {
        pscsApi.setActiveCount(2L);

        var ex = assertThrows(EligibilityException.class, 
                () -> companyPscCountValidation.validate(companyProfileApi));

        assertEquals(EligibilityStatusCode.INVALID_COMPANY_APPOINTMENTS_MORE_THAN_ONE_PSC,
                ex.getEligibilityStatusCode());

    }

}
