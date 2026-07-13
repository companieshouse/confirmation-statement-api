package uk.gov.companieshouse.confirmationstatementapi.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.siccode.CondensedSicCodeDao;
import uk.gov.companieshouse.confirmationstatementapi.model.json.siccode.CondensedSicCodeJson;
import uk.gov.companieshouse.confirmationstatementapi.model.mapping.CondensedSicCodeMapper;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CondensedSicCodeServiceTest {

    @Mock
    private MongoTemplate condensedSicCodeMongoTemplate;

    @Mock
    private CondensedSicCodeMapper condensedSicCodeMapper;

    @InjectMocks
    private CondensedSicCodeService condensedSicCodeService;

    private static final String SIC_CODE_ID_10120 = "id-10120";
    private static final String SIC_CODE_10120 = "10120";
    private static final String SIC_DESCRIPTION_10120 = "Processing and preserving of poultry meat";
    private static final String SIC_CODE_ID_10511 = "id-10511";
    private static final String SIC_CODE_10511 = "10511";
    private static final String SIC_DESCRIPTION_10511 = "Liquid milk and cream production";

    @Test
    void shouldReturnCondensedSicCodeList() {
        // GIVEN
        List<CondensedSicCodeDao> condensedSicCodeDaoList = getMockCondensedSicCodeDaoList();
        when(condensedSicCodeMongoTemplate.findAll(CondensedSicCodeDao.class)).thenReturn(condensedSicCodeDaoList);
        when(condensedSicCodeMapper.daoToJson(condensedSicCodeDaoList)).thenReturn(getMockCondensedSicCodeJsonList());

        // When
        List<CondensedSicCodeJson> result = condensedSicCodeService.getCondensedSicCodeList();

        // Then
        verify(condensedSicCodeMongoTemplate, times(1))
                .findAll(CondensedSicCodeDao.class);
        verify(condensedSicCodeMapper, times(1))
                .daoToJson(condensedSicCodeDaoList);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("10120", result.get(0).getSicCode());
    }

    @Test
    void shouldReturnNullSicCodeListWhenNoSicCodeInDb() {
        // GIVEN
        when(condensedSicCodeMongoTemplate.findAll(CondensedSicCodeDao.class)).thenReturn(null);
        when(condensedSicCodeMapper.daoToJson(null)).thenReturn(null);

        // When
        List<CondensedSicCodeJson> result = condensedSicCodeService.getCondensedSicCodeList();

        // Then
        verify(condensedSicCodeMongoTemplate, times(1))
                .findAll(CondensedSicCodeDao.class);
        verify(condensedSicCodeMapper, times(1))
                .daoToJson(null);
        assertNull(result);
    }

    @Test
    void shouldExecuteCacheEvict() {
        assertDoesNotThrow(() -> condensedSicCodeService.cacheEvict());
    }

    private List<CondensedSicCodeDao> getMockCondensedSicCodeDaoList() {
        return Arrays.asList(new CondensedSicCodeDao(SIC_CODE_ID_10120, SIC_CODE_10120, SIC_DESCRIPTION_10120),
                new CondensedSicCodeDao(SIC_CODE_ID_10511, SIC_CODE_10511, SIC_DESCRIPTION_10511));
    }

    private List<CondensedSicCodeJson> getMockCondensedSicCodeJsonList() {
        return Arrays.asList(new CondensedSicCodeJson(SIC_CODE_10120, SIC_DESCRIPTION_10120),
                new CondensedSicCodeJson(SIC_CODE_10511, SIC_DESCRIPTION_10511));
    }

}
