package uk.gov.companieshouse.confirmationstatementapi.model.mapping;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.ConfirmationStatementSubmissionDao;
import uk.gov.companieshouse.confirmationstatementapi.model.json.ConfirmationStatementSubmissionJson;

@Component
@Mapper(componentModel = "spring")
public interface ConfirmationStatementJsonDaoMapper {

      ConfirmationStatementSubmissionJson daoToJson(ConfirmationStatementSubmissionDao confirmationStatementSubmissionDao);
      ConfirmationStatementSubmissionDao jsonToDao(ConfirmationStatementSubmissionJson confirmationStatementSubmissionJson);
}
