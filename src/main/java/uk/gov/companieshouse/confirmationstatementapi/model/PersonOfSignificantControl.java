package uk.gov.companieshouse.confirmationstatementapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.api.model.common.Address;

public class PersonOfSignificantControl {

    private String officerForename1;
    private String officerForename2;
    private String officerSurname;
    private String appointmentTypeId;
    private String appointmentDate;
    private String superSecurePscInd;
    private String officerNationality;
    private String usualResidentialCountry;
    private String officerDateOfBirth;
    private String suppliedCompanyName;
    private String natureOfControl;
    private String registerLocation;
    private String registrationNumber;
    private String lawGoverned;
    private String legalForm;
    private String countryOfResidence;
    @JsonProperty("service_address")
    private Address serviceAddress;
    private Address address;

    public String getOfficerForename1() {
        return officerForename1;
    }

    public void setOfficerForename1(String officerForename1) {
        this.officerForename1 = officerForename1;
    }

    public String getOfficerForename2() {
        return officerForename2;
    }

    public void setOfficerForename2(String officerForename2) {
        this.officerForename2 = officerForename2;
    }

    public String getOfficerSurname() {
        return officerSurname;
    }

    public void setOfficerSurname(String officerSurname) {
        this.officerSurname = officerSurname;
    }

    public String getAppointmentTypeId() {
        return appointmentTypeId;
    }

    public void setAppointmentTypeId(String appointmentTypeId) {
        this.appointmentTypeId = appointmentTypeId;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getSuperSecurePscInd() {
        return superSecurePscInd;
    }

    public void setSuperSecurePscInd(String superSecurePscInd) {
        this.superSecurePscInd = superSecurePscInd;
    }

    public String getOfficerNationality() {
        return officerNationality;
    }

    public void setOfficerNationality(String officerNationality) {
        this.officerNationality = officerNationality;
    }

    public String getUsualResidentialCountry() {
        return usualResidentialCountry;
    }

    public void setUsualResidentialCountry(String usualResidentialCountry) {
        this.usualResidentialCountry = usualResidentialCountry;
    }

    public String getOfficerDateOfBirth() {
        return officerDateOfBirth;
    }

    public void setOfficerDateOfBirth(String officerDateOfBirth) {
        this.officerDateOfBirth = officerDateOfBirth;
    }

    public String getSuppliedCompanyName() {
        return suppliedCompanyName;
    }

    public void setSuppliedCompanyName(String suppliedCompanyName) {
        this.suppliedCompanyName = suppliedCompanyName;
    }

    public String getNatureOfControl() {
        return natureOfControl;
    }

    public void setNatureOfControl(String natureOfControl) {
        this.natureOfControl = natureOfControl;
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

    public String getCountryOfResidence() {
        return countryOfResidence;
    }

    public void setCountryOfResidence(String countryOfResidence) {
        this.countryOfResidence = countryOfResidence;
    }

    public Address getServiceAddress() {
        return serviceAddress;
    }

    public void setServiceAddress(Address serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
