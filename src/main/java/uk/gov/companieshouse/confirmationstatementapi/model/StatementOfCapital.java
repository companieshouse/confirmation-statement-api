package uk.gov.companieshouse.confirmationstatementapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StatementOfCapital {

    @JsonProperty("class_of_shares")
    private String classOfShares;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("number_allotted")
    private Integer numberAllotted;

    @JsonProperty("aggregate_nominal_value")
    private Integer aggregateNominalValue;

    @JsonProperty("prescribed_particulars")
    private String prescribedParticulars;

    @JsonProperty("total_currency")
    private String totalCurrency;

    @JsonProperty("total_number_of_shares")
    private Integer totalNumberOfShares;

    @JsonProperty("total_aggregate_nominal_value")
    private Integer totalAggregateNominalValue;

    @JsonProperty("total_amount_unpaid_for_currency")
    private Integer totalAmountUnpaidForCurrency;

    public String getClassOfShares() {
        return classOfShares;
    }

    public void setClassOfShares(String classOfShares) {
        this.classOfShares = classOfShares;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Integer getNumberAllotted() {
        return numberAllotted;
    }

    public void setNumberAllotted(Integer numberAllotted) {
        this.numberAllotted = numberAllotted;
    }

    public Integer getAggregateNominalValue() {
        return aggregateNominalValue;
    }

    public void setAggregateNominalValue(Integer aggregateNominalValue) {
        this.aggregateNominalValue = aggregateNominalValue;
    }

    public String getPrescribedParticulars() {
        return prescribedParticulars;
    }

    public void setPrescribedParticulars(String prescribedParticulars) {
        this.prescribedParticulars = prescribedParticulars;
    }

    public String getTotalCurrency() {
        return totalCurrency;
    }

    public void setTotalCurrency(String totalCurrency) {
        this.totalCurrency = totalCurrency;
    }

    public Integer getTotalNumberOfShares() {
        return totalNumberOfShares;
    }

    public void setTotalNumberOfShares(Integer totalNumberOfShares) {
        this.totalNumberOfShares = totalNumberOfShares;
    }

    public Integer getTotalAggregateNominalValue() {
        return totalAggregateNominalValue;
    }

    public void setTotalAggregateNominalValue(Integer totalAggregateNominalValue) {
        this.totalAggregateNominalValue = totalAggregateNominalValue;
    }

    public Integer getTotalAmountUnpaidForCurrency() {
        return totalAmountUnpaidForCurrency;
    }

    public void setTotalAmountUnpaidForCurrency(Integer totalAmountUnpaidForCurrency) {
        this.totalAmountUnpaidForCurrency = totalAmountUnpaidForCurrency;
    }

    @Override
    public String toString(){
        return String.format("Statement of capital: { " +
            "class_of_shares %s," +
            "currency %s, " +
            "number_allotted %d, " +
            "aggregate_nominal_value %d, " +
            "prescribed_particulars %s, " +
            "total_currency %s, " +
            "total_number_of_shares %d, " +
            "total_aggregate_nominal_value %d, " +
            "total_amount_unpaid_for_currency %d }",
           classOfShares,
           currency,
           numberAllotted,
           aggregateNominalValue,
           prescribedParticulars,
           totalCurrency,
           totalNumberOfShares,
           totalAggregateNominalValue,
           totalAmountUnpaidForCurrency
        );
    }
}
