package uk.gov.companieshouse.confirmationstatementapi.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import uk.gov.companieshouse.api.model.company.RegisteredEmailAddressJson;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.confirmationstatementapi.exception.RegisteredEmailNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.service.EmailService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class EmailControllerTest {

    private static final String TRANSACTION_ID = "GFEDCBA";

    private static final String ERIC_REQUEST_ID = "XaBcDeF12345";

    @Mock
    private Transaction transaction;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private EmailController emailController;

    @Test
    void getRegisteredEmailAddress() throws ServiceException, RegisteredEmailNotFoundException {
        // GIVEN

        var companyNumber = "12345ABCDE";
        var registeredEmailAddress = new RegisteredEmailAddressJson();
        registeredEmailAddress.setRegisteredEmailAddress("info@acme.com");

        // WHEN

        when(transaction.getCompanyNumber()).thenReturn(companyNumber);
        when(emailService.getRegisteredEmailAddress(companyNumber)).thenReturn(registeredEmailAddress);

        var response = emailController.getRegisteredEmailAddress(transaction, TRANSACTION_ID, ERIC_REQUEST_ID);

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        RegisteredEmailAddressJson actual = (RegisteredEmailAddressJson) response.getBody();
        assertEquals(registeredEmailAddress.getRegisteredEmailAddress(), actual.getRegisteredEmailAddress());
    }

    @Test
    void getRegisteredEmailAddressServiceException() throws ServiceException, RegisteredEmailNotFoundException {
        // GIVEN

        var companyNumber = "12345ABCDE";

        // WHEN

        when(transaction.getCompanyNumber()).thenReturn(companyNumber);
        when(emailService.getRegisteredEmailAddress(companyNumber)).thenThrow(ServiceException.class);

        var response = emailController.getRegisteredEmailAddress(transaction, TRANSACTION_ID, ERIC_REQUEST_ID);

        // THEN

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void getRegisteredEmailAddressRegisteredEmailNotFoundException() throws ServiceException, RegisteredEmailNotFoundException {
        // GIVEN

        var companyNumber = "12345ABCDE";

        // WHEN

        when(transaction.getCompanyNumber()).thenReturn(companyNumber);
        when(emailService.getRegisteredEmailAddress(companyNumber)).thenThrow(RegisteredEmailNotFoundException.class);

        var response = emailController.getRegisteredEmailAddress(transaction, TRANSACTION_ID, ERIC_REQUEST_ID);

        // THEN

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
