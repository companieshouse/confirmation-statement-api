package uk.gov.companieshouse.confirmationstatementapi.model.mapping;

import org.mapstruct.*;
import uk.gov.companieshouse.confirmationstatementapi.model.SectionStatus;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.siccode.SicCodeDataDao;
import uk.gov.companieshouse.confirmationstatementapi.model.json.siccode.SicCodeDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.siccode.SicCodeJson;
import uk.gov.companieshouse.confirmationstatementapi.utils.ApiLogger;

import java.util.Collections;
import java.util.List;


@Mapper
public interface SicCodeJsonDaoMapper {

      @Mapping(source = "sicCodes", target = "sicCode", qualifiedByName = "mapSicCodeStringsToJson")
      SicCodeDataJson daoToJson(SicCodeDataDao sicCodeDataDao);

      @Mapping(source = "sicCode", target = "sicCodes", qualifiedByName = "mapSicCodeJsonToStrings")
      SicCodeDataDao jsonToDao(SicCodeDataJson sicCodeDataJson);

      @Named("mapSicCodeStringsToJson")
      default List<SicCodeJson> mapSicCodeStringsToJson(List<String> sicCodes) {
            if (sicCodes == null) {
                  return null;
            }
            return sicCodes.stream()
                  .map(s -> {
                        SicCodeJson sicCodeJson = new SicCodeJson();
                        sicCodeJson.setCode(s);
                        return sicCodeJson;
                  })
                  .toList();
      }

      @Named("mapSicCodeJsonToStrings")
      default List<String> mapSicCodeJsonToStrings(List<SicCodeJson> sicCodeJsonList) {
            if (sicCodeJsonList == null) {
                  return null;
            }

            return sicCodeJsonList.stream()
                        .map(SicCodeJson::getCode)
                        .toList();
      }

      
      @AfterMapping
      default void enrichSicCodeData(SicCodeDataJson json,
                                    @MappingTarget SicCodeDataDao dao) {

            if (dao == null) {
                  dao = new SicCodeDataDao();
            }

            dao.setSectionStatus(SectionStatus.CONFIRMED);

            if (json != null && json.getSicCode() != null && !json.getSicCode().isEmpty()) {
                  var codes = mapSicCodeJsonToStrings(json.getSicCode());
                  if (codes != null) {
                        dao.setSicCodes(codes);
                  }
            }
      }

}
