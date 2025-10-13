package uk.gov.companieshouse.confirmationstatementapi.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import uk.gov.companieshouse.api.model.company.StatementOfCapitalJson;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.ConfirmationStatementSubmissionDataDao;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.TradingStatusDataDao;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.activedirectordetails.ActiveOfficerDetailsDataDao;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.personsignificantcontrol.PersonSignificantControlDao;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.personsignificantcontrol.PersonsSignificantControlDataDao;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.registeredemailaddress.RegisteredEmailAddressDataDao;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.registeredofficeaddress.RegisteredOfficeAddressDataDao;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.registerlocation.RegisterLocationsDataDao;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.shareholder.ShareholderDataDao;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.siccode.SicCodeDao;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.siccode.SicCodeDataDao;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.statementofcapital.StatementOfCapitalDao;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.statementofcapital.StatementOfCapitalDataDao;
import uk.gov.companieshouse.confirmationstatementapi.model.json.ConfirmationStatementSubmissionDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.TradingStatusDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.activedirectordetails.ActiveOfficerDetailsDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.personsignificantcontrol.PersonSignificantControlJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.personsignificantcontrol.PersonsSignificantControlDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.registeredemailaddress.RegisteredEmailAddressDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.registeredofficeaddress.RegisteredOfficeAddressDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.registerlocation.RegisterLocationsDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.shareholder.ShareholderDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.siccode.SicCodeDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.siccode.SicCodeJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.statementofcapital.StatementOfCapitalDataJson;

public class MockConfirmationStatementSubmissionData {

    public static ConfirmationStatementSubmissionDataJson getMockJsonData() {
        ConfirmationStatementSubmissionDataJson data = new ConfirmationStatementSubmissionDataJson();
        data.setStatementOfCapitalData(getStatementOfCapitalJsonData());
        data.setPersonsSignificantControlData(getPersonsSignificantControlJsonData());
        //data.setSicCodeData(getSicCodeJsonData());
        data.setRegisteredOfficeAddressData(getRegisteredOfficeAddressJsonData());
        data.setRegisteredEmailAddressData(getRegisteredEmailAddressJsonData());
        data.setActiveOfficerDetailsData(getActiveOfficerDetailsJsonData());
        data.setShareholderData(getShareholdersJsonData());
        data.setRegisterLocationsData(getRegisterLocationsData());
        data.setTradingStatusData(getTradingStatusJsonData());
        data.setMadeUpToDate(LocalDate.of(2021, 9, 12));
        data.setAcceptLawfulPurposeStatement(true);
        return data;
    }

    private static StatementOfCapitalDataJson getStatementOfCapitalJsonData() {
        var statementOfCapitalData = new StatementOfCapitalDataJson();
        statementOfCapitalData.setSectionStatus(SectionStatus.NOT_CONFIRMED);
        var statementOfCapitalJson = new StatementOfCapitalJson();
        statementOfCapitalJson.setClassOfShares("ORDINARY");
        statementOfCapitalJson.setCurrency("GBP");
        statementOfCapitalJson.setNumberAllotted("100");
        statementOfCapitalJson.setAggregateNominalValue("0.01");
        statementOfCapitalJson.setPrescribedParticulars("THE QUICK BROWN FOX");
        statementOfCapitalJson.setTotalNumberOfShares("100");
        statementOfCapitalJson.setTotalAggregateNominalValue("1");
        statementOfCapitalJson.setTotalAmountUnpaidForCurrency("2");
        statementOfCapitalData.setStatementOfCapital(statementOfCapitalJson);
        return statementOfCapitalData;
    }

    private static PersonsSignificantControlDataJson getPersonsSignificantControlJsonData() {
        var personsSignificantControlDataJson = new PersonsSignificantControlDataJson();
        personsSignificantControlDataJson.setSectionStatus(SectionStatus.NOT_CONFIRMED);

        var personSignificantControl = new PersonSignificantControlJson();
        personSignificantControl.setName("FOO");
        personSignificantControl.setNationality("NATION");
        personSignificantControl.setNaturesOfControl(Collections.singleton("NATURE_CONTROL"));
        personSignificantControl.setFullDateOfBirth(LocalDate.of(1995, 10, 1));
        var personsSignificantControlJson = Collections.singleton(personSignificantControl);

        personsSignificantControlDataJson.setPersonsSignificantControl(personsSignificantControlJson);

        return personsSignificantControlDataJson;
    }

