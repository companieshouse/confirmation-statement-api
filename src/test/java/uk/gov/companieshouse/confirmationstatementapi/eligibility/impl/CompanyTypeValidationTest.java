package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityFailureReason;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;

public class CompanyTypeValidationTest {
    
    private static final String ALLOWED_TYPE = "AllowedType";
    private static final Set<String> ALLOWED_LIST = Collections.singleton(ALLOWED_TYPE);

    private CompanyTypeValidation companyTypeValidation;

    @BeforeEach
    void init() {
        companyTypeValidation = new CompanyTypeValidation(ALLOWED_LIST);
    }

    @Test
    void validateDoesNotThrowOnAllowedType() throws EligibilityException {
        CompanyProfileApi companyProfileApi = new CompanyProfileApi();
        companyProfileApi.setType(ALLOWED_TYPE);

        companyTypeValidation.validate(companyProfileApi);
    }

    @Test
    void validateThrowsOnDisallowedType() {
        CompanyProfileApi companyProfileApi = new CompanyProfileApi();
        companyProfileApi.setType("Disallowed_Type");

        var ex = assertThrows(EligibilityException.class, () -> {
            companyTypeValidation.validate(companyProfileApi);
        });

        assertEquals(EligibilityFailureReason.INVALID_COMPANY_TYPE, ex.getEligibilityFailureReason());
    }

    @Test
    void validateThrowsOnNullType() {
        CompanyProfileApi companyProfileApi = new CompanyProfileApi();
        companyProfileApi.setType(null);

        var ex = assertThrows(EligibilityException.class, () -> {
            companyTypeValidation.validate(companyProfileApi);
        });

        assertEquals(EligibilityFailureReason.INVALID_COMPANY_TYPE, ex.getEligibilityFailureReason());
    }
}
