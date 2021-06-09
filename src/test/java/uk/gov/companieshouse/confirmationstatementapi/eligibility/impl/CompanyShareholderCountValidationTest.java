package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Description;

import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.client.OracleQueryClient;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.service.ShareholderService;

@ExtendWith(MockitoExtension.class)
class CompanyShareholderCountValidationTest {

    private static final String COMPANY_NUMBER = "12345678";

    private static String LTD_BY_GUARANTEE = "test-private-limited-guarant-nsc";
    private static String NOT_LTD_BY_GUARANTEE = "test-not-limited-guarant-nsc";

    @Mock
    private CompanyProfileApi companyProfile;

    @Mock
    private ShareholderService shareholderService;

    private CompanyShareholderCountValidation validation;

    @BeforeEach
    void beforeEach() {
        MockitoAnnotations.initMocks(this);
        validation = new CompanyShareholderCountValidation(shareholderService);
        when(companyProfile.getCompanyNumber()).thenReturn(COMPANY_NUMBER);

    }

    @Test
    @Description("Should not throw exception on company with no shareholders")
    void validateDoesNotThrowOnZeroShareholders() throws EligibilityException {
        when(companyProfile.getType()).thenReturn(NOT_LTD_BY_GUARANTEE);
        when(shareholderService.getShareholderCount(COMPANY_NUMBER)).thenReturn(0);

        assertDoesNotThrow(() -> validation.validate(companyProfile));
    }

    @Test
    @Description("Should not throw exception on company with a single shareholder")
    void validateDoesNotThrowOnSingleShareholders() {
        when(companyProfile.getType()).thenReturn(NOT_LTD_BY_GUARANTEE);
        when(shareholderService.getShareholderCount(COMPANY_NUMBER)).thenReturn(1);

        assertDoesNotThrow(() -> validation.validate(companyProfile));
    }

    @Test
    @Description("Should throw exception on company with multiple shareholders")
    void validateThrowsOnMultipleShareholders() {
        when(companyProfile.getType()).thenReturn(NOT_LTD_BY_GUARANTEE);
        when(shareholderService.getShareholderCount(COMPANY_NUMBER)).thenReturn(2);

        var ex = assertThrows(EligibilityException.class, () -> validation.validate(companyProfile));

        assertEquals(EligibilityStatusCode.INVALID_COMPANY_APPOINTMENTS_MORE_THAN_ONE_SHAREHOLDER,
                ex.getEligibilityStatusCode());
    }

    @Test
    @Description("Should not check shareholder count for limited-by-gurantee company")
    void validateDoesNotThrowOnMultipleShareholdersForLimitedByGuaranteeCompanyTest() throws EligibilityException {
        when(companyProfile.getType()).thenReturn(LTD_BY_GUARANTEE);
        validation.validate(companyProfile);

        verify(shareholderService, times(0)).getShareholderCount(COMPANY_NUMBER);
    }
}
