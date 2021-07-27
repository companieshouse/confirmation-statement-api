package uk.gov.companieshouse.confirmationstatementapi.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.confirmationstatementapi.model.SectionStatus;

public class SectionDataJson {

    @JsonProperty("section_status")
    private SectionStatus sectionStatus;

    public SectionStatus getSectionStatus() {
        return sectionStatus;
    }

    public void setSectionStatus(SectionStatus sectionStatus) {
        this.sectionStatus = sectionStatus;
    }
}
