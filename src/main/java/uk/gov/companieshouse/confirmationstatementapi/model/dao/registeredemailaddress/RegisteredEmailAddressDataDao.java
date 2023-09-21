package uk.gov.companieshouse.confirmationstatementapi.model.dao.registeredemailaddress;

import org.springframework.data.mongodb.core.mapping.Field;

import uk.gov.companieshouse.confirmationstatementapi.model.dao.SectionDataDao;

public class RegisteredEmailAddressDataDao extends SectionDataDao {

    @Field("registered_email_address")
    private String registeredEmailAddress;

    public String getRegisteredEmailAddress() {
        return registeredEmailAddress;
    }

    public void setRegisteredEmailAddress(String registeredEmailAddress) {
        this.registeredEmailAddress = registeredEmailAddress;
    }


}
