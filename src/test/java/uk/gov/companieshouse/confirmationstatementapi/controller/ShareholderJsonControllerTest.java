package uk.gov.companieshouse.confirmationstatementapi.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.model.json.shareholder.ShareholderJson;
import uk.gov.companieshouse.confirmationstatementapi.service.ShareholderService;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShareholderJsonControllerTest {

    private static final String COMPANY_NUMBER = "12345678";
    private static final String ERICK_REQUEST_ID = "XaBcDeF12345";

    @Mock
    private ShareholderService shareholderService;

    @InjectMocks
    private ShareholderController shareholderController;

    @Test
    void testGetShareholderOKResponse() throws ServiceException {
        var shareholder = Arrays.asList(new ShareholderJson(), new ShareholderJson());
        when(shareholderService.getShareholders(COMPANY_NUMBER)).thenReturn(shareholder);
        var response = shareholderController.getShareholders(COMPANY_NUMBER, ERICK_REQUEST_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(shareholder, response.getBody());
    }

    @Test
    void testGetShareholderServiceException() throws ServiceException {
        when(shareholderService.getShareholders(COMPANY_NUMBER)).thenThrow(new ServiceException("Internal Server Error"));
        var response = shareholderController.getShareholders(COMPANY_NUMBER, ERICK_REQUEST_ID);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testGetShareholderUncheckedException() throws ServiceException {
        var runtimeException = new RuntimeException("Runtime Error");
        when(shareholderService.getShareholders(COMPANY_NUMBER)).thenThrow(runtimeException);
        var thrown = assertThrows(Exception.class, () -> shareholderController.getShareholders(COMPANY_NUMBER, ERICK_REQUEST_ID));

        assertEquals(runtimeException, thrown);
    }
}
