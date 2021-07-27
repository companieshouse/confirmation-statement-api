package uk.gov.companieshouse.confirmationstatementapi.model.dao.personsignificantcontrol;

import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.SectionDataDao;

import java.util.Set;

public class PersonsSignificantControlDataDao extends SectionDataDao {

    @Field("persons_significant_control")
    private Set<PersonSignificantControlDao> personsSignificantControl;

    public Set<PersonSignificantControlDao> getPersonsSignificantControl() {
        return personsSignificantControl;
    }

    public void setPersonsSignificantControl(Set<PersonSignificantControlDao> personsSignificantControl) {
        this.personsSignificantControl = personsSignificantControl;
    }
}
