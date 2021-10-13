package uk.gov.companieshouse.confirmationstatementapi.controller;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.model.json.PersonOfSignificantControlJson;
import uk.gov.companieshouse.confirmationstatementapi.service.PscService;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonOfSignificantControlJsonControllerTest {

    private static final String COMPANY_NUMBER = "12777531";
    private static final String ERIC_REQUEST_ID = "XaBcDeF12345";

    @Mock
    private PscService pscService;

    @InjectMocks
    private PersonsOfSignificantControlController personsOfSignificantControlController;

    @Test
    void testGetPersonsOfSignificantControlOKResponse() throws ServiceException {
        var pscs = Arrays.asList(new PersonOfSignificantControlJson(), new PersonOfSignificantControlJson());
        when(pscService.getPSCsFromOracle(COMPANY_NUMBER)).thenReturn(pscs);
        var response = personsOfSignificantControlController.getPersonsOfSignificantControl(COMPANY_NUMBER, ERIC_REQUEST_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pscs, response.getBody());
    }

    @Test
    void testGetPersonsOfSignificantControlServiceException() throws ServiceException {
        when(pscService.getPSCsFromOracle(COMPANY_NUMBER)).thenThrow(new ServiceException("Message"));
        var response = personsOfSignificantControlController.getPersonsOfSignificantControl(COMPANY_NUMBER, ERIC_REQUEST_ID);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testGetPersonsOfSignificantControlUncheckedException() throws ServiceException {
        var runtimeException = new RuntimeException("Message");
        when(pscService.getPSCsFromOracle(COMPANY_NUMBER)).thenThrow(runtimeException);
        var thrown = assertThrows(Exception.class, () -> personsOfSignificantControlController.getPersonsOfSignificantControl(COMPANY_NUMBER, ERIC_REQUEST_ID));

        assertEquals(runtimeException, thrown);
    }
}
