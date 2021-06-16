package uk.gov.companieshouse.confirmationstatementapi.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.gov.companieshouse.confirmationstatementapi.model.ConfirmationStatementSubmission;

@Repository
public interface ConfirmationStatementSubmissionsRepository
        extends MongoRepository<ConfirmationStatementSubmission, String> {
}
