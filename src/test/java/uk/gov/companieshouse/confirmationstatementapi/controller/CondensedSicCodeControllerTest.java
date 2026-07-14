package uk.gov.companieshouse.confirmationstatementapi.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.confirmationstatementapi.model.json.siccode.CondensedSicCodeJson;
import uk.gov.companieshouse.confirmationstatementapi.service.CondensedSicCodeService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CondensedSicCodeControllerTest {

    @Mock
    private CondensedSicCodeService condensedSicCodeService;

    @InjectMocks
    private CondensedSicCodeController condensedSicCodeController;

    @Test
    void testSuccessfulGetCondensedSicCodeList() {

        // GIVEN
        when(condensedSicCodeService.getCondensedSicCodeList()).thenReturn(getMockCondensedSicCodeList());

        // When
        ResponseEntity<Object> response = condensedSicCodeController.getCondensedSicCodeList();

        // Then
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(2, ((List<CondensedSicCodeJson>) response.getBody()).size());
    }

    private List<CondensedSicCodeJson> getMockCondensedSicCodeList() {
        return Arrays.asList(new CondensedSicCodeJson("17110", "Manufacture of pulp"),
                new CondensedSicCodeJson("20301", "Manufacture of paints"));
    }
}
