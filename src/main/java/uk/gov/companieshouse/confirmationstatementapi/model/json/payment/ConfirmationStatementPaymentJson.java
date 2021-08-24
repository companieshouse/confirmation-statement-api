package uk.gov.companieshouse.confirmationstatementapi.model.json.payment;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ConfirmationStatementPaymentJson {

    @JsonProperty("is_paid")
    private Boolean paid;

    public Boolean isPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }
}

