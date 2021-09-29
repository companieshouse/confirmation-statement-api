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

    @JsonProperty("service_address_po_box")
    private String serviceAddressPoBox;

    @JsonProperty("service_address_country_name")
    private String serviceAddressCountryName;

    @JsonProperty("service_address_care_of")
    private String serviceAddressCareOf;

    @JsonProperty("service_address_region")
    private String serviceAddressRegion;

    @JsonProperty("service_address_area")
    private String serviceAddressArea;

    @JsonProperty("service_address_house_name_number")
    private String serviceAddressHouseNameNumber;

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

    @JsonProperty("date_of_birth_iso")
    private String dateOfBirthIso;

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

    public String getDateOfBirthIso() {
        return dateOfBirthIso;
    }

    public void setDateOfBirthIso(String dateOfBirthIso) {
        this.dateOfBirthIso = dateOfBirthIso;
    }

    public String getServiceAddressPoBox() {
        return serviceAddressPoBox;
    }

    public void setServiceAddressPoBox(String serviceAddressPoBox) {
        this.serviceAddressPoBox = serviceAddressPoBox;
    }

    public String getServiceAddressCountryName() {
        return serviceAddressCountryName;
    }

    public void setServiceAddressCountryName(String serviceAddressCountryName) {
        this.serviceAddressCountryName = serviceAddressCountryName;
    }

    public String getServiceAddressCareOf() {
        return serviceAddressCareOf;
    }

    public void setServiceAddressCareOf(String serviceAddressCareOf) {
        this.serviceAddressCareOf = serviceAddressCareOf;
    }

    public String getServiceAddressRegion() {
        return serviceAddressRegion;
    }

    public void setServiceAddressRegion(String serviceAddressRegion) {
        this.serviceAddressRegion = serviceAddressRegion;
    }

    public String getServiceAddressArea() {
        return serviceAddressArea;
    }

    public void setServiceAddressArea(String serviceAddressArea) {
        this.serviceAddressArea = serviceAddressArea;
    }

    public String getServiceAddressHouseNameNumber() {
        return serviceAddressHouseNameNumber;
    }

    public void setServiceAddressHouseNameNumber(String serviceAddressHouseNameNumber) {
        this.serviceAddressHouseNameNumber = serviceAddressHouseNameNumber;
    }
}
