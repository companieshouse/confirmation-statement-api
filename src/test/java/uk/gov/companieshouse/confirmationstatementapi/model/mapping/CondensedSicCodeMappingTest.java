package uk.gov.companieshouse.confirmationstatementapi.model.mapping;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.siccode.CondensedSicCodeDao;
import uk.gov.companieshouse.confirmationstatementapi.model.json.siccode.CondensedSicCodeJson;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CondensedSicCodeMappingTest {

    @Autowired
    private CondensedSicCodeMapper condensedSicCodeMapper;

    private static final String SIC_CODE_ID_13922 = "id-13922";
    private static final String SIC_CODE_13922 = "13922";
    private static final String SIC_DESCRIPTION_13922 = "manufacture of canvas goods";
    private static final String SIC_CODE_ID_13950 = "id-13950";
    private static final String SIC_CODE_13950 = "13950";
    private static final String SIC_DESCRIPTION_13950 = "Manufacture of non-wovens and articles made from non-wovens";

    @Test
    void testValidMappingForSicCodes() {
        CondensedSicCodeDao condensedSicCodeDao1 = new CondensedSicCodeDao();
        condensedSicCodeDao1.setId(SIC_CODE_ID_13922);
        condensedSicCodeDao1.setSicCode(SIC_CODE_13922);
        condensedSicCodeDao1.setSicDescription(SIC_DESCRIPTION_13922);

        CondensedSicCodeDao condensedSicCodeDao2 = new CondensedSicCodeDao();
        condensedSicCodeDao2.setId(SIC_CODE_ID_13950);
        condensedSicCodeDao2.setSicCode(SIC_CODE_13950);
        condensedSicCodeDao2.setSicDescription(SIC_DESCRIPTION_13950);

        List<CondensedSicCodeDao> condensedSicCodeDaoList = Arrays.asList(
                condensedSicCodeDao1,
                condensedSicCodeDao2);

        List<CondensedSicCodeJson> condensedSicCodeJsonList = condensedSicCodeMapper.daoToJson(condensedSicCodeDaoList);

        assertNotNull(condensedSicCodeJsonList);
        assertEquals(2, condensedSicCodeJsonList.size());
        assertEquals("manufacture of canvas goods", condensedSicCodeJsonList.get(0).getSicDescription());
    }

    @Test
    void testSicCodeNullList() {
        List<CondensedSicCodeJson> result = condensedSicCodeMapper.daoToJson(null);
        assertNull(result);
    }
}
