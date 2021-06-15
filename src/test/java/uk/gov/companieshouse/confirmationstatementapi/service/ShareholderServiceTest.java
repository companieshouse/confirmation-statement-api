package uk.gov.companieshouse.confirmationstatementapi.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.confirmationstatementapi.client.OracleQueryClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShareholderServiceTest {

    private static final String COMPANY_NUMBER = "12345678";

    @Mock
    private OracleQueryClient oracleQueryClient;

    @InjectMocks
    private ShareholderService shareholderService;

    @Test
    void getCompanyShareholdersCountTest() {
        when(oracleQueryClient.getShareholderCount(COMPANY_NUMBER)).thenReturn(0);

        var response = shareholderService.getShareholderCount(COMPANY_NUMBER);

        assertEquals(0, response);
    }

}
