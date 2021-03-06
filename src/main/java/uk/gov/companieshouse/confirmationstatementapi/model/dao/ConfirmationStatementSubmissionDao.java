package uk.gov.companieshouse.confirmationstatementapi.model.dao;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Map;

@Document(collection = "confirmation_statement_submissions")
public class ConfirmationStatementSubmissionDao {

    @Id
    private String id;

    @Field("data")
    private ConfirmationStatementSubmissionDataDao data;

    @Field("links")
    private Map<String, String> links;

    public ConfirmationStatementSubmissionDao() {
    }

    public ConfirmationStatementSubmissionDao(String id, ConfirmationStatementSubmissionDataDao data, Map<String, String> links) {
        this.id = id;
        this.data = data;
        this.links = links;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ConfirmationStatementSubmissionDataDao getData() {
        return data;
    }

    public void setData(ConfirmationStatementSubmissionDataDao data) {
        this.data = data;
    }

    public Map<String, String> getLinks() {
        return links;
    }

    public void setLinks(Map<String, String> links) {
        this.links = links;
    }
}
