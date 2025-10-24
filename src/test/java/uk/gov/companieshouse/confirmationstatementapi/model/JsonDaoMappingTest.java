package uk.gov.companieshouse.confirmationstatementapi.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uk.gov.companieshouse.api.model.company.StatementOfCapitalJson;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.ConfirmationStatementSubmissionDao;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.ConfirmationStatementSubmissionDataDao;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.TradingStatusDataDao;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.activedirectordetails.ActiveOfficerDetailsDataDao;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.registeredemailaddress.RegisteredEmailAddressDataDao;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.registeredofficeaddress.RegisteredOfficeAddressDataDao;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.registerlocation.RegisterLocationsDataDao;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.shareholder.ShareholderDataDao;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.siccode.SicCodeDao;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.siccode.SicCodeDataDao;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.statementofcapital.StatementOfCapitalDao;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.statementofcapital.StatementOfCapitalDataDao;
import uk.gov.companieshouse.confirmationstatementapi.model.json.ConfirmationStatementSubmissionJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.TradingStatusDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.activedirectordetails.ActiveOfficerDetailsDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.registeredemailaddress.RegisteredEmailAddressDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.registeredofficeaddress.RegisteredOfficeAddressDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.registerlocation.RegisterLocationsDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.shareholder.ShareholderDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.siccode.SicCodeJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.statementofcapital.StatementOfCapitalDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.mapping.ConfirmationStatementJsonDaoMapper;
import uk.gov.companieshouse.confirmationstatementapi.model.mapping.ConfirmationStatementJsonDaoMapperImpl;

class JsonDaoMappingTest {

    private static final String SUBMISSION_ID = "abcdefg";

    private ConfirmationStatementJsonDaoMapper confirmationStatementJsonDaoMapper;

    @BeforeEach
    void init() {
        confirmationStatementJsonDaoMapper = new ConfirmationStatementJsonDaoMapperImpl();
    }

    @Test
    void testDaoToJson() {
        ConfirmationStatementSubmissionDataDao data =
                MockConfirmationStatementSubmissionData.getMockDaoData();
        var sicCode = new SicCodeDao();
        sicCode.setCode("123");
        sicCode.setDescription("TEST SIC CODE DETAILS");

        SicCodeDataDao sicCodeDataDao = new SicCodeDataDao();
        sicCodeDataDao.setSicCodes(List.of(sicCode.getCode()));
        sicCodeDataDao.setSectionStatus(SectionStatus.CONFIRMED);
        data.setSicCodeData(sicCodeDataDao);
        data.getSicCodeData().setSicCodes(List.of(sicCode.getCode()));       
        ConfirmationStatementSubmissionDao dao =
                new ConfirmationStatementSubmissionDao(SUBMISSION_ID, data, new HashMap<>());
        ConfirmationStatementSubmissionJson json =
                confirmationStatementJsonDaoMapper.daoToJson(dao);      
        testContentIsEqual(json, dao);
    }

    @Test
    void testJsonToDao() {
        ConfirmationStatementSubmissionJson json = new ConfirmationStatementSubmissionJson();
        json.setData(MockConfirmationStatementSubmissionData.getMockJsonData());
        ConfirmationStatementSubmissionDao dao =
                confirmationStatementJsonDaoMapper.jsonToDao(json);
        testContentIsEqual(json, dao);
    }

    @Test
    void shouldParseValidDateString() {
        String input = "2025-10-15";
        LocalDate expected = LocalDate.of(2025, 10, 15);
        LocalDate result = ConfirmationStatementJsonDaoMapper.newCsDateStringToLocalDate(input);
        assertEquals(expected, result);
    }

    @Test
    void testNullNewCsDateStringToLocalDate() {
        LocalDate result = ConfirmationStatementJsonDaoMapper.newCsDateStringToLocalDate(null);
        assertNull(result);
    }
        
    @Test
    void testValidNewCsDateLocalDateToString() {
        String result = ConfirmationStatementJsonDaoMapper.newCsDateLocalDateToString(LocalDate.of(2025, 10, 15));
        assertEquals("2025-10-15", result);
    }

    @Test
    void testNullNewCsDateLocalDateToString() {
        String result = ConfirmationStatementJsonDaoMapper.newCsDateLocalDateToString(null);
        assertNull(result);
    }

    @Test
    void shouldReturnNullForNullInput() {
        assertNull(ConfirmationStatementJsonDaoMapper.newCsDateStringToLocalDate(null));
    }

    @Test
    void shouldReturnNullForEmptyInput() {
        assertNull(ConfirmationStatementJsonDaoMapper.newCsDateStringToLocalDate(""));
    }

    @Test
    void shouldReturnNullForWhitespaceInput() {
        assertNull(ConfirmationStatementJsonDaoMapper.newCsDateStringToLocalDate("   "));
    }

    @Test
    void shouldThrowExceptionForInvalidDateFormat() {
        String input = "15-10-2025";
        assertThrows(DateTimeParseException.class, () -> ConfirmationStatementJsonDaoMapper.newCsDateStringToLocalDate(input));
    }

