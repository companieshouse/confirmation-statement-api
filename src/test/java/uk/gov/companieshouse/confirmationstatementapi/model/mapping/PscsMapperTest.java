package uk.gov.companieshouse.confirmationstatementapi.model.mapping;

import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.confirmationstatementapi.model.PersonOfSignificantControl;
import uk.gov.companieshouse.confirmationstatementapi.model.json.PersonOfSignificantControlJson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PscsMapperTest {

    private static final String OFFICER_FORENAME_1 = "fred";
    private static final String OFFICER_FORENAME_2 = "john";
    private static final String OFFICER_SURNAME = "flintstone";
    private static final String OFFICER_DATE_OF_BIRTH = "1968-10-28 00:00:00";
    private static final String OFFICER_NATIONALITY = "BRITISH";
    private static final String APPOINTMENT_TYPE_ID = "10";
    private static final String SERVICE_ADDRESS_LINE_1 = "serv line 1";
    private static final String SERVICE_ADDRESS_POST_TOWN = "cardiff";
    private static final String SERVICE_ADDRESS_POST_CODE = "CF1 1AA";
    private static final String SECURE_PSC_IND = "N";
    private static final String HOUSE_NAME_NUMBER = "22";
    private static final String STREET = "street";
    private static final String AREA = "area";
    private static final String POST_TOWN = "bridgend";
    private static final String POST_CODE = "B1 1AA";
    private static final String REGION = "region";
    private static final String COUNTRY_NAME = "Wales";
    private static final String PO_BOX = "po box";
    private static final String SUPPLIED_COMPANY_NAME = "company name";
    private static final String ADDRESS_LINE_1 = "address line 1";
    private static final String REGISTRATION_NUMBER = "123456";
    private static final String LAW_GOVERED = "ENGLISH";
    private static final String LEGAL_FORM = "LIMITED";
    private static final String COUNTRY_OF_RESIDENCE = "UNITED KINGDOM";
    private PscsMapper pscsMapper = new PscsMapper();

    @Test
    void testMapToPscJson() {
        PersonOfSignificantControl psc1 = getPersonOfSignificantControl();
        psc1.setNatureOfControl("12;55;23");

        PersonOfSignificantControl psc2 = getPersonOfSignificantControl();
        psc2.setOfficerForename1("James");
        psc2.setOfficerForename2(null);
        psc2.setOfficerSurname("Smith");
        psc2.setAddressLine1("PSC2 ADD LINE1");
        psc2.setNatureOfControl("ABC;HH;XC");

        PersonOfSignificantControl psc3 = getPersonOfSignificantControl();
        psc3.setOfficerForename1("Kevin");
        psc3.setOfficerForename2(null);
        psc3.setOfficerSurname("Lloyd");
        psc3.setAddressLine1("PSC3 ADD LINE1");
        psc3.setNatureOfControl("55;22;88;66");

        List<PersonOfSignificantControl> pscs = Arrays.asList(psc1, psc2, psc3);

        List<PersonOfSignificantControlJson> pscsJson = pscsMapper.mapToPscsApi(pscs);

        assertEquals(3, pscsJson.size());

        var pscJson1 = pscsJson.get(0);
        assertEquals(OFFICER_FORENAME_1 + " " + OFFICER_FORENAME_2 + " " + OFFICER_SURNAME, pscJson1.getName());
        assertEquals(OFFICER_FORENAME_1, pscJson1.getNameElements().getForename());
        assertEquals(OFFICER_FORENAME_2, pscJson1.getNameElements().getOtherForenames());
        assertEquals(OFFICER_SURNAME, pscJson1.getNameElements().getSurname());

        assertEquals(10L, pscJson1.getDateOfBirth().getMonth());
        assertEquals(1968L, pscJson1.getDateOfBirth().getYear());
        assertEquals(OFFICER_NATIONALITY, pscJson1.getNationality());

        assertEquals(APPOINTMENT_TYPE_ID, pscJson1.getAppointmentType());
        assertEquals(SERVICE_ADDRESS_LINE_1, pscJson1.getServiceAddressLine1());
        assertEquals(SERVICE_ADDRESS_POST_TOWN, pscJson1.getServiceAddressPostTown());
        assertEquals(SERVICE_ADDRESS_POST_CODE, pscJson1.getServiceAddressPostCode());

        assertEquals(ADDRESS_LINE_1, pscJson1.getAddress().getAddressLine1());
        assertEquals(HOUSE_NAME_NUMBER, pscJson1.getAddress().getPremises());
        assertEquals(STREET, pscJson1.getAddress().getAddressLine2());
        assertEquals(POST_TOWN, pscJson1.getAddress().getLocality());
        assertEquals(POST_CODE, pscJson1.getAddress().getPostalCode());
        assertEquals(REGION, pscJson1.getAddress().getRegion());
        assertEquals(COUNTRY_NAME, pscJson1.getAddress().getCountry());
        assertEquals(PO_BOX, pscJson1.getAddress().getPoBox());

        assertEquals(SUPPLIED_COMPANY_NAME, pscJson1.getCompanyName());

        assertEquals(REGISTRATION_NUMBER, pscJson1.getRegistrationNumber());
        assertEquals(LAW_GOVERED, pscJson1.getLawGoverned());
        assertEquals(LEGAL_FORM, pscJson1.getLegalForm());
        assertEquals(COUNTRY_OF_RESIDENCE, pscJson1.getCountryOfResidence());

        assertEquals(3, pscJson1.getNaturesOfControl().length);
        assertEquals("12", pscJson1.getNaturesOfControl()[0]);
        assertEquals("55", pscJson1.getNaturesOfControl()[1]);
        assertEquals("23", pscJson1.getNaturesOfControl()[2]);


        var pscJson2 = pscsJson.get(1);
        assertEquals("James Smith", pscJson2.getName());
        assertEquals("James", pscJson2.getNameElements().getForename());
        assertNull(pscJson2.getNameElements().getOtherForenames());
        assertEquals("Smith", pscJson2.getNameElements().getSurname());
        assertEquals("PSC2 ADD LINE1", pscJson2.getAddress().getAddressLine1());

        assertEquals(3, pscJson2.getNaturesOfControl().length);
        assertEquals("ABC", pscJson2.getNaturesOfControl()[0]);
        assertEquals("HH", pscJson2.getNaturesOfControl()[1]);
        assertEquals("XC", pscJson2.getNaturesOfControl()[2]);


        var pscJson3 = pscsJson.get(2);
        assertEquals("Kevin Lloyd", pscJson3.getName());
        assertEquals("Kevin", pscJson3.getNameElements().getForename());
        assertNull(pscJson3.getNameElements().getOtherForenames());
        assertEquals("Lloyd", pscJson3.getNameElements().getSurname());
        assertEquals("PSC3 ADD LINE1", pscJson3.getAddress().getAddressLine1());

        assertEquals(4, pscJson3.getNaturesOfControl().length);
        assertEquals("55", pscJson3.getNaturesOfControl()[0]);
        assertEquals("22", pscJson3.getNaturesOfControl()[1]);
        assertEquals("88", pscJson3.getNaturesOfControl()[2]);
        assertEquals("66", pscJson3.getNaturesOfControl()[3]);

    }

    @Test
    void testNullInput() {
        var pscsJsons = pscsMapper.mapToPscsApi(null);
        assertEquals(0, pscsJsons.size());
    }

    @Test
    void testEmptyInput() {
        var pscsJsons = pscsMapper.mapToPscsApi(new ArrayList<>());
        assertEquals(0, pscsJsons.size());
    }

    private PersonOfSignificantControl getPersonOfSignificantControl() {
        PersonOfSignificantControl psc = new PersonOfSignificantControl();
        psc.setOfficerForename1(OFFICER_FORENAME_1);
        psc.setOfficerForename2(OFFICER_FORENAME_2);
        psc.setOfficerSurname(OFFICER_SURNAME);
        psc.setOfficerDateOfBirth(OFFICER_DATE_OF_BIRTH);
        psc.setOfficerNationality(OFFICER_NATIONALITY);
        psc.setAppointmentTypeId(APPOINTMENT_TYPE_ID);
        psc.setServiceAddressLine1(SERVICE_ADDRESS_LINE_1);
        psc.setServiceAddressPostTown(SERVICE_ADDRESS_POST_TOWN);
        psc.setServiceAddressPostCode(SERVICE_ADDRESS_POST_CODE);
        psc.setSuperSecurePscInd(SECURE_PSC_IND);
        psc.setHouseNameNumber(HOUSE_NAME_NUMBER);
        psc.setStreet(STREET);
        psc.setArea(AREA);
        psc.setPostTown(POST_TOWN);
        psc.setPostCode(POST_CODE);
        psc.setRegion(REGION);
        psc.setCountryName(COUNTRY_NAME);
        psc.setPoBox(PO_BOX);
        psc.setSuppliedCompanyName(SUPPLIED_COMPANY_NAME);
        psc.setAddressLine1(ADDRESS_LINE_1);
        psc.setRegistrationNumber(REGISTRATION_NUMBER);
        psc.setLawGoverned(LAW_GOVERED);
        psc.setLegalForm(LEGAL_FORM);
        psc.setCountryOfResidence(COUNTRY_OF_RESIDENCE);
        return psc;
    }
}
