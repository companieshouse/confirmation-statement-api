package uk.gov.companieshouse.confirmationstatementapi.model.json.registeredemailaddress;

import com.fasterxml.jackson.annotation.JsonProperty;

import uk.gov.companieshouse.confirmationstatementapi.model.json.SectionDataJson;

public class RegisteredEmailAddressDataJson extends SectionDataJson {

    @JsonProperty("registered_email_address")
    private String registeredEmailAddress;

    public String getRegisteredEmailAddress() {
        return registeredEmailAddress;
    }

    public void setRegisteredEmailAddress(String registeredEmailAddress) {
        this.registeredEmailAddress = registeredEmailAddress;
    }

}
