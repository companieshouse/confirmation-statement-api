package uk.gov.companieshouse.confirmationstatementapi.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.model.json.registerlocation.RegisterLocationJson;
import uk.gov.companieshouse.confirmationstatementapi.service.RegisterLocationService;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterLocationsControllerTest {

    private static final String COMPANY_NUMBER = "12345678";
    private static final String ERIC_REQUEST_ID = "XaBcDeF12345";

    @Mock
    private RegisterLocationService regLocService;

    @InjectMocks
    private RegisterLocationsController regLocController;

    @Test
    void testGetRegisterLocationsOKResponse() throws ServiceException {
        var registerLocations = Arrays.asList(new RegisterLocationJson(), new RegisterLocationJson());
        when(regLocService.getRegisterLocations(COMPANY_NUMBER)).thenReturn(registerLocations);
        var response = regLocController.getRegisterLocations(COMPANY_NUMBER, ERIC_REQUEST_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(registerLocations, response.getBody());
    }

    @Test
    void testGetRegisterLocationsServiceException() throws ServiceException {
        when(regLocService.getRegisterLocations(COMPANY_NUMBER)).thenThrow(new ServiceException("Internal Server Error"));
        var response = regLocController.getRegisterLocations(COMPANY_NUMBER, ERIC_REQUEST_ID);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testGetRegisterLocationsUncheckedException() throws ServiceException {
        var runtimeException = new RuntimeException("Runtime Error");
        when(regLocService.getRegisterLocations(COMPANY_NUMBER)).thenThrow(runtimeException);
        var thrown = assertThrows(Exception.class, () -> regLocController.getRegisterLocations(COMPANY_NUMBER, ERIC_REQUEST_ID));

        assertEquals(runtimeException, thrown);
    }
}
