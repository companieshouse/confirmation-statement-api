package uk.gov.companieshouse.confirmationstatementapi.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.confirmationstatementapi.client.OracleQueryClient;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.exception.StatementOfCapitalNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.exception.SubmissionNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.ConfirmationStatementSubmissionDao;
import uk.gov.companieshouse.confirmationstatementapi.model.json.statementofcapital.StatementOfCapitalJson;
import uk.gov.companieshouse.confirmationstatementapi.repository.ConfirmationStatementSubmissionsRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatementOfCapitalJsonServiceTest {

    private static final String COMPANY_NUMBER = "11111111";
    private static final String SUBMISSION_ID = "ABCDEFG";

    @Mock
    private OracleQueryClient oracleQueryClient;

    @Mock
    private ConfirmationStatementSubmissionsRepository confirmationStatementSubmissionsRepository;

    @InjectMocks
    private StatementOfCapitalService statementOfCapitalService;

    @Test
    void testGetStatementOfCapitalData() throws ServiceException, StatementOfCapitalNotFoundException, SubmissionNotFoundException {
        var confirmationStatementSubmission = new ConfirmationStatementSubmissionDao();
        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.of(confirmationStatementSubmission));
        when(oracleQueryClient.getStatementOfCapitalData(COMPANY_NUMBER)).thenReturn(new StatementOfCapitalJson());
        assertNotNull(statementOfCapitalService.getStatementOfCapital(SUBMISSION_ID, COMPANY_NUMBER));
    }

    @Test
    void testGetStatementOfCapitalDataServiceException() throws ServiceException, StatementOfCapitalNotFoundException, SubmissionNotFoundException {
        var confirmationStatementSubmission = new ConfirmationStatementSubmissionDao();
        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.of(confirmationStatementSubmission));
        when(oracleQueryClient.getStatementOfCapitalData(COMPANY_NUMBER)).thenThrow(ServiceException.class);
        assertThrows(ServiceException.class, () -> statementOfCapitalService.getStatementOfCapital(SUBMISSION_ID, COMPANY_NUMBER));
    }

    @Test
    void testGetSubmissionNotFoundException() {
        when(confirmationStatementSubmissionsRepository.findById(SUBMISSION_ID)).thenReturn(Optional.empty());
        assertThrows(SubmissionNotFoundException.class, () -> {
            statementOfCapitalService.getStatementOfCapital(SUBMISSION_ID, COMPANY_NUMBER);
        });
    }
}
