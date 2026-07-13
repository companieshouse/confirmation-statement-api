package uk.gov.companieshouse.confirmationstatementapi.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.siccode.CondensedSicCodeDao;
import uk.gov.companieshouse.confirmationstatementapi.model.json.siccode.CondensedSicCodeJson;
import uk.gov.companieshouse.confirmationstatementapi.model.mapping.CondensedSicCodeMapper;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class CondensedSicCodeService {
    private final MongoTemplate condensedSicCodeMongoTemplate;

    private final CondensedSicCodeMapper condensedSicCodeMapper;

    @Autowired
    public CondensedSicCodeService(@Qualifier("condensedSicCodeMongoTemplate")
                                   MongoTemplate condensedSicCodeMongoTemplate,
                                   CondensedSicCodeMapper condensedSicCodeMapper) {
        this.condensedSicCodeMongoTemplate = condensedSicCodeMongoTemplate;
        this.condensedSicCodeMapper = condensedSicCodeMapper;
    }

    @Cacheable("condensedSicCodeList")
    public List<CondensedSicCodeJson> getCondensedSicCodeList() {

        List<CondensedSicCodeDao> condensedSicCodeDaoList = condensedSicCodeMongoTemplate.findAll(CondensedSicCodeDao.class);

        return condensedSicCodeMapper.daoToJson(condensedSicCodeDaoList);
    }


    @CacheEvict(value = "condensedSicCodeList", allEntries = true)
    @Scheduled(fixedRate = 12, timeUnit = TimeUnit.HOURS)
    public void cacheEvict() {}

}
