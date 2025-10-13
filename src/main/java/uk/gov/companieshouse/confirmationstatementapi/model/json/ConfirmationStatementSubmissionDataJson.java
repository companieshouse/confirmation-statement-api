package uk.gov.companieshouse.confirmationstatementapi.model.json;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import uk.gov.companieshouse.confirmationstatementapi.model.json.activedirectordetails.ActiveOfficerDetailsDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.personsignificantcontrol.PersonsSignificantControlDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.registeredemailaddress.RegisteredEmailAddressDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.registeredofficeaddress.RegisteredOfficeAddressDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.registerlocation.RegisterLocationsDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.shareholder.ShareholderDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.siccode.SicCodeDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.siccode.SicCodeJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.statementofcapital.StatementOfCapitalDataJson;

public class ConfirmationStatementSubmissionDataJson {

    @JsonProperty("statement_of_capital_data")
    private StatementOfCapitalDataJson statementOfCapitalData;

    @JsonProperty("persons_significant_control_data")
    private PersonsSignificantControlDataJson personsSignificantControlData;

    @JsonProperty("sic_code_data")
    private List<SicCodeJson> sicCodeData;

    @JsonProperty("registered_office_address_data")
    private RegisteredOfficeAddressDataJson registeredOfficeAddressData;

    @JsonProperty("registered_email_address_data")
    private RegisteredEmailAddressDataJson registeredEmailAddressData;

    @JsonProperty("active_officer_details_data")
    private ActiveOfficerDetailsDataJson activeOfficerDetailsData;

    @JsonProperty("shareholder_data")
    private ShareholderDataJson shareholderData;

    @JsonProperty("register_locations_data")
    private RegisterLocationsDataJson registerLocationsData;

    @JsonProperty("confirmation_statement_made_up_to_date")
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate madeUpToDate;

    @JsonProperty("trading_status_data")
    private TradingStatusDataJson tradingStatusData;

    @JsonProperty("accept_lawful_purpose_statement")
    private Boolean acceptLawfulPurposeStatement;

    @JsonProperty("new_confirmation_date")
    private String newConfirmationDate;

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

    public List<SicCodeJson> getSicCodeData() {
        return sicCodeData;
    }

    public void setSicCodeData(List<SicCodeJson> sicCodeData) {
        this.sicCodeData = sicCodeData;
    }

    public RegisteredOfficeAddressDataJson getRegisteredOfficeAddressData() {
        return registeredOfficeAddressData;
    }

    public void setRegisteredOfficeAddressData(RegisteredOfficeAddressDataJson registeredOfficeAddressDataJson) {
        this.registeredOfficeAddressData = registeredOfficeAddressDataJson;
    }

    public RegisteredEmailAddressDataJson getRegisteredEmailAddressData() {
        return registeredEmailAddressData;
    }

    public void setRegisteredEmailAddressData(RegisteredEmailAddressDataJson registeredEmailAddressData) {
        this.registeredEmailAddressData = registeredEmailAddressData;
    }

    public ActiveOfficerDetailsDataJson getActiveOfficerDetailsData() {
        return activeOfficerDetailsData;
    }

    public void setActiveOfficerDetailsData(ActiveOfficerDetailsDataJson activeOfficerDetailsData) {
        this.activeOfficerDetailsData = activeOfficerDetailsData;
    }

    public ShareholderDataJson getShareholderData() {
        return shareholderData;
    }

    public void setShareholderData(ShareholderDataJson shareholderData) {
        this.shareholderData = shareholderData;
    }

    public RegisterLocationsDataJson getRegisterLocationsData() {
        return registerLocationsData;
    }

    public void setRegisterLocationsData(RegisterLocationsDataJson registerLocationsData) {
        this.registerLocationsData = registerLocationsData;
    }

    public LocalDate getMadeUpToDate() {
        return madeUpToDate;
    }

    public void setMadeUpToDate(LocalDate madeUpToDate) {
        this.madeUpToDate = madeUpToDate;
    }

    public TradingStatusDataJson getTradingStatusData() {
        return tradingStatusData;
    }

    public void setTradingStatusData(TradingStatusDataJson tradingStatusData) {
        this.tradingStatusData = tradingStatusData;
    }

    public Boolean getAcceptLawfulPurposeStatement() {
        return acceptLawfulPurposeStatement;
    }

    public void setAcceptLawfulPurposeStatement(Boolean acceptLawfulPurposeStatement) {
        this.acceptLawfulPurposeStatement = acceptLawfulPurposeStatement;
    }

    public String getNewConfirmationDate() {
        return newConfirmationDate;
    }

    public void setNewConfirmationDate(String newConfirmationDate) {
        this.newConfirmationDate = newConfirmationDate;
    }
}
