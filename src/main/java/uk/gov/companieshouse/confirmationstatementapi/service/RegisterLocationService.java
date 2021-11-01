package uk.gov.companieshouse.confirmationstatementapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.confirmationstatementapi.client.OracleQueryClient;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.exception.SubmissionNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.model.json.registerlocation.RegisterLocationJson;
import uk.gov.companieshouse.confirmationstatementapi.repository.ConfirmationStatementSubmissionsRepository;
import uk.gov.companieshouse.confirmationstatementapi.utils.ApiLogger;

import java.util.List;

@Service
public class RegisterLocationService {

    private final OracleQueryClient oracleQueryClient;
    private final ConfirmationStatementSubmissionsRepository confirmationStatementSubmissionsRepository;

    @Autowired
    public RegisterLocationService(OracleQueryClient oracleQueryClient, ConfirmationStatementSubmissionsRepository confirmationStatementSubmissionsRepository) {
        this.oracleQueryClient = oracleQueryClient;
        this.confirmationStatementSubmissionsRepository = confirmationStatementSubmissionsRepository;
    }

    public List<RegisterLocationJson> getRegisterLocations(String submissionId, String companyNumber) throws ServiceException, SubmissionNotFoundException {
        var submission = confirmationStatementSubmissionsRepository.findById(submissionId);
        if (submission.isPresent()) {
            ApiLogger.info(String.format("Found submission data for submission %s", submissionId));
        } else {
            throw new SubmissionNotFoundException("Could not find submission data for submission " + submissionId);
        }
        return oracleQueryClient.getRegisterLocations(companyNumber);
    }
}
