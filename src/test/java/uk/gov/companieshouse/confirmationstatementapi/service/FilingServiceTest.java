package uk.gov.companieshouse.confirmationstatementapi.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import uk.gov.companieshouse.api.model.filinggenerator.FilingApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.confirmationstatementapi.exception.SubmissionNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.model.json.ConfirmationStatementSubmissionDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.ConfirmationStatementSubmissionJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.TradingStatusDataJson;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FilingServiceTest {

    private static final String CONFIRMATION_ID = "abc123";

    @InjectMocks
    private FilingService filingService;

    @Mock
    private ConfirmationStatementService csService;

    @Mock
    private Environment environment;

    @Test
    void testWhenSubmissionIsReturnedSuccessfully() throws SubmissionNotFoundException {
        ConfirmationStatementSubmissionJson confirmationStatementSubmissionJson =  buildSubmissionJson();
        Optional<ConfirmationStatementSubmissionJson> opt = Optional.of(confirmationStatementSubmissionJson);
        when(environment.getProperty("confirmation.statement.no.updates")).thenReturn("**Confirmation statement** made on {made up date} with no updates");
        when(csService.getConfirmationStatement(CONFIRMATION_ID)).thenReturn(opt);
              FilingApi filing = filingService.generateConfirmationFiling(CONFIRMATION_ID);
        assertEquals("**Confirmation statement** made on 2021/06/01 with no updates", filing.getDescription());
        assertEquals(confirmationStatementSubmissionJson.getData().getMadeUpToDate(), filing.getData().get("confirmationStatementDate"));
        assertFalse((Boolean) filing.getData().get("tradingOnMarket"));
        assertFalse((Boolean) filing.getData().get("dtr5Ind"));
    }

    @Test
    void testWhenEmptySubmissionIsReturned() {
        when(csService.getConfirmationStatement(CONFIRMATION_ID)).thenReturn(Optional.empty());
        var transaction = new Transaction();
        assertThrows(SubmissionNotFoundException.class, () -> filingService.generateConfirmationFiling(CONFIRMATION_ID));
    }

    ConfirmationStatementSubmissionJson buildSubmissionJson() {
        ConfirmationStatementSubmissionJson confirmationStatementSubmissionJson =
                new ConfirmationStatementSubmissionJson();
        ConfirmationStatementSubmissionDataJson confirmationStatementSubmissionDataJson
                = new ConfirmationStatementSubmissionDataJson();
        confirmationStatementSubmissionDataJson.setMadeUpToDate(LocalDate.of(2021, 06, 01));
        TradingStatusDataJson tradingStatus = new TradingStatusDataJson();
        tradingStatus.setTradingStatusAnswer(true);
        confirmationStatementSubmissionDataJson.setTradingStatusData(tradingStatus);
        confirmationStatementSubmissionJson.setData(confirmationStatementSubmissionDataJson);
        return confirmationStatementSubmissionJson;
    }
}
