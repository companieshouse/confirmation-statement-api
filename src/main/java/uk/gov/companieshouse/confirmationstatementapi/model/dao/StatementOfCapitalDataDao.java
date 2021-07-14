package uk.gov.companieshouse.confirmationstatementapi.model.dao;

import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.confirmationstatementapi.model.tasks.TaskStatus;

public class StatementOfCapitalDataDao {
    @Field("task_status")
    private TaskStatus taskStatus;

    @Field("statement_of_capital")
    private StatementOfCapitalDao statementOfCapital;

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public StatementOfCapitalDao getStatementOfCapital() {
        return statementOfCapital;
    }

    public void setStatementOfCapital(StatementOfCapitalDao statementOfCapital) {
        this.statementOfCapital = statementOfCapital;
    }
}
