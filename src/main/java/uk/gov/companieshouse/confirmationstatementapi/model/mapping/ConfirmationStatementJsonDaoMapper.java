package uk.gov.companieshouse.confirmationstatementapi.model.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.ConfirmationStatementSubmissionDao;
import uk.gov.companieshouse.confirmationstatementapi.model.json.ConfirmationStatementSubmissionJson;

import java.time.LocalDate;

@Component
@Mapper(componentModel = "spring")
public interface ConfirmationStatementJsonDaoMapper {

      @Mapping(source = "data.madeUpToDate", target = "data.madeUpToDate", qualifiedByName = "localDate")
      ConfirmationStatementSubmissionJson daoToJson(ConfirmationStatementSubmissionDao confirmationStatementSubmissionDao);

      @Mapping(source = "data.madeUpToDate", target = "data.madeUpToDate", qualifiedByName = "localDate")
      ConfirmationStatementSubmissionDao jsonToDao(ConfirmationStatementSubmissionJson confirmationStatementSubmissionJson);

      @Named("localDate")
      static LocalDate localDate(LocalDate date) {
            if (date == null) {
                  return null;
            }
            return LocalDate.of(date.getYear(), date.getMonth(), date.getDayOfMonth());
      }
}
