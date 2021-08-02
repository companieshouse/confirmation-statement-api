package uk.gov.companieshouse.confirmationstatementapi.model.json.siccode;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.confirmationstatementapi.model.json.SectionDataJson;

public class SicCodeDataJson extends SectionDataJson {

    @JsonProperty("sic_code")
    private SicCodeJson sicCode;

    public SicCodeJson getSicCode() {
        return sicCode;
    }

    public void setSicCode(SicCodeJson sicCode) {
        this.sicCode = sicCode;
    }
}