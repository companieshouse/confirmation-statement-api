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
    private Long shares;

    @JsonProperty("share_class_type_id")
    private Long shareClassTypeId;

    @JsonProperty("currency_type_id")
    private Long currencyTypeId;

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

    public Long getShares() {
        return shares;
    }

    public void setShares(Long shares) {
        this.shares = shares;
    }

    public Long getShareClassTypeId() {
        return shareClassTypeId;
    }

    public void setShareClassTypeId(Long shareClassTypeId) {
        this.shareClassTypeId = shareClassTypeId;
    }

    public Long getCurrencyTypeId() {
        return currencyTypeId;
    }

    public void setCurrencyTypeId(Long currencyTypeId) {
        this.currencyTypeId = currencyTypeId;
    }

}
