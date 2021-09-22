package uk.gov.companieshouse.confirmationstatementapi.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.filinggenerator.FilingApi;
import uk.gov.companieshouse.confirmationstatementapi.exception.SubmissionNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.service.FilingService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FilingControllerTest {

    private static final String CONFIRMATION_ID = "abc123";

    @InjectMocks
    private FilingController filingController;

    @Mock
    private FilingService filingService;

    @Test
    void getFiling() throws SubmissionNotFoundException {
        FilingApi filing = new FilingApi();
        filing.setDescription("12345678");
        when(filingService.generateConfirmationFiling(CONFIRMATION_ID)).thenReturn(filing);
        var result = filingController.getFiling(CONFIRMATION_ID);

        assertNotNull(result.getBody());
        assertEquals("12345678", result.getBody().getDescription());
    }
}