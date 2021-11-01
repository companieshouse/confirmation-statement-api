package uk.gov.companieshouse.confirmationstatementapi.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.confirmationstatementapi.client.OracleQueryClient;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.exception.SubmissionNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.ConfirmationStatementSubmissionDao;
import uk.gov.companieshouse.confirmationstatementapi.model.json.shareholder.ShareholderJson;
import uk.gov.companieshouse.confirmationstatementapi.repository.ConfirmationStatementSubmissionsRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShareholderServiceTest {

    private static final String COMPANY_NUMBER = "12345678";
    private static final String SUBMISSION_ID = "ABCDEFG";

    @Mock
    private OracleQueryClient oracleQueryClient;

    @Mock
    private ConfirmationStatementSubmissionsRepository confirmationStatementSubmissionsRepository;

    @InjectMocks
    private ShareholderService shareholderService;

    @Test
    void testGetShareholderData() throws ServiceException, SubmissionNotFoundException {
        var confirmationStatementSubmission = new ConfirmationStatementSubmissionDao();
        var shareholder1 = new ShareholderJson();
        shareholder1.setSurname("Smith");
        var shareholder2 = new ShareholderJson();
        shareholder2.setSurname("Bond");

        List<ShareholderJson> shareholder = Arrays.asList(shareholder1, shareholder2);

        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.of(confirmationStatementSubmission));
        when(oracleQueryClient.getShareholders(COMPANY_NUMBER)).thenReturn(shareholder);
        var shareholderData = shareholderService.getShareholders(SUBMISSION_ID, COMPANY_NUMBER);

        assertNotNull(shareholderService.getShareholders(SUBMISSION_ID, COMPANY_NUMBER));
        assertEquals("Smith", shareholderData.get(0).getSurname());
        assertEquals("Bond", shareholderData.get(1).getSurname());
        assertEquals(2, shareholderData.size());
    }

    @Test
    void getCompanyShareholdersCountTest() {
        when(oracleQueryClient.getShareholderCount(COMPANY_NUMBER)).thenReturn(0);

        var response = shareholderService.getShareholderCount(COMPANY_NUMBER);

        assertEquals(0, response);
    }

    @Test
    void getSubmissionNotFoundException() {
        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.empty());

        assertThrows(SubmissionNotFoundException.class, () -> {
            shareholderService.getShareholders(SUBMISSION_ID, COMPANY_NUMBER);
        });
    }

}
