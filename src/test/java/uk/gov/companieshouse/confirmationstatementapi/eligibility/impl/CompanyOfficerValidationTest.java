package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.officers.OfficersApi;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.service.OfficerService;

import java.time.LocalDate;
import java.util.function.Supplier;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyOfficerValidationTest {

    private static final String MUD_AFTER_ACTIVATION_DATE = "2026-07-01";
    private static final String NOW_DATE = "2026-05-01";

    private final Supplier<LocalDate> supplyNowDate =
            () -> LocalDate.parse(NOW_DATE);

    private static final String COMPANY_NUMBER = "12345678";

    OfficersApi mockOfficers = new OfficersApi();
    CompanyProfileApi companyProfileApi = new CompanyProfileApi();

    @Mock
    private OfficerService officerService;
    @Mock
    private CompanyMultipleOfficerValidation companyMultipleOfficerValidation;
    @Mock
    private CompanySingleOfficerValidation companySingleOfficerValidation;

    private CompanyOfficerValidation companyOfficerValidation;
    private LocalDate mudAfterActivationDate;

    @BeforeEach
    void init() {
        mudAfterActivationDate = LocalDate.parse(MUD_AFTER_ACTIVATION_DATE);
        mockOfficers.setActiveCount(1L);
        companyProfileApi.setCompanyNumber(COMPANY_NUMBER);

        companyOfficerValidation = new CompanyOfficerValidation(officerService,
                companyMultipleOfficerValidation,
                companySingleOfficerValidation);
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(officerService, companyMultipleOfficerValidation, companySingleOfficerValidation);
    }

    @Test
    void shouldPerformMultipleOfficerCheck() throws ServiceException, EligibilityException {
        when(officerService.getOfficers(COMPANY_NUMBER)).thenReturn(mockOfficers);
        when(companyMultipleOfficerValidation.isEligibleForMultipleOfficerCheck(companyProfileApi, mudAfterActivationDate)).thenReturn(true);

        companyOfficerValidation.validateAgainstMadeUpDate(companyProfileApi, mudAfterActivationDate);

        verify(companyMultipleOfficerValidation).validateAgainstMadeUpDate(companyProfileApi, mudAfterActivationDate);
        verify(companySingleOfficerValidation, never()).validateAgainstMadeUpDate(companyProfileApi, mudAfterActivationDate);
    }

    @Test
    void shouldPerformSingleOfficerCheck() throws ServiceException, EligibilityException {
        when(officerService.getOfficers(COMPANY_NUMBER)).thenReturn(mockOfficers);
        when(companyMultipleOfficerValidation.isEligibleForMultipleOfficerCheck(companyProfileApi, mudAfterActivationDate)).thenReturn(false);

        companyOfficerValidation.validateAgainstMadeUpDate(companyProfileApi, mudAfterActivationDate);

        verify(companyMultipleOfficerValidation, never()).validateAgainstMadeUpDate(companyProfileApi, mudAfterActivationDate);
        verify(companySingleOfficerValidation).validateAgainstMadeUpDate(companyProfileApi, mudAfterActivationDate);
    }
}
