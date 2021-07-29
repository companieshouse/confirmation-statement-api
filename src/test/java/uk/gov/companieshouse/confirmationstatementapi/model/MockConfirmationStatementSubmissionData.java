package uk.gov.companieshouse.confirmationstatementapi.model;

import uk.gov.companieshouse.confirmationstatementapi.model.dao.ConfirmationStatementSubmissionDataDao;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.personsignificantcontrol.PersonSignificantControlDao;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.personsignificantcontrol.PersonsSignificantControlDataDao;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.registeredofficeaddress.RegisteredOfficeAddressDao;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.registeredofficeaddress.RegisteredOfficeAddressDataDao;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.statementofcapital.StatementOfCapitalDao;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.statementofcapital.StatementOfCapitalDataDao;
import uk.gov.companieshouse.confirmationstatementapi.model.json.ConfirmationStatementSubmissionDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.personsignificantcontrol.PersonSignificantControlJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.personsignificantcontrol.PersonsSignificantControlDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.registeredofficeaddress.RegisteredOfficeAddressDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.registeredofficeaddress.RegisteredOfficeAddressJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.statementofcapital.StatementOfCapitalDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.statementofcapital.StatementOfCapitalJson;

import java.time.LocalDate;
import java.util.Collections;

public class MockConfirmationStatementSubmissionData {

    public static ConfirmationStatementSubmissionDataJson getMockJsonData() {
        ConfirmationStatementSubmissionDataJson data = new ConfirmationStatementSubmissionDataJson();
        data.setStatementOfCapitalData(getStatementOfCapitalJsonData());
        data.setPersonsSignificantControlData(getPersonsSignificantControlJsonData());
        data.setRegisteredOfficeAddressData(getRegisteredOfficeAddressJsonData());
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

    private static RegisteredOfficeAddressDataJson getRegisteredOfficeAddressJsonData() {
        var registeredOfficeAddressData = new RegisteredOfficeAddressDataJson();
        registeredOfficeAddressData.setSectionStatus(SectionStatus.NOT_CONFIRMED);

        var registeredOfficeAddress = new RegisteredOfficeAddressJson();
        registeredOfficeAddress.setAddressLineOne("line1");
        registeredOfficeAddress.setAddressLineTwo("line2");
        registeredOfficeAddress.setCareOf("careOf");
        registeredOfficeAddress.setCountry("uk");
        registeredOfficeAddress.setLocality("locality");
        registeredOfficeAddress.setPoBox("123");
        registeredOfficeAddress.setPostalCode("post code");
        registeredOfficeAddress.setPremises("premises");
        registeredOfficeAddress.setRegion("region");

        registeredOfficeAddressData.setRegisteredOfficeAddressJson(registeredOfficeAddress);

        return registeredOfficeAddressData;
    }

    static ConfirmationStatementSubmissionDataDao getMockDaoData() {
        ConfirmationStatementSubmissionDataDao data = new ConfirmationStatementSubmissionDataDao();
        data.setStatementOfCapitalData(getStatementOfCapitalDaoData());
        data.setPersonsSignificantControlData(getPersonsSignificantControlDaoData());
        data.setRegisteredOfficeAddressData(getRegisteredOfficeAddressDaoData());
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

    private static RegisteredOfficeAddressDataDao getRegisteredOfficeAddressDaoData() {
        var registeredOfficeAddressData = new RegisteredOfficeAddressDataDao();
        registeredOfficeAddressData.setSectionStatus(SectionStatus.NOT_CONFIRMED);

        var registeredOfficeAddress = new RegisteredOfficeAddressDao();
        registeredOfficeAddress.setAddressLineOne("line1");
        registeredOfficeAddress.setAddressLineTwo("line2");
        registeredOfficeAddress.setCareOf("careOf");
        registeredOfficeAddress.setCountry("uk");
        registeredOfficeAddress.setLocality("locality");
        registeredOfficeAddress.setPoBox("123");
        registeredOfficeAddress.setPostalCode("post code");
        registeredOfficeAddress.setPremises("premises");
        registeredOfficeAddress.setRegion("region");

        registeredOfficeAddressData.setRegisteredOfficeAddressDao(registeredOfficeAddress);

        return registeredOfficeAddressData;
    }
}
