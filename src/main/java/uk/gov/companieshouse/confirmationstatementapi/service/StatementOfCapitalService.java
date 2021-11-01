package uk.gov.companieshouse.confirmationstatementapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.confirmationstatementapi.client.OracleQueryClient;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.exception.StatementOfCapitalNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.exception.SubmissionNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.model.json.statementofcapital.StatementOfCapitalJson;
import uk.gov.companieshouse.confirmationstatementapi.repository.ConfirmationStatementSubmissionsRepository;
import uk.gov.companieshouse.confirmationstatementapi.utils.ApiLogger;

@Service
public class StatementOfCapitalService {

    @Autowired
    private OracleQueryClient oracleQueryClient;

    @Autowired
    private ConfirmationStatementSubmissionsRepository confirmationStatementSubmissionsRepository;

    public StatementOfCapitalJson getStatementOfCapital(String submissionId, String companyNumber) throws ServiceException, StatementOfCapitalNotFoundException, SubmissionNotFoundException{
        var submission = confirmationStatementSubmissionsRepository.findById(submissionId);
        if (submission.isPresent()) {
            ApiLogger.info(String.format("Found submission data for submission %s", submissionId));
        } else {
            throw new SubmissionNotFoundException("Could not find submission data for submission " + submissionId);
        }
        return oracleQueryClient.getStatementOfCapitalData(companyNumber);
    }
}
