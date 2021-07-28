package uk.gov.companieshouse.confirmationstatementapi.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.confirmationstatementapi.model.json.personsignificantcontrol.PersonsSignificantControlDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.siccode.SicCodeDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.statementofcapital.StatementOfCapitalDataJson;

public class ConfirmationStatementSubmissionDataJson {

    @JsonProperty("statement_of_capital_data")
    private StatementOfCapitalDataJson statementOfCapitalData;

    @JsonProperty("persons_significant_control_data")
    private PersonsSignificantControlDataJson personsSignificantControlData;

    @JsonProperty("sic_code")
    private SicCodeDataJson sicCodeData;

    public StatementOfCapitalDataJson getStatementOfCapitalData() {
        return statementOfCapitalData;
    }

    public void setStatementOfCapitalData(StatementOfCapitalDataJson statementOfCapitalData) {
        this.statementOfCapitalData = statementOfCapitalData;
    }

    public PersonsSignificantControlDataJson getPersonsSignificantControlData() {
        return personsSignificantControlData;
    }

    public void setPersonsSignificantControlData(PersonsSignificantControlDataJson personsSignificantControlData) {
        this.personsSignificantControlData = personsSignificantControlData;
    }

    public SicCodeDataJson getSicCodeData() {
        return sicCodeData;
    }

    public void setSicCodeData(SicCodeDataJson sicCodeData) {
        this.sicCodeData = sicCodeData;
    }
}
