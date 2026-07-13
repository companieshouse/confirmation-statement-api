package uk.gov.companieshouse.confirmationstatementapi.model.mapping;

import org.mapstruct.Mapper;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.siccode.CondensedSicCodeDao;
import uk.gov.companieshouse.confirmationstatementapi.model.json.siccode.CondensedSicCodeJson;

import java.util.List;

@Mapper
public interface CondensedSicCodeMapper {

    List<CondensedSicCodeJson> daoToJson(List<CondensedSicCodeDao> condensedSicCodeDaoList);
}
