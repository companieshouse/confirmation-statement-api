package uk.gov.companieshouse.confirmationstatementapi.model.json.siccode;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CondensedSicCodeJson {
    @JsonProperty("sic_code")
    private String sicCode;

    @JsonProperty("sic_description")
    private String sicDescription;

    public CondensedSicCodeJson() {
    }

    public CondensedSicCodeJson(String sicCode, String sicDescription) {
        this.sicCode = sicCode;
        this.sicDescription = sicDescription;
    }

    public String getSicCode() {
        return sicCode;
    }

    public void setSicCode(String sicCode) {
        this.sicCode = sicCode;
    }

    public String getSicDescription() {
        return sicDescription;
    }

    public void setSicDescription(String sicDescription) {
        this.sicDescription = sicDescription;
    }
}