    @Test
    void testValidLocalDate() {
        LocalDate input = LocalDate.of(2025, 10, 15);
        LocalDate result = ConfirmationStatementJsonDaoMapper.localDate(input);
        assertEquals(input, result);
    }

    @Test
    void testNullLocalDate() {
        LocalDate result = ConfirmationStatementJsonDaoMapper.localDate(null);
        assertNull(result);
    }

    @Test
    void testValidExtractSicCodes() {
        SicCodeJson code1 = new SicCodeJson();
        code1.setCode("12345");
        SicCodeJson code2 = new SicCodeJson();
        code2.setCode("67890");

        List<String> result = ConfirmationStatementJsonDaoMapper.extractSicCodes(List.of(code1, code2));
        assertEquals(List.of("12345", "67890"), result);
    }

    @Test
    void testNullExtractSicCodes() {
        List<String> result = ConfirmationStatementJsonDaoMapper.extractSicCodes(null);
        assertEquals(List.of(), result);
    }

    @Test
    void testEmptyExtractSicCodes() {
        List<String> result = ConfirmationStatementJsonDaoMapper.extractSicCodes(List.of());
        assertEquals(List.of(), result);
    }

    private void testContentIsEqual(ConfirmationStatementSubmissionJson json, ConfirmationStatementSubmissionDao dao) {
        StatementOfCapitalDataJson socJson = json.getData().getStatementOfCapitalData();
        StatementOfCapitalDataDao socDao = dao.getData().getStatementOfCapitalData();
        assertEquals(socJson.getSectionStatus(), socDao.getSectionStatus());
        List<String> expectedSicCodes = List.of("123");
        List<String> actualSicCodes = dao.getData().getSicCodeData().getSicCodes();
        assertEquals(expectedSicCodes, actualSicCodes);
        StatementOfCapitalJson statementOfCapitalJson = socJson.getStatementOfCapital();
        StatementOfCapitalDao statementOfSubmissionCapital = socDao.getStatementOfCapital();
        assertEquals(statementOfCapitalJson.getClassOfShares(), statementOfSubmissionCapital.getClassOfShares());
        assertEquals(statementOfCapitalJson.getCurrency(), statementOfSubmissionCapital.getCurrency());
        assertEquals(statementOfCapitalJson.getNumberAllotted(), statementOfSubmissionCapital.getNumberAllotted());
        assertEquals(statementOfCapitalJson.getAggregateNominalValue(), statementOfSubmissionCapital.getAggregateNominalValue());
        assertEquals(statementOfCapitalJson.getPrescribedParticulars(), statementOfSubmissionCapital.getPrescribedParticulars());
        assertEquals(statementOfCapitalJson.getTotalNumberOfShares(), statementOfSubmissionCapital.getTotalNumberOfShares());
        assertEquals(statementOfCapitalJson.getTotalAggregateNominalValue(), statementOfSubmissionCapital.getTotalAggregateNominalValue());
        assertEquals(statementOfCapitalJson.getTotalAmountUnpaidForCurrency(), statementOfSubmissionCapital.getTotalAmountUnpaidForCurrency());

        List<String> sicDao = dao.getData().getSicCodeData().getSicCodes();
        RegisteredOfficeAddressDataJson roaJson = json.getData().getRegisteredOfficeAddressData();
        RegisteredOfficeAddressDataDao roaDao = dao.getData().getRegisteredOfficeAddressData();
        RegisteredEmailAddressDataJson reaJson = json.getData().getRegisteredEmailAddressData();
        RegisteredEmailAddressDataDao reaDao = dao.getData().getRegisteredEmailAddressData();
        ActiveOfficerDetailsDataJson dirJson = json.getData().getActiveOfficerDetailsData();
        ActiveOfficerDetailsDataDao dirDao = dao.getData().getActiveOfficerDetailsData();
        ShareholderDataJson shareholderJson = json.getData().getShareholderData();
        ShareholderDataDao shareholderDao = dao.getData().getShareholderData();
        RegisterLocationsDataJson rlJson = json.getData().getRegisterLocationsData();
        RegisterLocationsDataDao rlDao = dao.getData().getRegisterLocationsData();
        LocalDate madeUpToDateDao = dao.getData().getMadeUpToDate();
        LocalDate madeUpToDateJson = json.getData().getMadeUpToDate();
        TradingStatusDataJson tsJson = json.getData().getTradingStatusData();
        TradingStatusDataDao tsDao = dao.getData().getTradingStatusData();


        assertEquals(expectedSicCodes.get(0), sicDao.get(0));
        assertEquals(roaJson.getSectionStatus(), roaDao.getSectionStatus());
        assertEquals(reaJson.getSectionStatus(), reaDao.getSectionStatus());
        assertEquals(reaJson.getRegisteredEmailAddress(), reaDao.getRegisteredEmailAddress());
        assertEquals(dirJson.getSectionStatus(), dirDao.getSectionStatus());
        assertEquals(shareholderJson.getSectionStatus(), shareholderDao.getSectionStatus());
        assertEquals(rlJson.getSectionStatus(), rlDao.getSectionStatus());
        assertEquals(madeUpToDateJson, madeUpToDateDao);
        assertEquals(tsJson.getTradingStatusAnswer(), tsDao.getTradingStatusAnswer());
    }
}
