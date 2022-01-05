package uk.gov.companieshouse.confirmationstatementapi.model.dao.personsignificantcontrol;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.util.Set;

public class PersonSignificantControlDao {

    @Field("name")
    private String name;
    @Field("nationality")
    private String nationality;
    @Field("country_of_residence")
    private String countryOfResidence;
    @Field("full_date_of_birth")
    private LocalDate fullDateOfBirth;
    @Field("usual_residential_address")
    private String usualResidentialAddress;
    @Field("correspondence_address")
    private String correspondenceAddress;
    @Field("natures_of_control")
    private Set<String> naturesOfControl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getCountryOfResidence() {
        return countryOfResidence;
    }

    public void setCountryOfResidence(String countryOfResidence) {
        this.countryOfResidence = countryOfResidence;
    }

    public LocalDate getFullDateOfBirth() {
        return fullDateOfBirth;
    }

    public void setFullDateOfBirth(LocalDate fullDateOfBirth) {
        this.fullDateOfBirth = fullDateOfBirth;
    }

    public String getUsualResidentialAddress() {
        return usualResidentialAddress;
    }

    public void setUsualResidentialAddress(String usualResidentialAddress) {
        this.usualResidentialAddress = usualResidentialAddress;
    }

    public String getCorrespondenceAddress() {
        return correspondenceAddress;
    }

    public void setCorrespondenceAddress(String correspondenceAddress) {
        this.correspondenceAddress = correspondenceAddress;
    }

    public Set<String> getNaturesOfControl() {
        return naturesOfControl;
    }

    public void setNaturesOfControl(Set<String> naturesOfControl) {
        this.naturesOfControl = naturesOfControl;
    }
}