    private static List<SicCodeDataJson> getSicCodeJsonData() {
        SicCodeDataJson sicCodeDataJson = new SicCodeDataJson();
        sicCodeDataJson.setSectionStatus(SectionStatus.NOT_CONFIRMED);

        SicCodeJson sicCode = new SicCodeJson();
        sicCode.setCode("123");
        sicCode.setDescription("TEST SIC CODE DETAILS");

        List<SicCodeJson> sicCodeList = new ArrayList<>();
        sicCodeList.add(sicCode);
        sicCodeDataJson.setSicCode(sicCodeList);

        List<SicCodeDataJson> result = new ArrayList<>();
        result.add(sicCodeDataJson);

        return result;
    }

    private static RegisteredOfficeAddressDataJson getRegisteredOfficeAddressJsonData() {
        var registeredOfficeAddressData = new RegisteredOfficeAddressDataJson();
        registeredOfficeAddressData.setSectionStatus(SectionStatus.NOT_CONFIRMED);

        return registeredOfficeAddressData;
    }

    private static RegisteredEmailAddressDataJson getRegisteredEmailAddressJsonData() {
        var registeredEmailAddressData = new RegisteredEmailAddressDataJson();
        registeredEmailAddressData.setSectionStatus(SectionStatus.NOT_CONFIRMED);

        return registeredEmailAddressData;
    }

    private static ActiveOfficerDetailsDataJson getActiveOfficerDetailsJsonData() {
        var activeDirectorDetailsData = new ActiveOfficerDetailsDataJson();
        activeDirectorDetailsData.setSectionStatus(SectionStatus.NOT_CONFIRMED);

        return activeDirectorDetailsData;
    }

    private static ShareholderDataJson getShareholdersJsonData() {
        var shareholderData = new ShareholderDataJson();
        shareholderData.setSectionStatus(SectionStatus.NOT_CONFIRMED);

        return shareholderData;
    }

    private static RegisterLocationsDataJson getRegisterLocationsData() {
        var registerLocationsData = new RegisterLocationsDataJson();
        registerLocationsData.setSectionStatus(SectionStatus.NOT_CONFIRMED);

        return registerLocationsData;
    }

    private static TradingStatusDataJson getTradingStatusJsonData() {
        var tradingStatusData = new TradingStatusDataJson();
        tradingStatusData.setTradingStatusAnswer(true);

        return tradingStatusData;
    }

    static ConfirmationStatementSubmissionDataDao getMockDaoData() {
        ConfirmationStatementSubmissionDataDao data = new ConfirmationStatementSubmissionDataDao();
        data.setStatementOfCapitalData(getStatementOfCapitalDaoData());
        data.setPersonsSignificantControlData(getPersonsSignificantControlDaoData());
        data.setSicCodeData(getSicCodeDaoData());
        data.setRegisteredOfficeAddressData(getRegisteredOfficeAddressDaoData());
        data.setRegisteredEmailAddressData(getRegisteredEmailAddressDaoData());
        data.setActiveOfficerDetailsData(getActiveOfficerDetailsDaoData());
        data.setShareholderData(getShareholdersDaoData());
        data.setRegisterLocationsData(getRegisterLocationsDaoData());
        data.setMadeUpToDate(LocalDate.of(2021, 5, 12));
        data.setTradingStatusData(getTradingStatusDaoData());
        data.setAcceptLawfulPurposeStatement(true);
        return data;
    }

