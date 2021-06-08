package uk.gov.companieshouse.confirmationstatementapi.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.confirmationstatementapi.client.OracleQueryClient;
import uk.gov.companieshouse.confirmationstatementapi.model.CompanyTradedStatusType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CorporateBodyServiceTest {

    private static final String COMPANY_NUMBER = "12345678";
    @Mock
    private OracleQueryClient oracleQueryClient;

    @InjectMocks
    private CorporateBodyService corporateBodyService;

    @Test
    void getCompanyTradedStatus() {
        when(oracleQueryClient.getCompanyTradedStatus(COMPANY_NUMBER)).thenReturn(0L);
        var companyTradedStatusType = corporateBodyService.getCompanyTradedStatus(COMPANY_NUMBER);

        assertEquals(CompanyTradedStatusType.NOT_ADMITTED_TO_TRADING, companyTradedStatusType);
    }
}
