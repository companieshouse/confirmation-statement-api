package uk.gov.companieshouse.confirmationstatementapi.model.json.statementofcapital;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.confirmationstatementapi.model.SectionStatus;

public class StatementOfCapitalDataJson {

    @JsonProperty("section_status")
    private SectionStatus sectionStatus;

    @JsonProperty("statement_of_capital")
    private StatementOfCapitalJson statementOfCapitalJson;

    public SectionStatus getSectionStatus() {
        return sectionStatus;
    }

    public void setSectionStatus(SectionStatus sectionStatus) {
        this.sectionStatus = sectionStatus;
    }

    public StatementOfCapitalJson getStatementOfCapital() {
        return statementOfCapitalJson;
    }

    public void setStatementOfCapital(StatementOfCapitalJson statementOfCapitalJsonData) {
        this.statementOfCapitalJson = statementOfCapitalJsonData;
    }
}
