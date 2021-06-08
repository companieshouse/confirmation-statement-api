package uk.gov.companieshouse.confirmationstatementapi.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Map;

@Document(collection = "confirmation_statement_submissions")
public class ConfirmationStatementSubmission {

    @Id
    private String id;

    @Field("links")
    private Map<String, String> links;

    public ConfirmationStatementSubmission() {
    }

    public ConfirmationStatementSubmission(String id, Map<String, String> links) {
        this.id = id;
        this.links = links;
    }

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
