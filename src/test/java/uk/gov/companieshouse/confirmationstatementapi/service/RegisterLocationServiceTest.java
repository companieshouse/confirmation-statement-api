package uk.gov.companieshouse.confirmationstatementapi.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.common.Address;
import uk.gov.companieshouse.confirmationstatementapi.client.OracleQueryClient;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.model.json.registerlocation.RegisterLocationJson;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterLocationServiceTest {

    private static final String COMPANY_NUMBER = "12345678";

    @Mock
    private OracleQueryClient oracleQueryClient;

    @InjectMocks
    private RegisterLocationService regLocService;

    @Test
    void getRegisterLocationsData() throws ServiceException {
        var regLoc1 = new RegisterLocationJson();
        regLoc1.setRegisterTypeDesc("desc1");
        regLoc1.setSailAddress(new Address());

        var regLoc2 = new RegisterLocationJson();
        regLoc2.setRegisterTypeDesc("desc2");
        regLoc2.setSailAddress(new Address());

        List<RegisterLocationJson> registerLocations = Arrays.asList(regLoc1, regLoc2);

        when(oracleQueryClient.getRegisterLocations(COMPANY_NUMBER)).thenReturn(registerLocations);
        var regLocData = regLocService.getRegisterLocations(COMPANY_NUMBER);

        assertNotNull(regLocService.getRegisterLocations(COMPANY_NUMBER));
        assertEquals(regLocData.get(0).getRegisterTypeDesc(), "desc1");
        assertEquals(regLocData.get(1).getRegisterTypeDesc(), "desc2");
        assertEquals(2, regLocData.size());
    }
}
