package uk.gov.companieshouse.confirmationstatementapi.model.json.shareholder;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ShareholderJson {
    @JsonProperty("forename1")
    private String forename1;

    @JsonProperty("forename2")
    private String forename2;

    @JsonProperty("surname")
    private String surname;

    @JsonProperty("shares")
    private String shares;

    @JsonProperty("share_class_type_id")
    private String shareClassTypeId;

    @JsonProperty("currency_type_id")
    private String currencyTypeId;

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

    public String getShares() {
        return shares;
    }

    public void setShares(String shares) {
        this.shares = shares;
    }

    public String getShareClassTypeId() {
        return shareClassTypeId;
    }

    public void setShareClassTypeId(String shareClassTypeId) {
        this.shareClassTypeId = shareClassTypeId;
    }

    public String getCurrencyTypeId() {
        return currencyTypeId;
    }

    public void setCurrencyTypeId(String currencyTypeId) {
        this.currencyTypeId = currencyTypeId;
    }

}
