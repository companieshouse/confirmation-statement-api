package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyOfficerValidationTest {

    private static final String COMPANY_NUMBER = "12345678";
    private static final List<CompanyOfficerApi> OFFICER_LIST = new ArrayList<>();

    OfficersApi mockOfficers = new OfficersApi();
    CompanyProfileApi companyProfileApi = new CompanyProfileApi();

    @Mock
    private OfficerService officerService;

    @InjectMocks
    private CompanyOfficerValidation companyOfficerValidation;

    @BeforeEach
    void init() {
        OFFICER_LIST.clear();
        CompanyOfficerApi MOCK_OFFICER = new CompanyOfficerApi();
        MOCK_OFFICER.setOfficerRole(OfficerRoleApi.DIRECTOR);
        OFFICER_LIST.add(MOCK_OFFICER);
        companyProfileApi.setCompanyNumber(COMPANY_NUMBER);
        companyOfficerValidation.setOfficer_validation_flag(true);
    }

    @Test
    void validateDoesNotThrowOnSingleOfficerCompany() throws ServiceException {
        mockOfficers.setItems(OFFICER_LIST);
        mockOfficers.setActiveCount((long) OFFICER_LIST.size());

        when(officerService.getOfficers(COMPANY_NUMBER)).thenReturn(mockOfficers);

       assertDoesNotThrow(() -> companyOfficerValidation.validate(companyProfileApi));
    }

    @Test
    void validateThrowsOnMultipleOfficerCompany() throws ServiceException {
        CompanyOfficerApi director = new CompanyOfficerApi();
        director.setOfficerRole(OfficerRoleApi.DIRECTOR);
        OFFICER_LIST.add(director);
        mockOfficers.setItems(OFFICER_LIST);
        mockOfficers.setActiveCount((long) OFFICER_LIST.size());

        when(officerService.getOfficers(COMPANY_NUMBER)).thenReturn(mockOfficers);

        var ex = assertThrows(EligibilityException.class, () ->
                companyOfficerValidation.validate(companyProfileApi));

        assertEquals(EligibilityStatusCode.INVALID_COMPANY_APPOINTMENTS_MORE_THAN_ONE_OFFICER,ex.getEligibilityStatusCode() );
    }

    @Test
    void validateDoesNotThrowOnSingleOfficerCompanyWithSecretaries() throws ServiceException {
        CompanyOfficerApi secretary = new CompanyOfficerApi();
        secretary.setOfficerRole(OfficerRoleApi.SECRETARY);
        OFFICER_LIST.add(secretary);
        mockOfficers.setItems(OFFICER_LIST);
        mockOfficers.setActiveCount((long) OFFICER_LIST.size());

        when(officerService.getOfficers(COMPANY_NUMBER)).thenReturn(mockOfficers);

        assertDoesNotThrow(() -> companyOfficerValidation.validate(companyProfileApi));
    }

    @Test
    void getOfficerCountReturnsNumberOfOfficersExcludingSecretaries() {
        CompanyOfficerApi director = new CompanyOfficerApi();
        CompanyOfficerApi secretary = new CompanyOfficerApi();
        director.setOfficerRole(OfficerRoleApi.NOMINEE_DIRECTOR);
        secretary.setOfficerRole(OfficerRoleApi.SECRETARY);

        OFFICER_LIST.add(director);
        OFFICER_LIST.add(secretary);
        mockOfficers.setItems(OFFICER_LIST);
        mockOfficers.setActiveCount((long) OFFICER_LIST.size());

        var result = companyOfficerValidation.getOfficerCount(mockOfficers.getItems());
        assertEquals(2L, result);
        assertNotEquals(OFFICER_LIST.size(), result);
    }

}
