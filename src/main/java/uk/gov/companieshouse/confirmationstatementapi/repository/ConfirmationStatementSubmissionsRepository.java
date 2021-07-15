package uk.gov.companieshouse.confirmationstatementapi.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.ConfirmationStatementSubmissionDao;

@Repository
public interface ConfirmationStatementSubmissionsRepository
        extends MongoRepository<ConfirmationStatementSubmissionDao, String> {
}
