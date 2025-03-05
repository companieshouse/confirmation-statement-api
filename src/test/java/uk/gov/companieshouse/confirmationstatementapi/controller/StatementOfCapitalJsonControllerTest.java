package uk.gov.companieshouse.confirmationstatementapi.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import uk.gov.companieshouse.api.model.company.StatementOfCapitalJson;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.exception.StatementOfCapitalNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.service.StatementOfCapitalService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatementOfCapitalJsonControllerTest {

    @Mock
    private Transaction transaction;

    @Mock
    private StatementOfCapitalService statementOfCapitalService;

    @InjectMocks
    private StatementOfCapitalController statementOfCapitalController;

    private static final String TRANSACTION_ID = "GFEDCBA";
    private static final String ERIC_REQUEST_ID = "XaBcDeF12345";

    @Test
    void getStatementOfCapital() throws ServiceException, StatementOfCapitalNotFoundException {
        when(statementOfCapitalService.getStatementOfCapital(transaction.getCompanyNumber())).thenReturn(new StatementOfCapitalJson());
        var response = statementOfCapitalController.getStatementOfCapital(transaction, TRANSACTION_ID, ERIC_REQUEST_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getStatementOfCapitalServiceException() throws ServiceException, StatementOfCapitalNotFoundException {
        when(statementOfCapitalService.getStatementOfCapital(transaction.getCompanyNumber())).thenThrow(ServiceException.class);
        var response = statementOfCapitalController.getStatementOfCapital(transaction, TRANSACTION_ID, ERIC_REQUEST_ID);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void getStatementOfCapitalStatementOfCapitalNotFoundException() throws ServiceException, StatementOfCapitalNotFoundException {
        when(statementOfCapitalService.getStatementOfCapital(transaction.getCompanyNumber())).thenThrow(StatementOfCapitalNotFoundException.class);
        var response = statementOfCapitalController.getStatementOfCapital(transaction, TRANSACTION_ID, ERIC_REQUEST_ID);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

}
