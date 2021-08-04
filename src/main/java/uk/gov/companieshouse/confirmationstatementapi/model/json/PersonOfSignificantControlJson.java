package uk.gov.companieshouse.confirmationstatementapi.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.api.model.psc.PscApi;

public class PersonOfSignificantControlJson extends PscApi {

    @JsonProperty("appointment_type")
    private String appointmentType;

    @JsonProperty("service_address_line_1")
    private String serviceAddressLine1;

    @JsonProperty("service_address_post_code")
    private String serviceAddressPostCode;

    @JsonProperty("service_address_post_town")
    private String serviceAddressPostTown;

    @JsonProperty("company_name")
    private String companyName;

    @JsonProperty("registration_number")
    private String registrationNumber;

    @JsonProperty("law_governed")
    private String lawGoverned;

    @JsonProperty("legal_form")
    private String legalForm;

    @JsonProperty("country_of_residence")
    private String countryOfResidence;

    public String getAppointmentType() {
        return appointmentType;
    }

    public void setAppointmentType(String appointmentType) {
        this.appointmentType = appointmentType;
    }

    public String getServiceAddressLine1() {
        return serviceAddressLine1;
    }

    public void setServiceAddressLine1(String serviceAddressLine1) {
        this.serviceAddressLine1 = serviceAddressLine1;
    }

    public String getServiceAddressPostCode() {
        return serviceAddressPostCode;
    }

    public void setServiceAddressPostCode(String serviceAddressPostCode) {
        this.serviceAddressPostCode = serviceAddressPostCode;
    }

    public String getServiceAddressPostTown() {
        return serviceAddressPostTown;
    }

    public void setServiceAddressPostTown(String serviceAddressPostTown) {
        this.serviceAddressPostTown = serviceAddressPostTown;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getLawGoverned() {
        return lawGoverned;
    }

    public void setLawGoverned(String lawGoverned) {
        this.lawGoverned = lawGoverned;
    }

    public String getLegalForm() {
        return legalForm;
    }

    public void setLegalForm(String legalForm) {
        this.legalForm = legalForm;
    }

    @Override
    public String getCountryOfResidence() {
        return countryOfResidence;
    }

    @Override
    public void setCountryOfResidence(String countryOfResidence) {
        this.countryOfResidence = countryOfResidence;
    }
}
