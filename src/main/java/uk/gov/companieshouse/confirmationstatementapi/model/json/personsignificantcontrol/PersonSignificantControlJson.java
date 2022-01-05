package uk.gov.companieshouse.confirmationstatementapi.model.json.personsignificantcontrol;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.Set;

public class PersonSignificantControlJson {

    @JsonProperty("name")
    private String name;
    @JsonProperty("nationality")
    private String nationality;
    @JsonProperty("country_of_residence")
    private String countryOfResidence;
    @JsonProperty("full_date_of_birth")
    private LocalDate fullDateOfBirth;
    @JsonProperty("usual_residential_address")
    private String usualResidentialAddress;
    @JsonProperty("correspondence_address")
    private String correspondenceAddress;
    @JsonProperty("natures_of_control")
    private Set<String> naturesOfControl;
    @JsonProperty("psc_country")
    private String pscCountry;

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

    public String getPscCountry() {
        return pscCountry;
    }

    public void setPscCountry(String pscCountry) {
        this.pscCountry = pscCountry;
    }
}
