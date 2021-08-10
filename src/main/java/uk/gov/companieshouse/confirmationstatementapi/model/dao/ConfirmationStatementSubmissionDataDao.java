package uk.gov.companieshouse.confirmationstatementapi.model.dao;

import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.activedirectordetails.ActiveDirectorDetailsDataDao;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.personsignificantcontrol.PersonsSignificantControlDataDao;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.siccode.SicCodeDataDao;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.registeredofficeaddress.RegisteredOfficeAddressDataDao;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.statementofcapital.StatementOfCapitalDataDao;

public class ConfirmationStatementSubmissionDataDao {

    @Field("statement_of_capital_data")
    private StatementOfCapitalDataDao statementOfCapitalData;

    @Field("persons_significant_control_data")
    private PersonsSignificantControlDataDao personsSignificantControlData;

    @Field("sic_code_data")
    private SicCodeDataDao sicCodeData;

    @Field("registered_office_address_data")
    private RegisteredOfficeAddressDataDao registeredOfficeAddressData;

    @Field("active_director_details_data")
    private ActiveDirectorDetailsDataDao activeDirectorDetailsData;

    public StatementOfCapitalDataDao getStatementOfCapitalData() {
        return statementOfCapitalData;
    }

    public void setStatementOfCapitalData(StatementOfCapitalDataDao statementOfCapitalData) {
        this.statementOfCapitalData = statementOfCapitalData;
    }

    public PersonsSignificantControlDataDao getPersonsSignificantControlData() {
        return personsSignificantControlData;
    }

    public void setPersonsSignificantControlData(PersonsSignificantControlDataDao personsSignificantControlData) {
        this.personsSignificantControlData = personsSignificantControlData;
    }

    public SicCodeDataDao getSicCodeData() {
        return sicCodeData;
    }

    public void setSicCodeData(SicCodeDataDao sicCodeData) {
        this.sicCodeData = sicCodeData;
    }

    public RegisteredOfficeAddressDataDao getRegisteredOfficeAddressData() {
        return registeredOfficeAddressData;
    }

    public void setRegisteredOfficeAddressData(RegisteredOfficeAddressDataDao registeredOfficeAddressDataDao) {
        this.registeredOfficeAddressData = registeredOfficeAddressDataDao;
    }

    public ActiveDirectorDetailsDataDao getActiveDirectorDetailsData() {
        return activeDirectorDetailsData;
    }

    public void setActiveDirectorDetailsData(ActiveDirectorDetailsDataDao activeDirectorDetailsDataDao) {
        this.activeDirectorDetailsData = activeDirectorDetailsDataDao;
    }
}
