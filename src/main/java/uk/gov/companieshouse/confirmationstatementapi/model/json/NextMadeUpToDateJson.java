package uk.gov.companieshouse.confirmationstatementapi.model.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NextMadeUpToDateJson {

    @JsonProperty("current_next_made_up_to_date")
    private String currentNextMadeUpToDate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("is_due")
    private Boolean due;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("new_next_made_up_to_date")
    private String newNextMadeUpToDate;


    public String getCurrentNextMadeUpToDate() {
        return currentNextMadeUpToDate;
    }

    public void setCurrentNextMadeUpToDate(String currentNextMadeUpToDate) {
        this.currentNextMadeUpToDate = currentNextMadeUpToDate;
    }

    public Boolean isDue() {
        return due;
    }

    public void setDue(Boolean due) {
        this.due = due;
    }

    public String getNewNextMadeUpToDate() {
        return newNextMadeUpToDate;
    }

    public void setNewNextMadeUpToDate(String newNextMadeUpToDate) {
        this.newNextMadeUpToDate = newNextMadeUpToDate;
    }
}
