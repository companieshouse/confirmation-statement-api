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

    private static final String ALLOWED_VALUE = "AllowedStatus";
    private static final Set<String> ALLOWED_LIST = Collections.singleton(ALLOWED_VALUE);

    private CompanyTypeValidationForWebFiling CompanyTypeValidationForWebFiling;

    @BeforeEach
    void init() {
        CompanyTypeValidationForWebFiling = new CompanyTypeValidationForWebFiling(ALLOWED_LIST);
    }

    @Test
    void validateDoesNotThrowOnAllowedValue() throws EligibilityException {
        CompanyProfileApi companyProfileApi = new CompanyProfileApi();
        companyProfileApi.setType(ALLOWED_VALUE);

        CompanyTypeValidationForWebFiling.validate(companyProfileApi);
    }

    @Test
    void validateThrowsOnDisallowedValue() {
        CompanyProfileApi companyProfileApi = new CompanyProfileApi();
        companyProfileApi.setType("Disallowed_Value");

        var ex = assertThrows(EligibilityException.class, () -> {
            CompanyTypeValidationForWebFiling.validate(companyProfileApi);
        });

        assertEquals(EligibilityFailureReason.INVALID_COMPANY_TYPE_FOR_WEB_FILING, ex.getEligibilityFailureReason());
    }

    @Test
    void validateThrowsOnNullStatus() {
        CompanyProfileApi companyProfileApi = new CompanyProfileApi();
        companyProfileApi.setType(null);

        var ex = assertThrows(EligibilityException.class, () -> {
            CompanyTypeValidationForWebFiling.validate(companyProfileApi);
        });

        assertEquals(EligibilityFailureReason.INVALID_COMPANY_TYPE_FOR_WEB_FILING, ex.getEligibilityFailureReason());
    }
}