package uk.gov.companieshouse.confirmationstatementapi.model.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.ConfirmationStatementSubmissionDao;
import uk.gov.companieshouse.confirmationstatementapi.model.json.ConfirmationStatementSubmissionJson;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.DATE_FORMAT_YYYYMD;

@Mapper(uses = SicCodeJsonDaoMapper.class)
public interface ConfirmationStatementJsonDaoMapper {

      @Mapping(source = "data.madeUpToDate", target = "data.madeUpToDate", qualifiedByName = "localDate")
      @Mapping(source = "data.newConfirmationDate", target = "data.newConfirmationDate", qualifiedByName = "newCsDateLocalDateToString")
      @Mapping(source = "data.sicCodeData", target = "data.sicCodeData")
      ConfirmationStatementSubmissionJson daoToJson(ConfirmationStatementSubmissionDao confirmationStatementSubmissionDao);

      @Mapping(source = "data.madeUpToDate", target = "data.madeUpToDate", qualifiedByName = "localDate")
      @Mapping(source = "data.newConfirmationDate", target = "data.newConfirmationDate", qualifiedByName = "newCsDateStringToLocalDate")
      @Mapping(source = "data.sicCodeData", target = "data.sicCodeData")
      ConfirmationStatementSubmissionDao jsonToDao(ConfirmationStatementSubmissionJson confirmationStatementSubmissionJson);

      @Named("localDate")
      static LocalDate localDate(LocalDate date) {
            if (date == null) {
                  return null;
            }
            return LocalDate.of(date.getYear(), date.getMonth(), date.getDayOfMonth());
      }

      @Named("newCsDateStringToLocalDate")
      default LocalDate newCsDateStringToLocalDate(String newCsDateString) {
            if (newCsDateString == null || newCsDateString.isBlank()) {
                  return null;
            }
            return LocalDate.parse(newCsDateString, DateTimeFormatter.ofPattern(DATE_FORMAT_YYYYMD));
      }


      @Named("newCsDateLocalDateToString")
      default String newCsDateLocalDateToString(LocalDate newCsDateLocalDate) {
            if (newCsDateLocalDate == null) {
                  return null;
            }
            return newCsDateLocalDate.format(DateTimeFormatter.ofPattern(DATE_FORMAT_YYYYMD));
      }

}
