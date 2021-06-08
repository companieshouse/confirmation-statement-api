package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.model.CompanyTradedStatusType;
import uk.gov.companieshouse.confirmationstatementapi.service.CorporateBodyService;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyTradedStatusValidationTest {

    private static final String COMPANY_NUMBER = "12345678";
    @Mock
    private CompanyProfileApi companyProfileApi;

    @Mock
    private CorporateBodyService corporateBodyService;

    private CompanyTradedStatusValidation companyTradedStatusValidation;

    @BeforeEach
    void beforeEach() {
        MockitoAnnotations.initMocks(this);
        companyTradedStatusValidation = new CompanyTradedStatusValidation(corporateBodyService);
    }

    @Test
    void validateDoesNotThrowOnValidTradedStatus() {
        when(companyProfileApi.getCompanyNumber()).thenReturn(COMPANY_NUMBER);
        when(corporateBodyService.getCompanyTradedStatus(COMPANY_NUMBER))
                .thenReturn(CompanyTradedStatusType.NOT_ADMITTED_TO_TRADING);
        assertDoesNotThrow(() -> companyTradedStatusValidation.validate(companyProfileApi));
    }

    @Test
    void validateDoesThrowOnInvalidTradedStatus() {
        when(companyProfileApi.getCompanyNumber()).thenReturn(COMPANY_NUMBER);
        when(corporateBodyService.getCompanyTradedStatus(COMPANY_NUMBER))
                .thenReturn(CompanyTradedStatusType.ADMITTED_TO_TRADING_AND_DTR5_APPLIED);

        var ex = assertThrows(EligibilityException.class,
                () -> companyTradedStatusValidation.validate(companyProfileApi));

        assertEquals(EligibilityStatusCode.INVALID_COMPANY_TRADED_STATUS_USE_WEBFILING,
                ex.getEligibilityStatusCode());
    }
}
