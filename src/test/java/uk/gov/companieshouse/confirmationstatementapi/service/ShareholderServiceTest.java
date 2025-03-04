package uk.gov.companieshouse.confirmationstatementapi.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.confirmationstatementapi.client.OracleQueryClient;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.model.json.shareholder.ShareholderJson;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShareholderServiceTest {

    private static final String COMPANY_NUMBER = "12345678";

    @Mock
    private OracleQueryClient oracleQueryClient;

    @InjectMocks
    private ShareholderService shareholderService;

    @Test
    void testGetShareholderData() throws ServiceException {
        var shareholder1 = new ShareholderJson();
        shareholder1.setSurname("Smith");
        var shareholder2 = new ShareholderJson();
        shareholder2.setSurname("Bond");

        List<ShareholderJson> shareholder = Arrays.asList(shareholder1, shareholder2);

        when(oracleQueryClient.getShareholders(COMPANY_NUMBER)).thenReturn(shareholder);
        var shareholderData = shareholderService.getShareholders(COMPANY_NUMBER);

        assertNotNull(shareholderService.getShareholders(COMPANY_NUMBER));
        assertEquals("Smith", shareholderData.get(0).getSurname());
        assertEquals("Bond", shareholderData.get(1).getSurname());
        assertEquals(2, shareholderData.size());
    }

    @Test
    void getCompanyShareholdersCountTest() throws ServiceException {
        when(oracleQueryClient.getShareholderCount(COMPANY_NUMBER)).thenReturn(0);

        var response = shareholderService.getShareholderCount(COMPANY_NUMBER);

        assertEquals(0, response);
    }

}
