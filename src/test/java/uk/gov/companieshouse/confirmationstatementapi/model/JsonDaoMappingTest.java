package uk.gov.companieshouse.confirmationstatementapi.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.ConfirmationStatementSubmissionDao;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.ConfirmationStatementSubmissionDataDao;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.activedirectordetails.ActiveDirectorDetailsDataDao;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.registeredofficeaddress.RegisteredOfficeAddressDataDao;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.siccode.SicCodeDao;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.siccode.SicCodeDataDao;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.statementofcapital.StatementOfCapitalDao;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.statementofcapital.StatementOfCapitalDataDao;
import uk.gov.companieshouse.confirmationstatementapi.model.json.ConfirmationStatementSubmissionJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.activedirectordetails.ActiveDirectorDetailsDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.siccode.SicCodeDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.siccode.SicCodeJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.registeredofficeaddress.RegisteredOfficeAddressDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.statementofcapital.StatementOfCapitalDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.statementofcapital.StatementOfCapitalJson;
import uk.gov.companieshouse.confirmationstatementapi.model.mapping.ConfirmationStatementJsonDaoMapper;
import uk.gov.companieshouse.confirmationstatementapi.model.mapping.ConfirmationStatementJsonDaoMapperImpl;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        SicCodeDataJson sicDataJson = json.getData().getSicCodeData();
        SicCodeDataDao sicDataDao = dao.getData().getSicCodeData();
        assertEquals(sicDataJson.getSectionStatus(), sicDataDao.getSectionStatus());
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

        SicCodeJson sicJson = sicDataJson.getSicCode();
        SicCodeDao sicDao = sicDataDao.getSicCode();
        RegisteredOfficeAddressDataJson roaJson = json.getData().getRegisteredOfficeAddressData();
        RegisteredOfficeAddressDataDao roaDao = dao.getData().getRegisteredOfficeAddressData();
        ActiveDirectorDetailsDataJson dirJson = json.getData().getActiveDirectorDetailsData();
        ActiveDirectorDetailsDataDao dirDao = dao.getData().getActiveDirectorDetailsData();

        assertEquals(sicJson.getCode(), sicDao.getCode());
        assertEquals(sicJson.getDescription(), sicDao.getDescription());
        assertEquals(roaJson.getSectionStatus(), roaDao.getSectionStatus());
        assertEquals(dirJson.getSectionStatus(), dirDao.getSectionStatus());
    }
}
