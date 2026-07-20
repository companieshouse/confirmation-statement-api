package uk.gov.companieshouse.confirmationstatementapi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.confirmationstatementapi.exception.CompanyNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CostServiceTest {

    private static final String COMPANY_NUMBER = "12345678";

    @Mock
    private CompanyProfileService companyProfileService;

    @Mock
    private Transaction transaction;

    @InjectMocks
    private CostService costService;

    @BeforeEach
    void init() {
        when(transaction.getCompanyNumber()).thenReturn(COMPANY_NUMBER);
        ReflectionTestUtils.setField(costService, "costAmount", "50.00");
    }

    @ParameterizedTest
    @CsvSource({
        "ltd, null, confirmation-statement",
        "limited-partnership, lp, lp-confirmation-statement",
        "limited-partnership, pflp, lp-confirmation-statement",
        "limited-partnership, slp, slp-confirmation-statement",
        "limited-partnership, spflp, slp-confirmation-statement",
        "limited-partnership, null, confirmation-statement",
    })
    void testGetCostsWithDifferentProductType(String companyType, String CompanySubtype, String expectedProductType) throws ServiceException, CompanyNotFoundException {
        CompanyProfileApi nonLimitedPartnershipCompany = getTestCompanyProfileApi(companyType, CompanySubtype);
        when(companyProfileService.getCompanyProfile(COMPANY_NUMBER)).thenReturn(nonLimitedPartnershipCompany);

        var result = costService.getCosts(transaction);
        assertEquals("50.00", result.getAmount());
        assertEquals(Collections.singletonList("credit-card"), result.getAvailablePaymentMethods());
        assertEquals(Collections.singletonList("data-maintenance"), result.getClassOfPayment());
        assertEquals(expectedProductType, result.getProductType());
    }

    private CompanyProfileApi getTestCompanyProfileApi(String companyType, String subtype) {
        CompanyProfileApi companyProfileApi = new CompanyProfileApi();

        companyProfileApi.setCompanyNumber(COMPANY_NUMBER);
        companyProfileApi.setType(companyType);
        companyProfileApi.setSubtype(subtype);

        return companyProfileApi;
    }
}