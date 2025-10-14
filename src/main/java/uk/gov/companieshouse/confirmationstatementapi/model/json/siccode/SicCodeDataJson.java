package uk.gov.companieshouse.confirmationstatementapi.model.json.siccode;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.confirmationstatementapi.model.json.SectionDataJson;

public class SicCodeDataJson extends SectionDataJson {

    @JsonProperty("sic_code")
    private List<SicCodeJson> sicCode;

    public List<SicCodeJson> getSicCode() {
        return sicCode;
    }

    public void setSicCode(List<SicCodeJson> sicCode) {
        this.sicCode = sicCode;
    }
}