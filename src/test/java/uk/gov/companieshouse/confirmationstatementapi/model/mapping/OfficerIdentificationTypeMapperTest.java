package uk.gov.companieshouse.confirmationstatementapi.model.mapping;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class OfficerIdentificationTypeMapperTest {

    @Test
    void testNonEEAMapping() {
        String chsType = OfficerIdentificationTypeMapper.mapIdentificationTypeToChs("N");
        assertEquals("non-eea", chsType);
    }

    @Test
    void testEEAMapping() {
        String chsType = OfficerIdentificationTypeMapper.mapIdentificationTypeToChs("Y");
        assertEquals("eea", chsType);
    }

    @Test
    void testUKLimitedCompanyMapping() {
        String chsType = OfficerIdentificationTypeMapper.mapIdentificationTypeToChs("U");
        assertEquals("uk-limited-company", chsType);
    }

    @Test
    void testOtherCorporateBodyOrFirmMapping() {
        String chsType = OfficerIdentificationTypeMapper.mapIdentificationTypeToChs("G");
        assertEquals("other-corporate-body-or-firm", chsType);
    }

    @Test
    void testUnrecognizedMapping() {
        String chsType = OfficerIdentificationTypeMapper.mapIdentificationTypeToChs("Z");
        assertNull(chsType);
    }
}
