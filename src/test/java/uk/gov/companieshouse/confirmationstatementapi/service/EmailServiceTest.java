package uk.gov.companieshouse.confirmationstatementapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.confirmationstatementapi.client.OracleQueryClient;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    private static final String COMPANY_NUMBER = "12345ABCDE";

    @Mock
    private OracleQueryClient oracleQueryClient;

    @InjectMocks
    private EmailService emailService;

    @Test
    void getRegisteredEmailAddress() throws ServiceException {

        // GIVEN

        var registeredEmailAddress = "info@acme.com";

        // WHEN

        when(oracleQueryClient.getRegisteredEmailAddress(COMPANY_NUMBER)).thenReturn(registeredEmailAddress);

        // THEN

        assertEquals(registeredEmailAddress, emailService.getRegisteredEmailAddress(COMPANY_NUMBER));
    }

    @Test
    void getRegisteredEmailAddressServiceException() throws ServiceException {

        // WHEN

        when(oracleQueryClient.getRegisteredEmailAddress(COMPANY_NUMBER)).thenThrow(ServiceException.class);

        // THEN

        assertThrows(ServiceException.class, () -> emailService.getRegisteredEmailAddress(COMPANY_NUMBER));
    }

}
