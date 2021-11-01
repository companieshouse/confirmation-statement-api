package uk.gov.companieshouse.confirmationstatementapi.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.common.Address;
import uk.gov.companieshouse.confirmationstatementapi.client.OracleQueryClient;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.exception.SubmissionNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.ConfirmationStatementSubmissionDao;
import uk.gov.companieshouse.confirmationstatementapi.model.json.registerlocation.RegisterLocationJson;
import uk.gov.companieshouse.confirmationstatementapi.repository.ConfirmationStatementSubmissionsRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterLocationServiceTest {

    private static final String COMPANY_NUMBER = "12345678";
    private static final String SUBMISSION_ID = "ABCDEFG";

    @Mock
    private OracleQueryClient oracleQueryClient;

    @Mock
    private ConfirmationStatementSubmissionsRepository confirmationStatementSubmissionsRepository;

    @InjectMocks
    private RegisterLocationService regLocService;

    @Test
    void getRegisterLocationsData() throws ServiceException, SubmissionNotFoundException {
        var confirmationStatementSubmission = new ConfirmationStatementSubmissionDao();
        var regLoc1 = new RegisterLocationJson();
        regLoc1.setRegisterTypeDesc("desc1");
        regLoc1.setSailAddress(new Address());

        var regLoc2 = new RegisterLocationJson();
        regLoc2.setRegisterTypeDesc("desc2");
        regLoc2.setSailAddress(new Address());

        List<RegisterLocationJson> registerLocations = Arrays.asList(regLoc1, regLoc2);

        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.of(confirmationStatementSubmission));
        when(oracleQueryClient.getRegisterLocations(COMPANY_NUMBER)).thenReturn(registerLocations);
        var regLocData = regLocService.getRegisterLocations(SUBMISSION_ID, COMPANY_NUMBER);

        assertNotNull(regLocService.getRegisterLocations(SUBMISSION_ID, COMPANY_NUMBER));
        assertEquals("desc1", regLocData.get(0).getRegisterTypeDesc());
        assertEquals("desc2", regLocData.get(1).getRegisterTypeDesc());
        assertEquals(2, regLocData.size());
    }

    @Test
    void getSubmissionNotFoundException() {
        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.empty());

        assertThrows(SubmissionNotFoundException.class, () -> {
            regLocService.getRegisterLocations(SUBMISSION_ID, COMPANY_NUMBER);
        });
    }
}
