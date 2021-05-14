package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityFailureReason;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CompanyTypeValidationForWebFilingTest {

    private static final String WEB_FILING_TYPE = "WebFilingType";
    private static final Set<String> WEB_FILING_LIST = Collections.singleton(WEB_FILING_TYPE);

    private CompanyTypeValidationForWebFiling CompanyTypeValidationForWebFiling;

    @BeforeEach
    void init() {
        CompanyTypeValidationForWebFiling = new CompanyTypeValidationForWebFiling(WEB_FILING_LIST);
    }

    @Test
    void validateThrowsOnWebFilingType() {
        CompanyProfileApi companyProfileApi = new CompanyProfileApi();
        companyProfileApi.setType(WEB_FILING_TYPE);

        var ex = assertThrows(EligibilityException.class, () -> {
            CompanyTypeValidationForWebFiling.validate(companyProfileApi);
        });

        assertEquals(EligibilityFailureReason.INVALID_COMPANY_TYPE_USE_WEB_FILING, ex.getEligibilityFailureReason());
    }

    @Test
    void validateDoesNotThrowOnNotWebFilingType() throws EligibilityException {
        CompanyProfileApi companyProfileApi = new CompanyProfileApi();
        companyProfileApi.setType("Not_Web_Filing_Type");

        CompanyTypeValidationForWebFiling.validate(companyProfileApi);
    }

    @Test
    void validateThrowsOnNullStatus() throws EligibilityException {
        CompanyProfileApi companyProfileApi = new CompanyProfileApi();
        companyProfileApi.setType(null);

        CompanyTypeValidationForWebFiling.validate(companyProfileApi);
    }
}
