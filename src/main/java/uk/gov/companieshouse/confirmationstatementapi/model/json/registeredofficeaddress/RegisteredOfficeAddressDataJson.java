package uk.gov.companieshouse.confirmationstatementapi.model.json.registeredofficeaddress;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.confirmationstatementapi.model.json.SectionDataJson;

public class RegisteredOfficeAddressDataJson extends SectionDataJson {

    @JsonProperty("registered_office_address")
    private RegisteredOfficeAddressJson registeredOfficeAddressJson;

    public RegisteredOfficeAddressJson getRegisteredOfficeAddressJson() {
        return registeredOfficeAddressJson;
    }

    public void setRegisteredOfficeAddressJson(RegisteredOfficeAddressJson registeredOfficeAddressJson) {
        this.registeredOfficeAddressJson = registeredOfficeAddressJson;
    }
}
