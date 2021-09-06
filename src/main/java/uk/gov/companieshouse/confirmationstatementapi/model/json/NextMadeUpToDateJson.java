package uk.gov.companieshouse.confirmationstatementapi.model.json;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public class NextMadeUpToDateJson {

    @JsonProperty("current_next_made_up_to_date")
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate currentNextMadeUpToDate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("is_due")
    private Boolean due;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("new_next_made_up_to_date")
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate newNextMadeUpToDate;


    public LocalDate getCurrentNextMadeUpToDate() {
        return currentNextMadeUpToDate;
    }

    public void setCurrentNextMadeUpToDate(LocalDate currentNextMadeUpToDate) {
        this.currentNextMadeUpToDate = currentNextMadeUpToDate;
    }

    public Boolean isDue() {
        return due;
    }

    public void setDue(Boolean due) {
        this.due = due;
    }

    public LocalDate getNewNextMadeUpToDate() {
        return newNextMadeUpToDate;
    }

    public void setNewNextMadeUpToDate(LocalDate newNextMadeUpToDate) {
        this.newNextMadeUpToDate = newNextMadeUpToDate;
    }
}
