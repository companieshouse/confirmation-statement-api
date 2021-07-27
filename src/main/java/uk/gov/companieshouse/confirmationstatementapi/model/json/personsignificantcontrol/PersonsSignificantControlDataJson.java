package uk.gov.companieshouse.confirmationstatementapi.model.json.personsignificantcontrol;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.confirmationstatementapi.model.json.SectionDataJson;

import java.util.Set;

public class PersonsSignificantControlDataJson extends SectionDataJson {

    @JsonProperty("persons_significant_control")
    private Set<PersonSignificantControlJson> personsSignificantControl;

    public Set<PersonSignificantControlJson> getPersonsSignificantControl() {
        return personsSignificantControl;
    }

    public void setPersonsSignificantControl(Set<PersonSignificantControlJson> personsSignificantControl) {
        this.personsSignificantControl = personsSignificantControl;
    }
}
