package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.officers.CompanyOfficerApi;
import uk.gov.companieshouse.api.model.officers.IdentityVerificationDetails;
import uk.gov.companieshouse.api.model.officers.OfficerRoleApi;
import uk.gov.companieshouse.api.model.officers.OfficersApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.service.OfficerService;

@ExtendWith(MockitoExtension.class)
class DirectorsIdentityVerifiedValidationTest {

    private static final String COMPANY_NUMBER = "12345678";
    private CompanyProfileApi companyProfileApi;
    private OfficersApi officersApi;

    @Mock
    private OfficerService officerService;
    private DirectorsIdentityVerifiedValidation validation;

    @BeforeEach
    void setUp() {
        companyProfileApi = new CompanyProfileApi();
        companyProfileApi.setCompanyNumber(COMPANY_NUMBER);
        officersApi = new OfficersApi();
    }

    private CompanyOfficerApi createOfficer(OfficerRoleApi role, boolean resigned, IdentityVerificationDetails idvDetails) {
        CompanyOfficerApi officer = new CompanyOfficerApi();
        officer.setOfficerRole(role);
        if (resigned) {
            officer.setResignedOn(LocalDate.now().minusDays(1));
        }
        officer.setIdentityVerificationDetails(idvDetails);
        return officer;
    }

    private IdentityVerificationDetails createIdvDetails(LocalDate start, LocalDate end) {
        IdentityVerificationDetails idv = new IdentityVerificationDetails();
        idv.setAppointmentVerificationStartOn(start);
        idv.setAppointmentVerificationEndOn(end);
        return idv;
    }

    @Test
    void validate_AllDirectorsVerified_Passes() throws ServiceException {
        validation = new DirectorsIdentityVerifiedValidation(officerService, true);
        LocalDate today = LocalDate.now();
        IdentityVerificationDetails idv = createIdvDetails(today.minusDays(1), today.plusDays(1));
        CompanyOfficerApi director = createOfficer(OfficerRoleApi.DIRECTOR, false, idv);
        officersApi.setItems(List.of(director));
        when(officerService.getOfficers(COMPANY_NUMBER)).thenReturn(officersApi);
        assertDoesNotThrow(() -> validation.validate(companyProfileApi));
    }

    @Test
    void validate_DirectorNotVerified_Throws() throws ServiceException {
        validation = new DirectorsIdentityVerifiedValidation(officerService, true);
        LocalDate today = LocalDate.now();
        IdentityVerificationDetails idv = createIdvDetails(today.plusDays(1), today.plusDays(2)); // not started yet
        CompanyOfficerApi director = createOfficer(OfficerRoleApi.DIRECTOR, false, idv);
        officersApi.setItems(List.of(director));
        when(officerService.getOfficers(COMPANY_NUMBER)).thenReturn(officersApi);
        EligibilityException ex = assertThrows(EligibilityException.class, () -> validation.validate(companyProfileApi));
        assertEquals(EligibilityStatusCode.INVALID_DIRECTORS_NOT_ALL_IDENTITY_VERIFIED, ex.getEligibilityStatusCode());
    }

    @Test
    void validate_DirectorResigned_Passes() throws ServiceException {
        validation = new DirectorsIdentityVerifiedValidation(officerService, true);
        CompanyOfficerApi director = createOfficer(OfficerRoleApi.DIRECTOR, true, null);
        officersApi.setItems(List.of(director));
        when(officerService.getOfficers(COMPANY_NUMBER)).thenReturn(officersApi);
        assertDoesNotThrow(() -> validation.validate(companyProfileApi));
    }

    @Test
    void validate_NonDirector_Passes() throws ServiceException {
        validation = new DirectorsIdentityVerifiedValidation(officerService, true);
        CompanyOfficerApi secretary = createOfficer(OfficerRoleApi.SECRETARY, false, null);
        officersApi.setItems(List.of(secretary));
        when(officerService.getOfficers(COMPANY_NUMBER)).thenReturn(officersApi);
        assertDoesNotThrow(() -> validation.validate(companyProfileApi));
    }

    @Test
    void validate_DirectorWithNullIdvDetails_Fails() throws ServiceException {
        validation = new DirectorsIdentityVerifiedValidation(officerService, true);
        CompanyOfficerApi director = createOfficer(OfficerRoleApi.DIRECTOR, false, null);
        officersApi.setItems(List.of(director));
        when(officerService.getOfficers(COMPANY_NUMBER)).thenReturn(officersApi);
        EligibilityException ex = assertThrows(EligibilityException.class, () -> validation.validate(companyProfileApi));
        assertEquals(EligibilityStatusCode.INVALID_DIRECTORS_NOT_ALL_IDENTITY_VERIFIED, ex.getEligibilityStatusCode());
    }

    @Test
    void validate_DirectorWithNullStartOrEnd_Fails() throws ServiceException {
        validation = new DirectorsIdentityVerifiedValidation(officerService, true);
        IdentityVerificationDetails idvNullStart = createIdvDetails(null, LocalDate.now().plusDays(1));
        IdentityVerificationDetails idvNullEnd = createIdvDetails(LocalDate.now().minusDays(1), null);
        CompanyOfficerApi director1 = createOfficer(OfficerRoleApi.DIRECTOR, false, idvNullStart);
        CompanyOfficerApi director2 = createOfficer(OfficerRoleApi.DIRECTOR, false, idvNullEnd);
        officersApi.setItems(List.of(director1, director2));
        when(officerService.getOfficers(COMPANY_NUMBER)).thenReturn(officersApi);
        EligibilityException ex = assertThrows(EligibilityException.class, () -> validation.validate(companyProfileApi));
        assertEquals(EligibilityStatusCode.INVALID_DIRECTORS_NOT_ALL_IDENTITY_VERIFIED, ex.getEligibilityStatusCode());
    }

    @Test
    void validate_FeatureFlagOff_DoesNotCheckVerification() throws ServiceException {
        validation = new DirectorsIdentityVerifiedValidation(officerService, false);
        CompanyOfficerApi director = createOfficer(OfficerRoleApi.DIRECTOR, false, null);
        officersApi.setItems(List.of(director));
        when(officerService.getOfficers(COMPANY_NUMBER)).thenReturn(officersApi);
        assertDoesNotThrow(() -> validation.validate(companyProfileApi));
    }
}

