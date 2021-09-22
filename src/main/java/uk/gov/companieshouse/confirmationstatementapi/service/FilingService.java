package uk.gov.companieshouse.confirmationstatementapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.model.filinggenerator.FilingApi;
import uk.gov.companieshouse.confirmationstatementapi.exception.SubmissionNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.model.json.ConfirmationStatementSubmissionJson;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class FilingService {

    @Value("${CONFIRMATION_STATEMENT_NO_UPDATES}")
    private String filingDescription;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private ConfirmationStatementService confirmationStatementService;

    @Autowired
    public FilingService(ConfirmationStatementService csService) {
        confirmationStatementService = csService;
    }

    public FilingApi generateConfirmationFiling(String confirmationStatementId) throws SubmissionNotFoundException {
        FilingApi filing = new FilingApi();
        filing.setKind("Confirmation Statement");
        setFilingApiData(filing, confirmationStatementId);
        setDescription(filing);
        return filing;
    }

    private void setFilingApiData(FilingApi filing, String confirmationStatementId) throws SubmissionNotFoundException {
        Optional<ConfirmationStatementSubmissionJson> submissionOpt =
                confirmationStatementService.getConfirmationStatement(confirmationStatementId);
        ConfirmationStatementSubmissionJson submission = submissionOpt
                .orElseThrow(() ->
                        new SubmissionNotFoundException(
                                String.format("Empty submission returned when generating filing for %s", confirmationStatementId)));
        if (submission.getData() != null) {
            Map<String, Object> data = new HashMap<>();
            data.put("confirmationStatementDate", submission.getData().getMadeUpToDate());
            data.put("tradingOnMarket", !submission.getData().getTradingStatusData().getTradingStatusAnswer());
            data.put("dtr5Ind", false);
            filing.setData(data);
        } else {
            throw new SubmissionNotFoundException(
                    String.format("Submission contains no data %s", confirmationStatementId));
        }
    }

    private void setDescription(FilingApi filing) {
        LocalDate madeUpToDate = (LocalDate)filing.getData().get("confirmationStatementDate");
        String madeUpToDateStr = madeUpToDate.format(formatter);
        filing.setDescriptionIdentifier(filingDescription);
        filing.setDescription(filingDescription.replace("{made up date}", madeUpToDateStr));
        Map<String, String> values = new HashMap<>();
        values.put("made up date", madeUpToDateStr);
        filing.setDescriptionValues(values);
    }
}
