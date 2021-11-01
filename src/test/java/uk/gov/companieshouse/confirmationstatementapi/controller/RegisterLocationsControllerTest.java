package uk.gov.companieshouse.confirmationstatementapi.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.exception.SubmissionNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.model.json.registerlocation.RegisterLocationJson;
import uk.gov.companieshouse.confirmationstatementapi.service.RegisterLocationService;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterLocationsControllerTest {

    private static final String TRANSACTION_ID = "GFEDCBA";
    private static final String ERIC_REQUEST_ID = "XaBcDeF12345";
    private static final String SUBMISSION_ID = "ABCDEFG";

    @Mock
    private Transaction transaction;

    @Mock
    private RegisterLocationService regLocService;

    @InjectMocks
    private RegisterLocationsController regLocController;

    @Test
    void testGetRegisterLocationsOKResponse() throws ServiceException, SubmissionNotFoundException {
        var registerLocations = Arrays.asList(new RegisterLocationJson(), new RegisterLocationJson());
        when(regLocService.getRegisterLocations(SUBMISSION_ID, transaction.getCompanyNumber())).thenReturn(registerLocations);
        var response = regLocController.getRegisterLocations(transaction, TRANSACTION_ID, SUBMISSION_ID, ERIC_REQUEST_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(registerLocations, response.getBody());
    }

    @Test
    void testGetRegisterLocationsServiceException() throws ServiceException, SubmissionNotFoundException {
        when(regLocService.getRegisterLocations(SUBMISSION_ID, transaction.getCompanyNumber())).thenThrow(new ServiceException("Internal Server Error"));
        var response = regLocController.getRegisterLocations(transaction, TRANSACTION_ID, SUBMISSION_ID, ERIC_REQUEST_ID);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testGetRegisterLocationsUncheckedException() throws ServiceException, SubmissionNotFoundException {
        var runtimeException = new RuntimeException("Runtime Error");
        when(regLocService.getRegisterLocations(SUBMISSION_ID, transaction.getCompanyNumber())).thenThrow(runtimeException);
        var thrown = assertThrows(Exception.class, () -> regLocController.getRegisterLocations(transaction, TRANSACTION_ID, SUBMISSION_ID, ERIC_REQUEST_ID));

        assertEquals(runtimeException, thrown);
    }

    @Test
    void testGetShareholderSubmissionNotFoundException() throws ServiceException, SubmissionNotFoundException {
        when(regLocService.getRegisterLocations(SUBMISSION_ID, transaction.getCompanyNumber())).thenThrow(SubmissionNotFoundException.class);
        var response = regLocController.getRegisterLocations(transaction, TRANSACTION_ID, SUBMISSION_ID, ERIC_REQUEST_ID);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
