package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;

class CompanyTypeCS01FilingNotRequiredValidationTest {
    
    private static final String NOT_REQUIRED_TYPE = "Not-required-type";
    private static final Set<String> NOT_REQUIRED_LIST = Collections.singleton(NOT_REQUIRED_TYPE);

    private CompanyTypeCS01FilingNotRequiredValidation companyTypeValidation;

    @BeforeEach
    void init() {
        companyTypeValidation = new CompanyTypeCS01FilingNotRequiredValidation(NOT_REQUIRED_LIST);
    }

    @Test
    void validateDoesNotThrowOnARequiredType() {
        CompanyProfileApi companyProfileApi = new CompanyProfileApi();
        companyProfileApi.setType("required-type");

        assertDoesNotThrow(() -> companyTypeValidation.validate(companyProfileApi));
    }

    @Test
    void validateThrowsOnANotRequiredType() {
        CompanyProfileApi companyProfileApi = new CompanyProfileApi();
        companyProfileApi.setType(NOT_REQUIRED_TYPE);

        var ex = assertThrows(EligibilityException.class, () ->
            companyTypeValidation.validate(companyProfileApi));

        assertEquals(EligibilityStatusCode.INVALID_COMPANY_TYPE_CS01_FILING_NOT_REQUIRED, ex.getEligibilityStatusCode());
    }

    @Test
    void validateDoesNotThrowOnNullType() throws EligibilityException {
        CompanyProfileApi companyProfileApi = new CompanyProfileApi();
        companyProfileApi.setType(null);

        assertDoesNotThrow(() -> companyTypeValidation.validate(companyProfileApi));
    }
}
