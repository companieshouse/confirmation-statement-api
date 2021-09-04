package uk.gov.companieshouse.confirmationstatementapi.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.confirmationstatementapi.exception.CompanyNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.model.json.NextMadeUpToDateJson;
import uk.gov.companieshouse.confirmationstatementapi.service.ConfirmationStatementService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NextMadeUpToDateControllerTest {

    private static final String COMPANY_NUMBER = "12345678";
    private static final NextMadeUpToDateJson NEXT_MADE_UP_TO_DATE_JSON = new NextMadeUpToDateJson();

    @Mock
    private ConfirmationStatementService confirmationStatementService;

    @InjectMocks
    private NextMadeUpToDateController nextMadeUpToDateController;

    @BeforeAll
    static void beforeAll() {
        NEXT_MADE_UP_TO_DATE_JSON.setDue(false);
        NEXT_MADE_UP_TO_DATE_JSON.setNewNextMadeUpToDate("2021-06-19");
        NEXT_MADE_UP_TO_DATE_JSON.setCurrentNextMadeUpToDate("2021-07-21");
    }

    @Test
    void getNextMadeUpToDate() throws CompanyNotFoundException, ServiceException {
        when(confirmationStatementService.getNextMadeUpToDate(COMPANY_NUMBER))
                .thenReturn(NEXT_MADE_UP_TO_DATE_JSON);

        ResponseEntity<NextMadeUpToDateJson> response = nextMadeUpToDateController.getNextMadeUpToDate(COMPANY_NUMBER);

        assertEquals(NEXT_MADE_UP_TO_DATE_JSON, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getNextMadeUpToDateReturns404() throws CompanyNotFoundException, ServiceException {
        when(confirmationStatementService.getNextMadeUpToDate(COMPANY_NUMBER))
                .thenThrow(new CompanyNotFoundException());

        ResponseEntity<NextMadeUpToDateJson> response = nextMadeUpToDateController.getNextMadeUpToDate(COMPANY_NUMBER);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getNextMadeUpToDateReturns500() throws CompanyNotFoundException, ServiceException {
        when(confirmationStatementService.getNextMadeUpToDate(COMPANY_NUMBER))
                .thenThrow(new NullPointerException());

        ResponseEntity<NextMadeUpToDateJson> response = nextMadeUpToDateController.getNextMadeUpToDate(COMPANY_NUMBER);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }
}
