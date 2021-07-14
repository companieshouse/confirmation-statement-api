package uk.gov.companieshouse.confirmationstatementapi.model.mapping;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.confirmationstatementapi.model.ConfirmationStatementSubmission;
import uk.gov.companieshouse.confirmationstatementapi.model.json.ConfirmationStatementSubmissionJson;

@Component
@Mapper(componentModel = "spring")
public interface ConfirmationStatementJsonDaoMapper {

      ConfirmationStatementSubmissionJson daoToJson(ConfirmationStatementSubmission confirmationStatementSubmission);
      ConfirmationStatementSubmission jsonToDao(ConfirmationStatementSubmissionJson confirmationStatementSubmissionJson);
}
