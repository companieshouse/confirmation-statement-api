package uk.gov.companieshouse.confirmationstatementapi.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class ConfirmationStatementSubmissionJson {

    @JsonProperty
    private String id;

    @JsonProperty
    private Map<String, String> links;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, String> getLinks() {
        return links;
    }

    public void setLinks(Map<String, String> links) {
        this.links = links;
    }
}