    private static StatementOfCapitalDataDao getStatementOfCapitalDaoData() {
        var statementOfCapitalData = new StatementOfCapitalDataDao();
        statementOfCapitalData.setSectionStatus(SectionStatus.NOT_CONFIRMED);
        var statementOfCapital = new StatementOfCapitalDao();
        statementOfCapital.setClassOfShares("ORDINARY");
        statementOfCapital.setCurrency("GBP");
        statementOfCapital.setNumberAllotted("100");
        statementOfCapital.setAggregateNominalValue("0.01");
        statementOfCapital.setPrescribedParticulars("THE QUICK BROWN FOX");
        statementOfCapital.setTotalNumberOfShares("100");
        statementOfCapital.setTotalAggregateNominalValue("1");
        statementOfCapital.setTotalAmountUnpaidForCurrency("2");
        statementOfCapitalData.setStatementOfCapital(statementOfCapital);
        return statementOfCapitalData;
    }

    private static PersonsSignificantControlDataDao getPersonsSignificantControlDaoData() {
        var personsSignificantControlDataDao = new PersonsSignificantControlDataDao();
        personsSignificantControlDataDao.setSectionStatus(SectionStatus.NOT_CONFIRMED);

        var personSignificantControl = new PersonSignificantControlDao();
        personSignificantControl.setName("FOO");
        personSignificantControl.setNationality("NATION");
        personSignificantControl.setNaturesOfControl(Collections.singleton("NATURE_CONTROL"));
        personSignificantControl.setFullDateOfBirth(LocalDate.of(1995, 10, 1));
        var personsSignificantControlDao = Collections.singleton(personSignificantControl);

        personsSignificantControlDataDao.setPersonsSignificantControl(personsSignificantControlDao);

        return personsSignificantControlDataDao;
    }

    private static List<SicCodeDataDao> getSicCodeDaoData() {
        SicCodeDataDao sicCodeDataDao = new SicCodeDataDao();
        sicCodeDataDao.setSectionStatus(SectionStatus.NOT_CONFIRMED);

        SicCodeDao sicCode = new SicCodeDao();
        sicCode.setCode("123");
        sicCode.setDescription("TEST SIC CODE DETAILS");

        List<SicCodeDao> sicCodeList = new ArrayList<>();
        sicCodeList.add(sicCode);
        sicCodeDataDao.setSicCode(sicCodeList);

        List<SicCodeDataDao> result = new ArrayList<>();
        result.add(sicCodeDataDao);

        return result;
    }

    private static RegisteredOfficeAddressDataDao getRegisteredOfficeAddressDaoData() {
        var registeredOfficeAddressData = new RegisteredOfficeAddressDataDao();
        registeredOfficeAddressData.setSectionStatus(SectionStatus.NOT_CONFIRMED);

        return registeredOfficeAddressData;
    }

    private static RegisteredEmailAddressDataDao getRegisteredEmailAddressDaoData() {
        var registeredEmailAddressData = new RegisteredEmailAddressDataDao();
        registeredEmailAddressData.setSectionStatus(SectionStatus.NOT_CONFIRMED);

        return registeredEmailAddressData;
    }

    private static ActiveOfficerDetailsDataDao getActiveOfficerDetailsDaoData() {
        var activeOfficerDetailsData = new ActiveOfficerDetailsDataDao();
        activeOfficerDetailsData.setSectionStatus(SectionStatus.NOT_CONFIRMED);

        return activeOfficerDetailsData;
    }

    private static ShareholderDataDao getShareholdersDaoData() {
        var shareholderData = new ShareholderDataDao();
        shareholderData.setSectionStatus(SectionStatus.NOT_CONFIRMED);

        return shareholderData;
    }

    private static RegisterLocationsDataDao getRegisterLocationsDaoData() {
        var registerLocationsData = new RegisterLocationsDataDao();
        registerLocationsData.setSectionStatus(SectionStatus.NOT_CONFIRMED);

        return registerLocationsData;
    }

    private static TradingStatusDataDao getTradingStatusDaoData() {
        var tradingStatusData = new TradingStatusDataDao();
        tradingStatusData.setTradingStatusAnswer(false);

        return tradingStatusData;
    }
}
