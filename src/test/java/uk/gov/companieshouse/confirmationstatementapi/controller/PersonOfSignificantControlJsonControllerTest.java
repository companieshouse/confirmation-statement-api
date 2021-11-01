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
import uk.gov.companieshouse.confirmationstatementapi.model.json.PersonOfSignificantControlJson;
import uk.gov.companieshouse.confirmationstatementapi.service.PscService;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonOfSignificantControlJsonControllerTest {

    private static final String TRANSACTION_ID = "GFEDCBA";
    private static final String ERIC_REQUEST_ID = "XaBcDeF12345";
    private static final String SUBMISSION_ID = "ABCDEFG";

    @Mock
    private Transaction transaction;

    @Mock
    private PscService pscService;

    @InjectMocks
    private PersonsOfSignificantControlController personsOfSignificantControlController;

    @Test
    void testGetPersonsOfSignificantControlOKResponse() throws ServiceException, SubmissionNotFoundException {
        var pscs = Arrays.asList(new PersonOfSignificantControlJson(), new PersonOfSignificantControlJson());
        when(pscService.getPSCsFromOracle(SUBMISSION_ID, transaction.getCompanyNumber())).thenReturn(pscs);
        var response = personsOfSignificantControlController.getPersonsOfSignificantControl(transaction, TRANSACTION_ID, SUBMISSION_ID, ERIC_REQUEST_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pscs, response.getBody());
    }

    @Test
    void testGetPersonsOfSignificantControlUnsafeCompNumber() throws ServiceException, SubmissionNotFoundException {
        when(transaction.getCompanyNumber()).thenReturn("\n\r\t12345678");
        var pscs = Arrays.asList(new PersonOfSignificantControlJson(), new PersonOfSignificantControlJson());
        when(pscService.getPSCsFromOracle(SUBMISSION_ID, "___12345678")).thenReturn(pscs);
        var response = personsOfSignificantControlController.getPersonsOfSignificantControl(transaction, TRANSACTION_ID, SUBMISSION_ID, ERIC_REQUEST_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pscs, response.getBody());
    }

    @Test
    void testGetPersonsOfSignificantControlServiceException() throws ServiceException, SubmissionNotFoundException {
        when(pscService.getPSCsFromOracle(SUBMISSION_ID, transaction.getCompanyNumber())).thenThrow(new ServiceException("Message"));
        var response = personsOfSignificantControlController.getPersonsOfSignificantControl(transaction, TRANSACTION_ID, SUBMISSION_ID, ERIC_REQUEST_ID);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testGetPersonsOfSignificantControlUncheckedException() throws ServiceException, SubmissionNotFoundException {
        var runtimeException = new RuntimeException("Message");
        when(pscService.getPSCsFromOracle(SUBMISSION_ID, transaction.getCompanyNumber())).thenThrow(runtimeException);
        var thrown = assertThrows(Exception.class, () -> personsOfSignificantControlController.getPersonsOfSignificantControl(transaction, TRANSACTION_ID, SUBMISSION_ID, ERIC_REQUEST_ID));

        assertEquals(runtimeException, thrown);
    }

    @Test
    void testGetPersonsOfSignificantControlSubmissionNotFoundException() throws ServiceException, SubmissionNotFoundException {
        when(pscService.getPSCsFromOracle(SUBMISSION_ID, transaction.getCompanyNumber())).thenThrow(SubmissionNotFoundException.class);
        var response = personsOfSignificantControlController.getPersonsOfSignificantControl(transaction, TRANSACTION_ID, SUBMISSION_ID, ERIC_REQUEST_ID);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
