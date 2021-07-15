package uk.gov.companieshouse.confirmationstatementapi.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.ConfirmationStatementSubmissionDataDao;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.StatementOfCapitalDao;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.StatementOfCapitalDataDao;
import uk.gov.companieshouse.confirmationstatementapi.model.json.ConfirmationStatementSubmissionJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.StatementOfCapitalDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.mapping.ConfirmationStatementJsonDaoMapper;
import uk.gov.companieshouse.confirmationstatementapi.model.mapping.ConfirmationStatementJsonDaoMapperImpl;


import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonDaoMppingTest {

    private static final String SUBMISSION_ID = "abcdefg";

    private ConfirmationStatementJsonDaoMapper confirmationStatementJsonDaoMapper;

    @BeforeEach
    void init() {
        confirmationStatementJsonDaoMapper = new ConfirmationStatementJsonDaoMapperImpl();
    }

    @Test
    void testDaoToJson() {
        ConfirmationStatementSubmissionDataDao data =
                MockConfirmationStatementSubmissionData.GetMockDaoData();
        ConfirmationStatementSubmission dao =
                new ConfirmationStatementSubmission(SUBMISSION_ID, data, new HashMap<String, String>());
        ConfirmationStatementSubmissionJson json =
                confirmationStatementJsonDaoMapper.daoToJson(dao);
        testContentIsEqual(json, dao);
    }

    @Test
    void testJsonToDao() {
        ConfirmationStatementSubmissionJson json = new ConfirmationStatementSubmissionJson();
        json.setData(MockConfirmationStatementSubmissionData.GetMockJsonData());
        ConfirmationStatementSubmission dao =
                confirmationStatementJsonDaoMapper.jsonToDao(json);
        testContentIsEqual(json, dao);
    }

    private void testContentIsEqual(ConfirmationStatementSubmissionJson json, ConfirmationStatementSubmission dao) {
        StatementOfCapitalDataJson socJson = json.getData().getStatementOfCapitalData();
        StatementOfCapitalDataDao socDao = dao.getData().getStatementOfCapitalData();
        assertEquals(socJson.getTaskStatus(), socDao.getTaskStatus());
        StatementOfCapital statmentOfCapitalJson = socJson.getStatementOfCapital();
        StatementOfCapitalDao statementOfSubmissionCapital = socDao.getStatementOfCapital();
        assertEquals(statmentOfCapitalJson.getClassOfShares(), statementOfSubmissionCapital.getClassOfShares());
        assertEquals(statmentOfCapitalJson.getCurrency(), statementOfSubmissionCapital.getCurrency());
        assertEquals(statmentOfCapitalJson.getNumberAllotted(), statementOfSubmissionCapital.getNumberAllotted());
        assertEquals(statmentOfCapitalJson.getAggregateNominalValue(), statementOfSubmissionCapital.getAggregateNominalValue());
        assertEquals(statmentOfCapitalJson.getPrescribedParticulars(), statementOfSubmissionCapital.getPrescribedParticulars());
        assertEquals(statmentOfCapitalJson.getTotalNumberOfShares(), statementOfSubmissionCapital.getTotalNumberOfShares());
        assertEquals(statmentOfCapitalJson.getTotalAggregateNominalValue(), statementOfSubmissionCapital.getTotalAggregateNominalValue());
        assertEquals(statmentOfCapitalJson.getTotalAmountUnpaidForCurrency(), statementOfSubmissionCapital.getTotalAmountUnpaidForCurrency());
    }
}
