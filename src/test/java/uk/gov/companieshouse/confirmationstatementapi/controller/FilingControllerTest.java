package uk.gov.companieshouse.confirmationstatementapi.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class FilingControllerTest {

    @InjectMocks
    private FilingController filingController;
    @Test
    void getFiling() {
        var transaction = new Transaction();
        transaction.setDescription("12345678");
        var result = filingController.getFiling(transaction);

        assertNotNull(result.getBody());
        assertEquals("12345678", result.getBody().getDescription());
    }
}