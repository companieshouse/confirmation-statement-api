package uk.gov.companieshouse.confirmationstatementapi.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.confirmationstatementapi.model.json.activedirectordetails.ActiveDirectorDetailsDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.personsignificantcontrol.PersonsSignificantControlDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.registerlocation.RegisterLocationsDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.shareholder.ShareholderDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.siccode.SicCodeDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.registeredofficeaddress.RegisteredOfficeAddressDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.statementofcapital.StatementOfCapitalDataJson;

public class ConfirmationStatementSubmissionDataJson {

    @JsonProperty("statement_of_capital_data")
    private StatementOfCapitalDataJson statementOfCapitalData;

    @JsonProperty("persons_significant_control_data")
    private PersonsSignificantControlDataJson personsSignificantControlData;

    @JsonProperty("sic_code_data")
    private SicCodeDataJson sicCodeData;

    @JsonProperty("registered_office_address_data")
    private RegisteredOfficeAddressDataJson registeredOfficeAddressData;

    @JsonProperty("active_director_details_data")
    private ActiveDirectorDetailsDataJson activeDirectorDetailsData;

    @JsonProperty("shareholder_data")
    private ShareholderDataJson shareholderData;

    @JsonProperty("register_locations_data")
    private RegisterLocationsDataJson registerLocationsData;

    public StatementOfCapitalDataJson getStatementOfCapitalData() {
        return statementOfCapitalData;
    }

    public void setStatementOfCapitalData(StatementOfCapitalDataJson statementOfCapitalData) {
        this.statementOfCapitalData = statementOfCapitalData;
    }

    public PersonsSignificantControlDataJson getPersonsSignificantControlData() {
        return personsSignificantControlData;
    }

    public void setPersonsSignificantControlData(PersonsSignificantControlDataJson personsSignificantControlData) {
        this.personsSignificantControlData = personsSignificantControlData;
    }

    public SicCodeDataJson getSicCodeData() {
        return sicCodeData;
    }

    public void setSicCodeData(SicCodeDataJson sicCodeData) {
        this.sicCodeData = sicCodeData;
    }

    public RegisteredOfficeAddressDataJson getRegisteredOfficeAddressData() {
        return registeredOfficeAddressData;
    }

    public void setRegisteredOfficeAddressData(RegisteredOfficeAddressDataJson registeredOfficeAddressDataJson) {
        this.registeredOfficeAddressData = registeredOfficeAddressDataJson;
    }

    public ActiveDirectorDetailsDataJson getActiveDirectorDetailsData() {
        return activeDirectorDetailsData;
    }

    public void setActiveDirectorDetailsData(ActiveDirectorDetailsDataJson activeDirectorDetailsData) {
        this.activeDirectorDetailsData = activeDirectorDetailsData;
    }

    public ShareholderDataJson getShareholdersData() {
        return shareholderData;
    }

    public void setShareholdersData(ShareholderDataJson shareholderData) {
        this.shareholderData = shareholderData;
    }

    public RegisterLocationsDataJson getRegisterLocationsData() {
        return registerLocationsData;
    }

    public void setRegisterLocationsData(RegisterLocationsDataJson registerLocationsData) {
        this.registerLocationsData = registerLocationsData;
    }
}
