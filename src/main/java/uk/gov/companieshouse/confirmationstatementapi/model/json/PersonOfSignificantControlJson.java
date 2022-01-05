package uk.gov.companieshouse.confirmationstatementapi.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.api.model.common.Address;
import uk.gov.companieshouse.api.model.psc.PscApi;

public class PersonOfSignificantControlJson extends PscApi {

    @JsonProperty("appointment_type")
    private String appointmentType;

    @JsonProperty("appointment_date")
    private String appointmentDate;

    @JsonProperty("company_name")
    private String companyName;

    @JsonProperty("register_location")
    private String registerLocation;

    @JsonProperty("registration_number")
    private String registrationNumber;

    @JsonProperty("law_governed")
    private String lawGoverned;

    @JsonProperty("legal_form")
    private String legalForm;

    @JsonProperty("psc_country")
    private String pscCountry;

    @JsonProperty("date_of_birth_iso")
    private String dateOfBirthIso;

    @JsonProperty("service_address")
    private Address serviceAddress;

    public String getAppointmentType() {
        return appointmentType;
    }

    public void setAppointmentType(String appointmentType) {
        this.appointmentType = appointmentType;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getRegisterLocation() {
        return registerLocation;
    }

    public void setRegisterLocation(String registerLocation) {
        this.registerLocation = registerLocation;
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

    public String getPscCountry() {
        return pscCountry;
    }

    public void setPscCountry(String pscCountry) {
        this.pscCountry = pscCountry;
    }

    public String getDateOfBirthIso() {
        return dateOfBirthIso;
    }

    public void setDateOfBirthIso(String dateOfBirthIso) {
        this.dateOfBirthIso = dateOfBirthIso;
    }

    public Address getServiceAddress() {
        return serviceAddress;
    }

    public void setServiceAddress(Address serviceAddress) {
        this.serviceAddress = serviceAddress;
    }
}
