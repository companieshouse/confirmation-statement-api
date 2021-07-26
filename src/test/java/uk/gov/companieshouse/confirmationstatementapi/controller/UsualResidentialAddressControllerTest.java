package uk.gov.companieshouse.confirmationstatementapi.controller;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.model.UsualResidentialAddress;
import uk.gov.companieshouse.confirmationstatementapi.service.CorporateBodyAppointmentService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsualResidentialAddressControllerTest {

    private static final String CORP_BODY_APPOINTMENT_ID = "1231";

    @Mock
    private CorporateBodyAppointmentService corporateBodyAppointmentService;

    @InjectMocks
    private UsualResidentialAddressController usualResidentialAddressController;

    @Test
    void testGetUsualResidentialAddressOKResponse() throws ServiceException {
        var ura = new UsualResidentialAddress();
        when(corporateBodyAppointmentService.getUsualResidentialAddress(CORP_BODY_APPOINTMENT_ID)).thenReturn(ura);
        var response = usualResidentialAddressController.getUsualResidentialAddress(CORP_BODY_APPOINTMENT_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ura, response.getBody());
    }

    @Test
    void testGetUsualResidentialAddressServiceException() throws ServiceException {
        when(corporateBodyAppointmentService.getUsualResidentialAddress(CORP_BODY_APPOINTMENT_ID)).thenThrow(new ServiceException("Message"));
        var response = usualResidentialAddressController.getUsualResidentialAddress(CORP_BODY_APPOINTMENT_ID);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testGetUsualResidentialAddressUncheckedException() throws ServiceException {
        var runtimeException = new RuntimeException("Message");
        when(corporateBodyAppointmentService.getUsualResidentialAddress(CORP_BODY_APPOINTMENT_ID)).thenThrow(runtimeException);
        var thrown = assertThrows(Exception.class, () -> usualResidentialAddressController.getUsualResidentialAddress(CORP_BODY_APPOINTMENT_ID));

        assertEquals(runtimeException, thrown);
    }
}
