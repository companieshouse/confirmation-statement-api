package uk.gov.companieshouse.confirmationstatementapi.model.mapping;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import uk.gov.companieshouse.confirmationstatementapi.model.SectionStatus;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.ConfirmationStatementSubmissionDao;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.siccode.SicCodeDataDao;
import uk.gov.companieshouse.confirmationstatementapi.model.json.ConfirmationStatementSubmissionJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.siccode.SicCodeJson;
import uk.gov.companieshouse.confirmationstatementapi.utils.ApiLogger;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.DATE_FORMAT_YYYYMD;

@Component
@Mapper(componentModel = "spring")
public interface ConfirmationStatementJsonDaoMapper {

      @Mapping(source = "data.madeUpToDate", target = "data.madeUpToDate", qualifiedByName = "localDate")
      @Mapping(source = "data.newConfirmationDate", target = "data.newConfirmationDate", qualifiedByName = "newCsDateLocalDateToString")
      @Mapping(source = "data.sicCodeData", target = "data.sicCodeData")
      ConfirmationStatementSubmissionJson daoToJson(ConfirmationStatementSubmissionDao confirmationStatementSubmissionDao);

      @Mapping(source = "data.madeUpToDate", target = "data.madeUpToDate", qualifiedByName = "localDate")
      @Mapping(source = "data.newConfirmationDate", target = "data.newConfirmationDate", qualifiedByName = "newCsDateStringToLocalDate")
      @Mapping(source = "data.sicCodeData", target = "data.sicCodeData")
      @Mapping(source = "data.sicCodeData.sicCode", target = "data.sicCodeData.sicCodes", qualifiedByName = "extractSicCodes")
      ConfirmationStatementSubmissionDao jsonToDao(ConfirmationStatementSubmissionJson confirmationStatementSubmissionJson);

      @Named("extractSicCodes")
      static List<String> extractSicCodes(List<SicCodeJson> sicCodeJsonList) {
            if (sicCodeJsonList == null) {
                  return Collections.emptyList();
            }
            return sicCodeJsonList.stream()
                        .map(SicCodeJson::getCode)
                        .toList();
      }

      @AfterMapping
      default void enrichSicCodeData(ConfirmationStatementSubmissionJson json,
                                    @MappingTarget ConfirmationStatementSubmissionDao dao) {
            if (json.getData() == null || json.getData().getSicCodeData() == null) {
                  ApiLogger.info("AfterMapping: No SIC code data found in JSON");
                  return;
            }                              
            var sicCodeJsonList = json.getData().getSicCodeData().getSicCode();
            var codes = extractSicCodes(sicCodeJsonList);

            if (dao.getData().getSicCodeData() == null) {
                  dao.getData().setSicCodeData(new SicCodeDataDao());
            }

            dao.getData().getSicCodeData().setSicCodes(codes);

            if (!codes.isEmpty()) {
                  dao.getData().getSicCodeData().setSectionStatus(SectionStatus.CONFIRMED);
            }
      }

      @Named("localDate")
      static LocalDate localDate(LocalDate date) {
            if (date == null) {
                  return null;
            }
            return LocalDate.of(date.getYear(), date.getMonth(), date.getDayOfMonth());
      }

      @Named("newCsDateStringToLocalDate")
      static LocalDate newCsDateStringToLocalDate(String newCsDateString) {
            if (newCsDateString == null || newCsDateString.isBlank()) {
                  return null;
            }
            return LocalDate.parse(newCsDateString, DateTimeFormatter.ofPattern(DATE_FORMAT_YYYYMD));
      }


      @Named("newCsDateLocalDateToString")
      static String newCsDateLocalDateToString(LocalDate newCsDateLocalDate) {
            if (newCsDateLocalDate == null) {
                  return null;
            }
            return newCsDateLocalDate.format(DateTimeFormatter.ofPattern(DATE_FORMAT_YYYYMD));
      }

}
