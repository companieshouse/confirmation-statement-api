package uk.gov.companieshouse.confirmationstatementapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Shareholder {

    @JsonProperty("forename1")
    private String forename1;

    @JsonProperty("forename2")
    private String forename2;

    @JsonProperty("surname")
    private String surname;

    @JsonProperty("addressId")
    private long addressId;

    @JsonProperty("shares")
    private long shares;

    @JsonProperty("shareClassTypeId")
    private long shareClassTypeId;

    @JsonProperty("currencyTypeId")
    private long currencyTypeId;


    public String getForename1() {
        return forename1;
    }

    public void setForename1(String forename1) {
        this.forename1 = forename1;
    }

    public String getForename2() {
        return forename2;
    }

    public void setForename2(String forename2) {
        this.forename2 = forename2;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public long getAddressId() {
        return addressId;
    }

    public void setAddressId(long addressId) {
        this.addressId = addressId;
    }

    public long getShares() {
        return shares;
    }

    public void setShares(long shares) {
        this.shares = shares;
    }

    public long getShareClassTypeId() {
        return shareClassTypeId;
    }

    public void setShareClassTypeId(long shareClassTypeId) {
        this.shareClassTypeId = shareClassTypeId;
    }

    public long getCurrencyTypeId() {
        return currencyTypeId;
    }

    public void setCurrencyTypeId(long currencyTypeId) {
        this.currencyTypeId = currencyTypeId;
    }

}
