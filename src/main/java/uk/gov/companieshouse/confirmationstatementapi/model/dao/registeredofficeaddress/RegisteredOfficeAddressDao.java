package uk.gov.companieshouse.confirmationstatementapi.model.dao.registeredofficeaddress;

import org.springframework.data.mongodb.core.mapping.Field;

public class RegisteredOfficeAddressDao {

    @Field("address_line_one")
    private String addressLineOne;

    @Field("address_line_twp")
    private String addressLineTwo;

    @Field("care_of")
    private String careOf;

    @Field("country")
    private String country;

    @Field("locality")
    private String locality;

    @Field("poBox")
    private String poBox;

    @Field("postal_code")
    private String postalCode;

    @Field("premises")
    private String premises;

    @Field("region")
    private String region;

    public String getAddressLineOne() {
        return addressLineOne;
    }

    public void setAddressLineOne(String addressLineOne) {
        this.addressLineOne = addressLineOne;
    }

    public String getAddressLineTwo() {
        return addressLineTwo;
    }

    public void setAddressLineTwo(String addressLineTwo) {
        this.addressLineTwo = addressLineTwo;
    }

    public String getCareOf() {
        return careOf;
    }

    public void setCareOf(String careOf) {
        this.careOf = careOf;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getPoBox() {
        return poBox;
    }

    public void setPoBox(String poBox) {
        this.poBox = poBox;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPremises() {
        return premises;
    }

    public void setPremises(String premises) {
        this.premises = premises;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

}
