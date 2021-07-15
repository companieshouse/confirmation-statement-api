package uk.gov.companieshouse.confirmationstatementapi.model;

import uk.gov.companieshouse.confirmationstatementapi.model.dao.ConfirmationStatementSubmissionDataDao;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.statementofcapital.StatementOfCapitalDao;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.statementofcapital.StatementOfCapitalDataDao;
import uk.gov.companieshouse.confirmationstatementapi.model.json.ConfirmationStatementSubmissionDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.statementofcapital.StatementOfCapitalJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.statementofcapital.StatementOfCapitalDataJson;

public class MockConfirmationStatementSubmissionData {

    public static ConfirmationStatementSubmissionDataJson GetMockJsonData() {
        ConfirmationStatementSubmissionDataJson data = new ConfirmationStatementSubmissionDataJson();
        data.setStatementOfCapitalData(getStatementOfCapitalJsonData());
        return data;
    }

    private static StatementOfCapitalDataJson getStatementOfCapitalJsonData() {
        StatementOfCapitalDataJson statementOfCapitalData = new StatementOfCapitalDataJson();
        statementOfCapitalData.setSectionStatus(SectionStatus.NOT_CONFIRMED);
        StatementOfCapitalJson statementOfCapitalJson = new StatementOfCapitalJson();
        statementOfCapitalJson.setClassOfShares("ORDINARY");
        statementOfCapitalJson.setCurrency("GBP");
        statementOfCapitalJson.setNumberAllotted("100");
        statementOfCapitalJson.setAggregateNominalValue("0.01");
        statementOfCapitalJson.setPrescribedParticulars("THE QUICK BROWN FOX");
        statementOfCapitalJson.setTotalNumberOfShares("100");
        statementOfCapitalJson.setTotalAggregateNominalValue("1");
        statementOfCapitalJson.setTotalAmountUnpaidForCurrency("2");
        statementOfCapitalData.setStatementOfCapital(statementOfCapitalJson);
        return statementOfCapitalData;
    }

    public static ConfirmationStatementSubmissionDataDao GetMockDaoData() {
        ConfirmationStatementSubmissionDataDao data = new ConfirmationStatementSubmissionDataDao();
        data.setStatementOfCapitalData(getStatementOfCapitalDaoData());
        return data;
    }

    private static StatementOfCapitalDataDao getStatementOfCapitalDaoData() {
        StatementOfCapitalDataDao statementOfCapitalData = new StatementOfCapitalDataDao();
        statementOfCapitalData.setSectionStatus(SectionStatus.NOT_CONFIRMED);
        StatementOfCapitalDao statementOfCapital = new StatementOfCapitalDao();
        statementOfCapital.setClassOfShares("ORDINARY");
        statementOfCapital.setCurrency("GBP");
        statementOfCapital.setNumberAllotted("100");
        statementOfCapital.setAggregateNominalValue("0.01");
        statementOfCapital.setPrescribedParticulars("THE QUICK BROWN FOX");
        statementOfCapital.setTotalNumberOfShares("100");
        statementOfCapital.setTotalAggregateNominalValue("1");
        statementOfCapital.setTotalAmountUnpaidForCurrency("2");
        statementOfCapitalData.setStatementOfCapital(statementOfCapital);
        return statementOfCapitalData;
    }
}
