package uk.gov.companieshouse.confirmationstatementapi.model.json.registerlocation;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.api.model.common.Address;

public class RegisterLocationJson {

    @JsonProperty("register_type_desc")
    private String registerTypeDesc;

    @JsonProperty("sail_address")
    private Address sailAddress;

    public String getRegisterTypeDesc() {
        return registerTypeDesc;
    }

    public void setRegisterTypeDesc(String registerTypeDesc) {
        this.registerTypeDesc = registerTypeDesc;
    }

    public Address getSailAddress() {
        return sailAddress;
    }

    public void setSailAddress(Address sailAddress) {
        this.sailAddress = sailAddress;
    }
}
