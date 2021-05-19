package uk.gov.companieshouse.confirmationstatementapi.checks;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.api.model.psc.PscsApi;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.service.PscService;

@ExtendWith(MockitoExtension.class)
public class CompanyPscCheckTest {

    private static final String COMPANY_NUMBER = "12345678";
    
    @Mock
    private PscService pscService;

    private PscsApi pscsApi;
    private CompanyPscCheck check;
    
    @BeforeEach
    void setUp() {
        check = new CompanyPscCheck(pscService);
        pscsApi = new PscsApi();
    }
    
    @Test
    void checkForMultiplePscsTest() throws ServiceException {
        pscsApi.setActiveCount((long) 3);
        when(pscService.getPscs(any())).thenReturn(pscsApi);

        var response = check.hasMultipleActivePscs(COMPANY_NUMBER);

        assertTrue(response);
    }

    @Test
    void checkForSinglePscTest() throws ServiceException {
        pscsApi.setActiveCount((long) 1);
        when(pscService.getPscs(any())).thenReturn(pscsApi);

        var response = check.hasMultipleActivePscs(COMPANY_NUMBER);

        assertFalse(response);
    }
    @Test
    void checkForZeroPscTest() throws ServiceException {
        pscsApi.setActiveCount((long) 0);
        when(pscService.getPscs(any())).thenReturn(pscsApi);

        var response = check.hasMultipleActivePscs(COMPANY_NUMBER);

        assertFalse(response);
    }

    @Test
    void checkForNullPscValueTest() throws ServiceException {
        pscsApi.setActiveCount(null);
        when(pscService.getPscs(any())).thenReturn(pscsApi);

        var response = check.hasMultipleActivePscs(COMPANY_NUMBER);

        assertFalse(response);
    }

    @Test
    void checkForPscThrowsServiceExceptionTest() throws ServiceException {
        pscsApi.setActiveCount((long) 0);
        when(pscService.getPscs(any())).thenThrow(new ServiceException("ERROR", new IOException()));

        assertThrows(ServiceException.class, () -> {
            check.hasMultipleActivePscs(COMPANY_NUMBER);
        });
    }
}
