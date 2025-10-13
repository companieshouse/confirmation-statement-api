package uk.gov.companieshouse.confirmationstatementapi.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uk.gov.companieshouse.api.model.company.StatementOfCapitalJson;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.ConfirmationStatementSubmissionDao;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.ConfirmationStatementSubmissionDataDao;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.SectionDataDao;
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
import uk.gov.companieshouse.confirmationstatementapi.model.json.siccode.SicCodeDataJson;
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

    private void testContentIsEqual(ConfirmationStatementSubmissionJson json, ConfirmationStatementSubmissionDao dao) {
        StatementOfCapitalDataJson socJson = json.getData().getStatementOfCapitalData();
        StatementOfCapitalDataDao socDao = dao.getData().getStatementOfCapitalData();
        assertEquals(socJson.getSectionStatus(), socDao.getSectionStatus());
        List<SicCodeDataJson> sicCodeJsonList = json.getData().getSicCodeData();
        List<SicCodeDataDao> sicCodeDaoList = dao.getData().getSicCodeData();
        for (int i = 0; i < sicCodeJsonList.size(); i++) {
            SicCodeDataJson sicDataJson = sicCodeJsonList.get(i);
            SicCodeDataDao sicDataDao = sicCodeDaoList.get(i);

            assertEquals(sicDataJson.getSectionStatus(), sicDataDao.getSectionStatus());

            List<SicCodeJson> jsonSicCodes = sicDataJson.getSicCode();
            List<SicCodeDao> daoSicCodes = sicDataDao.getSicCode();

            for (int j = 0; j < jsonSicCodes.size(); j++) {
                assertEquals(jsonSicCodes.get(j).getCode(), daoSicCodes.get(j).getCode());
                assertEquals(jsonSicCodes.get(j).getDescription(), daoSicCodes.get(j).getDescription());
            }

        }
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
