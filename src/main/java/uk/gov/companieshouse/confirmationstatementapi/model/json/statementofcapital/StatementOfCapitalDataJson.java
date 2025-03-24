package uk.gov.companieshouse.confirmationstatementapi.model.json.statementofcapital;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.api.model.company.StatementOfCapitalJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.SectionDataJson;

public class StatementOfCapitalDataJson extends SectionDataJson {

    @JsonProperty("statement_of_capital")
    private StatementOfCapitalJson statementOfCapital;

    public StatementOfCapitalJson getStatementOfCapital() {
        return statementOfCapital;
    }

    public void setStatementOfCapital(StatementOfCapitalJson statementOfCapital) {
        this.statementOfCapital = statementOfCapital;
    }
}
