package uk.gov.companieshouse.confirmationstatementapi.model;

import uk.gov.companieshouse.confirmationstatementapi.model.dao.ConfirmationStatementSubmissionDataDao;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.StatementOfCapitalDao;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.StatementOfCapitalDataDao;
import uk.gov.companieshouse.confirmationstatementapi.model.json.ConfirmationStatementSubmissionDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.StatementOfCapitalDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.tasks.TaskStatus;

public class MockConfirmationStatementSubmissionData {

    public static ConfirmationStatementSubmissionDataJson GetMockJsonData() {
        ConfirmationStatementSubmissionDataJson data = new ConfirmationStatementSubmissionDataJson();
        data.setStatementOfCapitalData(getStatementOfCapitalJsonData());
        return data;
    }

    private static StatementOfCapitalDataJson getStatementOfCapitalJsonData() {
        StatementOfCapitalDataJson statementOfCapitalData = new StatementOfCapitalDataJson();
        statementOfCapitalData.setTaskStatus(TaskStatus.IN_PROGRESS);
        StatementOfCapital statementOfCapital = new StatementOfCapital();
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

    public static ConfirmationStatementSubmissionDataDao GetMockDaoData() {
        ConfirmationStatementSubmissionDataDao data = new ConfirmationStatementSubmissionDataDao();
        data.setStatementOfCapitalData(getStatementOfCapitalDaoData());
        return data;
    }

    private static StatementOfCapitalDataDao getStatementOfCapitalDaoData() {
        StatementOfCapitalDataDao statementOfCapitalData = new StatementOfCapitalDataDao();
        statementOfCapitalData.setTaskStatus(TaskStatus.IN_PROGRESS);
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
