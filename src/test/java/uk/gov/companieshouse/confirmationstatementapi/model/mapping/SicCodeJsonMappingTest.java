package uk.gov.companieshouse.confirmationstatementapi.model.mapping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import uk.gov.companieshouse.confirmationstatementapi.model.SectionStatus;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.siccode.SicCodeDataDao;
import uk.gov.companieshouse.confirmationstatementapi.model.json.siccode.SicCodeDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.siccode.SicCodeJson;

@SpringBootTest
class SicCodeJsonMappingTest {
    
    @Autowired
    private SicCodeJsonDaoMapper sicCodeJsonDaoMapper;

    @Test
    void testValidExtractSicCodes() {
        SicCodeJson code1 = new SicCodeJson();
        code1.setCode("12345");
        SicCodeJson code2 = new SicCodeJson();
        code2.setCode("67890");

        SicCodeDataJson json = new SicCodeDataJson();
        json.setSicCode(List.of(code1, code2));

        SicCodeDataDao dao = sicCodeJsonDaoMapper.jsonToDao(json);
        assertEquals(List.of("12345", "67890"), dao.getSicCodes());
    }

    @Test
    void testNullSicCodeStringsToJson() {
        List<SicCodeJson> result = sicCodeJsonDaoMapper.mapSicCodeStringsToJson(null);
        assertEquals(List.of(), result);
    }

    @Test
    void testEmptySicCodeStringsToJson() {
        List<SicCodeJson> result = sicCodeJsonDaoMapper.mapSicCodeStringsToJson(List.of());
        assertEquals(List.of(), result);
    }

    @Test
    void testNullSicCodeJsonToStrings() {
        List<String> result = sicCodeJsonDaoMapper.mapSicCodeJsonToStrings(null);
        assertEquals(List.of(), result);
    }

    @Test
    void testEmptySicCodeJsonToStrings() {
        List<String> result = sicCodeJsonDaoMapper.mapSicCodeJsonToStrings(List.of());
        assertEquals(List.of(), result);
    }

    @Test
    void enrichSicCodeDataSicCodesPresent() {
        // GIVEN
        SicCodeJson code1 = new SicCodeJson();
        code1.setCode("12345");
        SicCodeJson code2 = new SicCodeJson();
        code2.setCode("67890");

        SicCodeDataJson sicCodeDataJson = new SicCodeDataJson();
        sicCodeDataJson.setSicCode(List.of(code1, code2));

        SicCodeDataDao sicCodeDataDao = new SicCodeDataDao();

        // WHEN
        sicCodeJsonDaoMapper.enrichSicCodeData(sicCodeDataJson, sicCodeDataDao);

        // THEN
        assertEquals(List.of("12345", "67890"), sicCodeDataDao.getSicCodes());
        assertEquals(SectionStatus.CONFIRMED, sicCodeDataDao.getSectionStatus());
    }

    @Test
    void enrichSicCodeDataSicCodeListIsEmpty() {
        // GIVEN
        SicCodeDataJson sicCodeDataJson = new SicCodeDataJson();
        sicCodeDataJson.setSicCode(Collections.emptyList());

        SicCodeDataDao sicCodeDataDao = new SicCodeDataDao();

        // WHEN
        sicCodeJsonDaoMapper.enrichSicCodeData(sicCodeDataJson, sicCodeDataDao);

        // THEN
        assertEquals(Collections.emptyList(), sicCodeDataDao.getSicCodes());
        assertNull(sicCodeDataDao.getSectionStatus());
    }

    @Test
    void enrichSicCodeDataJsonDataIsNull() {
        // GIVEN
        SicCodeDataJson sicCodeDataJson = new SicCodeDataJson();
        sicCodeDataJson.setSicCode(null);

        SicCodeDataDao sicCodeDataDao = new SicCodeDataDao();

        // WHEN
        sicCodeJsonDaoMapper.enrichSicCodeData(sicCodeDataJson, sicCodeDataDao);

        // THEN
        assertEquals(Collections.emptyList(), sicCodeDataDao.getSicCodes());
        assertNull(sicCodeDataDao.getSectionStatus());
    }

    @Test
    void enrichSicCodeDataSicCodeDataIsNull() {
        // GIVEN
        SicCodeDataJson sicCodeDataJson = null;
        SicCodeDataDao sicCodeDataDao = new SicCodeDataDao();

        // WHEN
        sicCodeJsonDaoMapper.enrichSicCodeData(sicCodeDataJson, sicCodeDataDao);

        // THEN
        assertNull(sicCodeDataDao.getSicCodes());
        assertNull(sicCodeDataDao.getSectionStatus());
    }
}
