package uk.gov.companieshouse.confirmationstatementapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.model.filinggenerator.FilingApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.confirmationstatementapi.exception.SubmissionNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.model.json.ConfirmationStatementSubmissionJson;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class FilingService {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private ConfirmationStatementService confirmationStatementService;
    private Environment environment;

    @Autowired
    public FilingService(ConfirmationStatementService csService, Environment env) {
        confirmationStatementService = csService;
        environment = env;
    }

    public FilingApi generateConfirmationFiling(Transaction transaction, String confirmationStatementId) throws SubmissionNotFoundException {
        FilingApi filing = new FilingApi();
        filing.setKind("CS01");
        setFilingApiData(filing, confirmationStatementId);
        setDescription(filing);
        return filing;
    }

    private void setFilingApiData(FilingApi filing, String confirmationStatementId) throws SubmissionNotFoundException {
        Optional<ConfirmationStatementSubmissionJson> submissionOpt =
                confirmationStatementService.getConfirmationStatement(confirmationStatementId);
        if(submissionOpt.isEmpty()){
            throw new SubmissionNotFoundException(
                    String.format("Empty submission returned when generating filing for %s", confirmationStatementId));
        }
        ConfirmationStatementSubmissionJson submission = submissionOpt.get();
        if(submission.getData() != null) {
            Map<String, Object> data = new HashMap<>();
            data.put("confirmationStatementDate", submission.getData().getMadeUpToDate());
            data.put("tradingOnMarket", !submission.getData().getTradingStatusData().getTradingStatusAnswer());
            data.put("dtr5Ind", false);
            filing.setData(data);
        }
    }

    private void setDescription(FilingApi filing) {
        LocalDate madeUpToDate = (LocalDate)filing.getData().get("confirmationStatementDate");
        String description = environment.getProperty("confirmation.statement.no.updates");
        String madeUpDateStr = madeUpToDate.format(formatter);
        filing.setDescription(description.replace("(made_up_date)", madeUpDateStr));
    }

}
