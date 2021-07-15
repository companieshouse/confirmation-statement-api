package uk.gov.companieshouse.confirmationstatementapi.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.confirmationstatementapi.model.StatementOfCapital;
import uk.gov.companieshouse.confirmationstatementapi.model.tasks.TaskStatus;

public class StatementOfCapitalDataJson {

    @JsonProperty("task_status")
    private TaskStatus taskStatus;

    @JsonProperty("statement_of_capital")
    private StatementOfCapital statementOfCapital;

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public StatementOfCapital getStatementOfCapital() {
        return statementOfCapital;
    }

    public void setStatementOfCapital(StatementOfCapital statementOfCapitalData) {
        this.statementOfCapital = statementOfCapitalData;
    }
}