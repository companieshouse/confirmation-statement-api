package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CompanyTypeValidationPaperOnlyTest {

    private static final String PAPER_TYPE = "PaperType";
    private static final Set<String> PAPER_ONLY_LIST = Collections.singleton(PAPER_TYPE);

    private CompanyTypeValidationPaperOnly companyTypeValidationPaperOnly;

    @BeforeEach
    void init() {
        companyTypeValidationPaperOnly = new CompanyTypeValidationPaperOnly(PAPER_ONLY_LIST);
    }

    @Test
    void validateThrowsOnPaperType() {
        CompanyProfileApi companyProfileApi = new CompanyProfileApi();
        companyProfileApi.setType(PAPER_TYPE);

        var ex = assertThrows(EligibilityException.class, () ->
            companyTypeValidationPaperOnly.validate(companyProfileApi));

        assertEquals(EligibilityStatusCode.INVALID_COMPANY_TYPE_PAPER_FILING_ONLY, ex.getEligibilityStatusCode());
    }

    @Test
    void validateDoesNotThrowOnNonPaperType() {
        CompanyProfileApi companyProfileApi = new CompanyProfileApi();
        companyProfileApi.setType("Non_Paper_Type");

        assertDoesNotThrow(() -> companyTypeValidationPaperOnly.validate(companyProfileApi));
    }

    @Test
    void validateDoesNotThrowOnNullType() {
        CompanyProfileApi companyProfileApi = new CompanyProfileApi();
        companyProfileApi.setType(null);

        assertDoesNotThrow(() -> companyTypeValidationPaperOnly.validate(companyProfileApi));
    }
}
