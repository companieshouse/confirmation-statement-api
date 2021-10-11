package uk.gov.companieshouse.confirmationstatementapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.api.model.common.Address;

public class ActiveDirectorDetails {
    @JsonProperty("fore_name_1")
    private String foreName1;
    @JsonProperty("fore_name_2")
    private String foreName2;
    private String surname;
    private String occupation;
    private String nationality;
    @JsonProperty("date_of_birth")
    private String dateOfBirth;
    @JsonProperty("date_of_appointment")
    private String dateOfAppointment;
    @JsonProperty("service_address")
    private Address serviceAddress;
    @JsonProperty("residential_address")
    private Address residentialAddress;

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

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Address getServiceAddress() {
        return serviceAddress;
    }

    public void setServiceAddress(Address serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public Address getResidentialAddress() {
        return residentialAddress;
    }

    public void setResidentialAddress(Address residentialAddress) {
        this.residentialAddress = residentialAddress;
    }

    public String getDateOfAppointment() {
        return dateOfAppointment;
    }

    public void setDateOfAppointment(String dateOfAppointment) {
        this.dateOfAppointment = dateOfAppointment;
    }
}
