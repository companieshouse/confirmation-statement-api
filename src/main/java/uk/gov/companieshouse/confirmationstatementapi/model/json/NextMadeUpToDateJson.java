package uk.gov.companieshouse.confirmationstatementapi.model.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class NextMadeUpToDateJson {

    @JsonProperty("current_next_made_up_to_date")
    private String currentNextMadeUpToDate;

    @JsonProperty("is_due")
    private boolean due;

    @JsonProperty("new_next_made_up_to_date")
    private String newNextMadeUpToDate;


    public String getCurrentNextMadeUpToDate() {
        return currentNextMadeUpToDate;
    }

    public void setCurrentNextMadeUpToDate(String currentNextMadeUpToDate) {
        this.currentNextMadeUpToDate = currentNextMadeUpToDate;
    }

    public boolean isDue() {
        return due;
    }

    public void setDue(boolean due) {
        this.due = due;
    }

    public String getNewNextMadeUpToDate() {
        return newNextMadeUpToDate;
    }

    public void setNewNextMadeUpToDate(String newNextMadeUpToDate) {
        this.newNextMadeUpToDate = newNextMadeUpToDate;
    }
}
