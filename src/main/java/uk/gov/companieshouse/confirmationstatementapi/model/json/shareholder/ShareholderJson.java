package uk.gov.companieshouse.confirmationstatementapi.model.json.shareholder;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ShareholderJson {
    @JsonProperty("fore_name_1")
    private String foreName1;

    @JsonProperty("fore_name_2")
    private String foreName2;

    @JsonProperty("surname")
    private String surname;

    @JsonProperty("shares")
    private String shares;

    @JsonProperty("share_class_type_id")
    private String shareClassTypeId;

    @JsonProperty("currency_type_id")
    private String currencyTypeId;

    public String getForeName1() {
        return foreName1;
    }

    public void setForeName1(String foreName1) {
        this.foreName1 = foreName1;
    }

    public String getForeName2() {
        return foreName2;
    }

    public void setForeName2(String foreName2) {
        this.foreName2 = foreName2;
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
