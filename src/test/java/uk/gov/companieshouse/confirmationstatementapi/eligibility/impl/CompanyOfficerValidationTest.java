package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.officers.CompanyOfficerApi;
import uk.gov.companieshouse.api.model.officers.OfficerRoleApi;
import uk.gov.companieshouse.api.model.officers.OfficersApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.service.OfficerService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyOfficerValidationTest {

    private static final String COMPANY_NUMBER = "12345678";
    private static final List<CompanyOfficerApi> OFFICER_LIST = new ArrayList<>();

    OfficersApi mockOfficers = new OfficersApi();
    CompanyProfileApi companyProfileApi = new CompanyProfileApi();

    @Mock
    private OfficerService officerService;
    private CompanyOfficerValidation companyOfficerValidation;

    @BeforeEach
    void init() {
        OFFICER_LIST.clear();
        CompanyOfficerApi MOCK_OFFICER = new CompanyOfficerApi();
        MOCK_OFFICER.setOfficerRole(OfficerRoleApi.DIRECTOR);
        OFFICER_LIST.add(MOCK_OFFICER);
        companyProfileApi.setCompanyNumber(COMPANY_NUMBER);
    }

    @Test
    void validateDoesNotThrowExceptionOnSingleOfficerCompanyWithDirectorWithMultipleOfficerJourneyFlagOff() throws ServiceException {
        companyOfficerValidation = new CompanyOfficerValidation(officerService,false);
        mockOfficers.setItems(OFFICER_LIST);
        mockOfficers.setActiveCount((long) OFFICER_LIST.size());

        when(officerService.getOfficers(COMPANY_NUMBER)).thenReturn(mockOfficers);

       assertDoesNotThrow(() -> companyOfficerValidation.validate(companyProfileApi));
    }

    @Test
    void validateThrowsExceptionOnSingleOfficerCompanyWithSecretaryWithMultipleOfficerJourneyFlagOff() throws ServiceException {
        companyOfficerValidation = new CompanyOfficerValidation(officerService,false);
        OFFICER_LIST.clear();
        CompanyOfficerApi MOCK_OFFICER = new CompanyOfficerApi();
        MOCK_OFFICER.setOfficerRole(OfficerRoleApi.SECRETARY);
        OFFICER_LIST.add(MOCK_OFFICER);
        mockOfficers.setItems(OFFICER_LIST);
        mockOfficers.setActiveCount((long) OFFICER_LIST.size());

        when(officerService.getOfficers(COMPANY_NUMBER)).thenReturn(mockOfficers);

        var ex = assertThrows(EligibilityException.class, () -> companyOfficerValidation.validate(companyProfileApi));

        assertEquals(EligibilityStatusCode.INVALID_COMPANY_APPOINTMENTS_INVALID_NUMBER_OF_OFFICERS,ex.getEligibilityStatusCode() );
    }

    @Test
    void validateThrowsExceptionOnMultipleOfficerCompanyWithMultipleOfficerJourneyFlagOff() throws ServiceException {
        companyOfficerValidation = new CompanyOfficerValidation(officerService,false);
        CompanyOfficerApi director = new CompanyOfficerApi();
        director.setOfficerRole(OfficerRoleApi.CORPORATE_DIRECTOR);
        OFFICER_LIST.add(director);
        mockOfficers.setItems(OFFICER_LIST);
        mockOfficers.setActiveCount((long) OFFICER_LIST.size());

        when(officerService.getOfficers(COMPANY_NUMBER)).thenReturn(mockOfficers);

        var ex = assertThrows(EligibilityException.class, () -> companyOfficerValidation.validate(companyProfileApi));

        assertEquals(EligibilityStatusCode.INVALID_COMPANY_APPOINTMENTS_INVALID_NUMBER_OF_OFFICERS,ex.getEligibilityStatusCode() );
    }

    @Test
    void validateDoesNotThrowExceptionOnFiveOrLessOfficersWithMultipleOfficerJourneyFlagOn() throws ServiceException {
        companyOfficerValidation = new CompanyOfficerValidation(officerService,true);
        CompanyOfficerApi director = new CompanyOfficerApi();
        director.setOfficerRole(OfficerRoleApi.CORPORATE_DIRECTOR);
        OFFICER_LIST.add(director);
        mockOfficers.setItems(OFFICER_LIST);
        mockOfficers.setActiveCount(5L);

        when(officerService.getOfficers(COMPANY_NUMBER)).thenReturn(mockOfficers);

        assertDoesNotThrow(() -> companyOfficerValidation.validate(companyProfileApi));
    }

    @Test
    void validateThrowsExceptionOnZeroOfficersWithMultipleOfficerJourneyFlagOn() throws ServiceException {
        companyOfficerValidation = new CompanyOfficerValidation(officerService,true);
        CompanyOfficerApi director = new CompanyOfficerApi();
        director.setOfficerRole(OfficerRoleApi.CORPORATE_DIRECTOR);
        OFFICER_LIST.add(director);
        mockOfficers.setItems(OFFICER_LIST);
        mockOfficers.setActiveCount(0L);

        when(officerService.getOfficers(COMPANY_NUMBER)).thenReturn(mockOfficers);

        var ex = assertThrows(EligibilityException.class, () -> companyOfficerValidation.validate(companyProfileApi));
        assertEquals(EligibilityStatusCode.INVALID_COMPANY_APPOINTMENTS_INVALID_NUMBER_OF_OFFICERS,ex.getEligibilityStatusCode() );
    }

    @Test
    void validateThrowsExceptionOnMoreThanFiveOfficersWithMultipleOfficerJourneyFlagOn() throws ServiceException {
        companyOfficerValidation = new CompanyOfficerValidation(officerService,true);
        CompanyOfficerApi director = new CompanyOfficerApi();
        director.setOfficerRole(OfficerRoleApi.CORPORATE_DIRECTOR);
        OFFICER_LIST.add(director);
        mockOfficers.setItems(OFFICER_LIST);
        mockOfficers.setActiveCount(6L);

        when(officerService.getOfficers(COMPANY_NUMBER)).thenReturn(mockOfficers);

        var ex = assertThrows(EligibilityException.class, () -> companyOfficerValidation.validate(companyProfileApi));

        assertEquals(EligibilityStatusCode.INVALID_COMPANY_APPOINTMENTS_INVALID_NUMBER_OF_OFFICERS,ex.getEligibilityStatusCode() );
    }

    @Test
    void validateThrowsExceptionOnSingleDirectorCompanyWithSecretariesWithMultipleOfficerJourneyFlagOff() throws ServiceException {
        companyOfficerValidation = new CompanyOfficerValidation(officerService,false);
        CompanyOfficerApi secretary = new CompanyOfficerApi();
        secretary.setOfficerRole(OfficerRoleApi.SECRETARY);
        OFFICER_LIST.add(secretary);
        mockOfficers.setItems(OFFICER_LIST);
        mockOfficers.setActiveCount((long) OFFICER_LIST.size());

        when(officerService.getOfficers(COMPANY_NUMBER)).thenReturn(mockOfficers);

        var ex = assertThrows(EligibilityException.class, () -> companyOfficerValidation.validate(companyProfileApi));

        assertEquals(EligibilityStatusCode.INVALID_COMPANY_APPOINTMENTS_INVALID_NUMBER_OF_OFFICERS,ex.getEligibilityStatusCode() );
    }

    @Test
    void isOfficerDirectorReturnsFalseForMultipleOfficerCompany() {
        companyOfficerValidation = new CompanyOfficerValidation(officerService,false);
        CompanyOfficerApi director = new CompanyOfficerApi();
        CompanyOfficerApi director2 = new CompanyOfficerApi();
        CompanyOfficerApi secretary = new CompanyOfficerApi();
        director.setOfficerRole(OfficerRoleApi.NOMINEE_DIRECTOR);
        director2.setOfficerRole(OfficerRoleApi.CORPORATE_DIRECTOR);
        secretary.setOfficerRole(OfficerRoleApi.SECRETARY);

        OFFICER_LIST.add(director);
        OFFICER_LIST.add(director2);
        OFFICER_LIST.add(secretary);
        mockOfficers.setItems(OFFICER_LIST);
        mockOfficers.setActiveCount((long) OFFICER_LIST.size());

        var result = companyOfficerValidation.isOfficerDirector(mockOfficers.getItems(), mockOfficers.getActiveCount());
        assertFalse(result);
    }

    @Test
    void isOfficerDirectorReturnsTrueForCompanyWithTwoDirectorsOneRetired() {
        companyOfficerValidation = new CompanyOfficerValidation(officerService,false);
        CompanyOfficerApi director = new CompanyOfficerApi();
        director.setOfficerRole(OfficerRoleApi.NOMINEE_DIRECTOR);
        director.setResignedOn(LocalDate.now());

        OFFICER_LIST.add(director);
        mockOfficers.setItems(OFFICER_LIST);
        mockOfficers.setActiveCount(1L);

        var result = companyOfficerValidation.isOfficerDirector(mockOfficers.getItems(), mockOfficers.getActiveCount());
        assertTrue(result);
    }

    @Test
    void isOfficerDirectorReturnsFalseForCompanyWithOneSecretary() {
        companyOfficerValidation = new CompanyOfficerValidation(officerService,false);
        OFFICER_LIST.clear();
        CompanyOfficerApi MOCK_OFFICER = new CompanyOfficerApi();
        MOCK_OFFICER.setOfficerRole(OfficerRoleApi.SECRETARY);
        OFFICER_LIST.add(MOCK_OFFICER);
        mockOfficers.setItems(OFFICER_LIST);
        mockOfficers.setActiveCount((long) OFFICER_LIST.size());

        var result = companyOfficerValidation.isOfficerDirector(mockOfficers.getItems(), mockOfficers.getActiveCount());
        assertFalse(result);
    }

    @Test
    void validateThrowsExceptionOnCompanyWithZeroOfficers() throws ServiceException {
        companyOfficerValidation = new CompanyOfficerValidation(officerService,false);
        OfficersApi officersApi = new OfficersApi();
        officersApi.setItems(Collections.emptyList());
        officersApi.setActiveCount(0L);
        when(officerService.getOfficers(COMPANY_NUMBER)).thenReturn(officersApi);
        var result = companyOfficerValidation.isOfficerDirector(mockOfficers.getItems(), mockOfficers.getActiveCount());
        var ex = assertThrows(EligibilityException.class, () -> companyOfficerValidation.validate(companyProfileApi));

        assertEquals(EligibilityStatusCode.INVALID_COMPANY_APPOINTMENTS_INVALID_NUMBER_OF_OFFICERS,ex.getEligibilityStatusCode() );
        assertFalse(result);
    }

    @Test
    void validateThrowsExceptionOnCompanyWithNullOfficerList() throws ServiceException {
        companyOfficerValidation = new CompanyOfficerValidation(officerService,true);
        OfficersApi officersApi = new OfficersApi();
        officersApi.setItems(null);
        officersApi.setActiveCount(null);
        when(officerService.getOfficers(COMPANY_NUMBER)).thenReturn(officersApi);
        var result = companyOfficerValidation.isOfficerDirector(mockOfficers.getItems(), mockOfficers.getActiveCount());
        var ex = assertThrows(EligibilityException.class, () -> companyOfficerValidation.validate(companyProfileApi));

        assertEquals(EligibilityStatusCode.INVALID_COMPANY_APPOINTMENTS_INVALID_NUMBER_OF_OFFICERS,ex.getEligibilityStatusCode() );
        assertFalse(result);
    }
}
