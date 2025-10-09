package uk.gov.companieshouse.confirmationstatementapi.model.json.siccode;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.confirmationstatementapi.model.json.SectionDataJson;

public class SicCodeDataJson extends SectionDataJson {

    @JsonProperty("sic_codes")
    private List<SicCodeJson> sicCodes;

    public List<SicCodeJson> getSicCode() {
        return sicCodes;
    }

    public void setSicCode(List<SicCodeJson> sicCodes) {
        this.sicCodes = sicCodes;
    }
}