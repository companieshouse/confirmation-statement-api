package uk.gov.companieshouse.confirmationstatementapi.model.dao;


import org.springframework.data.mongodb.core.mapping.Field;

public class StatementOfCapitalDao {

    @Field("class_of_shares")
    private String classOfShares;

    @Field("currency")
    private String currency;

    @Field("number_allotted")
    private String numberAllotted;

    @Field("aggregate_nominal_value")
    private String aggregateNominalValue;

    @Field("prescribed_particulars")
    private String prescribedParticulars;

    @Field("total_number_of_shares")
    private String totalNumberOfShares;

    @Field("total_aggregate_nominal_value")
    private String totalAggregateNominalValue;

    @Field("total_amount_unpaid_for_currency")
    private String totalAmountUnpaidForCurrency;

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

    public String getNumberAllotted() {
        return numberAllotted;
    }

    public void setNumberAllotted(String numberAllotted) {
        this.numberAllotted = numberAllotted;
    }

    public String getAggregateNominalValue() {
        return aggregateNominalValue;
    }

    public void setAggregateNominalValue(String aggregateNominalValue) {
        this.aggregateNominalValue = aggregateNominalValue;
    }

    public String getPrescribedParticulars() {
        return prescribedParticulars;
    }

    public void setPrescribedParticulars(String prescribedParticulars) {
        this.prescribedParticulars = prescribedParticulars;
    }

    public String getTotalNumberOfShares() {
        return totalNumberOfShares;
    }

    public void setTotalNumberOfShares(String totalNumberOfShares) {
        this.totalNumberOfShares = totalNumberOfShares;
    }

    public String getTotalAggregateNominalValue() {
        return totalAggregateNominalValue;
    }

    public void setTotalAggregateNominalValue(String totalAggregateNominalValue) {
        this.totalAggregateNominalValue = totalAggregateNominalValue;
    }

    public String getTotalAmountUnpaidForCurrency() {
        return totalAmountUnpaidForCurrency;
    }

    public void setTotalAmountUnpaidForCurrency(String totalAmountUnpaidForCurrency) {
        this.totalAmountUnpaidForCurrency = totalAmountUnpaidForCurrency;
    }}
