package uk.gov.companieshouse.confirmationstatementapi.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.company.RegisteredEmailAddressJson;
import uk.gov.companieshouse.confirmationstatementapi.client.OracleQueryClient;
import uk.gov.companieshouse.confirmationstatementapi.exception.RegisteredEmailNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    private static final String COMPANY_NUMBER = "12345ABCDE";

    @Mock
    private OracleQueryClient oracleQueryClient;

    @InjectMocks
    private EmailService emailService;

    @Test
    void getRegisteredEmailAddress() throws ServiceException, RegisteredEmailNotFoundException {

        // GIVEN

        var registeredEmailAddress = new RegisteredEmailAddressJson();
        registeredEmailAddress.setRegisteredEmailAddress("info@acme.com");

        // WHEN

        when(oracleQueryClient.getRegisteredEmailAddress(COMPANY_NUMBER)).thenReturn(registeredEmailAddress);

        // THEN

        assertEquals(registeredEmailAddress, emailService.getRegisteredEmailAddress(COMPANY_NUMBER));
    }

    @Test
    void getRegisteredEmailAddressServiceException() throws ServiceException, RegisteredEmailNotFoundException {

        // WHEN

        when(oracleQueryClient.getRegisteredEmailAddress(COMPANY_NUMBER)).thenThrow(ServiceException.class);

        // THEN

        assertThrows(ServiceException.class, () -> emailService.getRegisteredEmailAddress(COMPANY_NUMBER));
    }
}
