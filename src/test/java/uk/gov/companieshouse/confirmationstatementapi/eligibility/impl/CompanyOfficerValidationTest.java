package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.officers.CompanyOfficerApi;
import uk.gov.companieshouse.api.model.officers.OfficersApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityFailureReason;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.service.OfficerService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyOfficerValidationTest {

    private static final String SECRETARY_JSON = "{ \"officer_role\" : \"secretary\" }";
    private static final String DIRECTOR_JSON = "{ \"officer_role\" : \"director\" }";
    private static final String COMPANY_NUMBER = "12345678";
    private static final List<CompanyOfficerApi> OFFICER_LIST = new ArrayList<>();

    ObjectMapper om = new ObjectMapper();
    OfficersApi mockOfficers = new OfficersApi();
    CompanyProfileApi companyProfileApi = new CompanyProfileApi();

    @Mock
    private OfficerService officerService;

    @InjectMocks
    private CompanyOfficerValidation companyOfficerValidation;

    @BeforeEach
    void init() throws JsonProcessingException {
        OFFICER_LIST.clear();
        CompanyOfficerApi MOCK_OFFICER = om.readValue(DIRECTOR_JSON, CompanyOfficerApi.class);
        OFFICER_LIST.add(MOCK_OFFICER);
        companyProfileApi.setCompanyNumber(COMPANY_NUMBER);
    }

    @Test
    void validateDoesNotThrowOnSingleOfficerCompany() throws ServiceException {
        mockOfficers.setItems(OFFICER_LIST);
        mockOfficers.setActiveCount((long) OFFICER_LIST.size());

        when(officerService.getOfficers(COMPANY_NUMBER)).thenReturn(mockOfficers);

       assertDoesNotThrow(() -> companyOfficerValidation.validate(companyProfileApi));
    }

    @Test
    void validateThrowsOnMultipleOfficerCompany() throws JsonProcessingException, ServiceException {
        CompanyOfficerApi director = om.readValue(DIRECTOR_JSON,CompanyOfficerApi.class);
        OFFICER_LIST.add(director);
        mockOfficers.setItems(OFFICER_LIST);
        mockOfficers.setActiveCount((long) OFFICER_LIST.size());

        when(officerService.getOfficers(COMPANY_NUMBER)).thenReturn(mockOfficers);

        var ex = assertThrows(EligibilityException.class, () ->
                companyOfficerValidation.validate(companyProfileApi));

        assertEquals(EligibilityFailureReason.INVALID_OFFICER_COUNT,ex.getEligibilityFailureReason() );
    }

    @Test
    void validateDoesNotThrowOnSingleOfficerCompanyWithSecretaries() throws JsonProcessingException, ServiceException {
        CompanyOfficerApi secretary = om.readValue(SECRETARY_JSON,CompanyOfficerApi.class);
        OFFICER_LIST.add(secretary);
        mockOfficers.setItems(OFFICER_LIST);
        mockOfficers.setActiveCount((long) OFFICER_LIST.size());

        when(officerService.getOfficers(COMPANY_NUMBER)).thenReturn(mockOfficers);

        assertDoesNotThrow(() -> companyOfficerValidation.validate(companyProfileApi));
    }

    @Test
    void getOfficerCountReturnsNumberOfOfficersExcludingSecretaries() throws JsonProcessingException {
        CompanyOfficerApi director = om.readValue(SECRETARY_JSON,CompanyOfficerApi.class);
        CompanyOfficerApi secretary = om.readValue(DIRECTOR_JSON,CompanyOfficerApi.class);
        OFFICER_LIST.add(director);
        OFFICER_LIST.add(secretary);
        mockOfficers.setItems(OFFICER_LIST);
        mockOfficers.setActiveCount((long) OFFICER_LIST.size());

        var result = companyOfficerValidation.getOfficerCount(mockOfficers.getItems(), mockOfficers.getActiveCount());
        assertEquals(result, 2L);
        assertNotEquals(result, OFFICER_LIST.size());
    }

}
