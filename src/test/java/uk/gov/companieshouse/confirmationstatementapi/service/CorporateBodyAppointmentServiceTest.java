package uk.gov.companieshouse.confirmationstatementapi.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.confirmationstatementapi.client.OracleQueryClient;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.model.UsualResidentialAddress;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CorporateBodyAppointmentServiceTest {

    private static final String CORP_BODY_APPOINTMENT_ID = "4353";

    @Mock
    private OracleQueryClient oracleQueryClient;

    @InjectMocks
    private CorporateBodyAppointmentService corporateBodyAppointmentService;

    @Test
    void testGetUsualResidentialAddress() throws ServiceException {
        var ura = new UsualResidentialAddress();
        when(oracleQueryClient.getUsualResidentialAddress(CORP_BODY_APPOINTMENT_ID)).thenReturn(ura);
        var response = corporateBodyAppointmentService.getUsualResidentialAddress(CORP_BODY_APPOINTMENT_ID);

        assertEquals(ura, response);
    }

    @Test
    void testGetUsualResidentialAddressThrowsServiceException() throws ServiceException {
        var se = new ServiceException("Message");
        when(oracleQueryClient.getUsualResidentialAddress(CORP_BODY_APPOINTMENT_ID)).thenThrow(se);

        var exception = assertThrows(ServiceException.class, () -> corporateBodyAppointmentService.getUsualResidentialAddress(CORP_BODY_APPOINTMENT_ID));
        assertEquals(se, exception);
    }

}
