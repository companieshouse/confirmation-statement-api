package uk.gov.companieshouse.confirmationstatementapi.model.dao.registeredofficeaddress;

import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.SectionDataDao;

public class RegisteredOfficeAddressDataDao extends SectionDataDao {

    @Field("registered_office_address")
    private RegisteredOfficeAddressDao registeredOfficeAddressDao;

    public RegisteredOfficeAddressDao getRegisteredOfficeAddressDao() {
        return registeredOfficeAddressDao;
    }

    public void setRegisteredOfficeAddressDao(RegisteredOfficeAddressDao registeredOfficeAddressDao) {
        this.registeredOfficeAddressDao = registeredOfficeAddressDao;
    }
}
