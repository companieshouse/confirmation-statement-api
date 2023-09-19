package uk.gov.companieshouse.confirmationstatementapi.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.service.EmailService;


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
    void getRegisteredEmailAddress() throws ServiceException {
        // GIVEN

        var companyNumber = "12345ABCDE";
        var registeredEmailAddress = "info@acme.com";

        // WHEN

        when(transaction.getCompanyNumber()).thenReturn(companyNumber);
        when(emailService.getRegisteredEmailAddress(companyNumber)).thenReturn(registeredEmailAddress);

        var response = emailController.getRegisteredEmailAddress(transaction, TRANSACTION_ID, ERIC_REQUEST_ID);

        // THEN

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(registeredEmailAddress, response.getBody());
    }

    @Test
    void getRegisteredEmailAddressServiceException() throws ServiceException {
        // GIVEN

        var companyNumber = "12345ABCDE";

        // WHEN

        when(transaction.getCompanyNumber()).thenReturn(companyNumber);
        when(emailService.getRegisteredEmailAddress(companyNumber)).thenThrow(ServiceException.class);

        var response = emailController.getRegisteredEmailAddress(transaction, TRANSACTION_ID, ERIC_REQUEST_ID);

        // THEN

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

}
