package uk.gov.companieshouse.confirmationstatementapi.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.model.json.statementofcapital.StatementOfCapitalJson;
import uk.gov.companieshouse.confirmationstatementapi.service.StatementOfCapitalService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatementOfCapitalJsonControllerTest {

    @Mock
    private StatementOfCapitalService statementOfCapitalService;

    @InjectMocks
    private StatementOfCapitalController statementOfCapitalController;

    private static final String COMPANY_NUMBER = "11111111";
    private static final String ERICK_REQUEST_ID = "XaBcDeF12345";

    @Test
    void getStatementOfCapital() throws ServiceException {
        when(statementOfCapitalService.getStatementOfCapital(COMPANY_NUMBER)).thenReturn(new StatementOfCapitalJson());
        var response = statementOfCapitalController.getStatementOfCapital(COMPANY_NUMBER, ERICK_REQUEST_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getStatementOfCapitalServiceException() throws ServiceException {
        when(statementOfCapitalService.getStatementOfCapital(COMPANY_NUMBER)).thenThrow(ServiceException.class);
        var response = statementOfCapitalController.getStatementOfCapital(COMPANY_NUMBER, ERICK_REQUEST_ID);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

}
