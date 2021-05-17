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

class CompanyTypeValidationPaperOnlyTest {

    private static final String PAPER_TYPE = "PaperType";
    private static final Set<String> PAPER_ONLY_LIST = Collections.singleton(PAPER_TYPE);

    private CompanyTypeValidationPaperOnly CompanyTypeValidationPaperOnly;

    @BeforeEach
    void init() {
        CompanyTypeValidationPaperOnly = new CompanyTypeValidationPaperOnly(PAPER_ONLY_LIST);
    }

    @Test
    void validateThrowsOnPaperType() {
        CompanyProfileApi companyProfileApi = new CompanyProfileApi();
        companyProfileApi.setType(PAPER_TYPE);

        var ex = assertThrows(EligibilityException.class, () -> {
            CompanyTypeValidationPaperOnly.validate(companyProfileApi);
        });

        assertEquals(EligibilityFailureReason.INVALID_COMPANY_TYPE_PAPER_FILING_ONLY, ex.getEligibilityFailureReason());
    }

    @Test
    void validateDoesNotThrowOnNonPaperType() throws EligibilityException {
        CompanyProfileApi companyProfileApi = new CompanyProfileApi();
        companyProfileApi.setType("Non_Paper_Type");

        CompanyTypeValidationPaperOnly.validate(companyProfileApi);
    }

    @Test
    void validateThrowsOnNullStatus() throws EligibilityException {
        CompanyProfileApi companyProfileApi = new CompanyProfileApi();
        companyProfileApi.setType(null);

        CompanyTypeValidationPaperOnly.validate(companyProfileApi);
    }
}
