package uk.gov.companieshouse.confirmationstatementapi.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.model.payment.Cost;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.confirmationstatementapi.exception.CompanyNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.service.CostService;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CostsControllerTest {

    private static final ResponseEntity<Object> NOT_FOUND_RESPONSE = ResponseEntity.notFound().build();
    private static final String SUBMISSION_ID = "ABCDEFG";
    private static final String TRANSACTION_ID = "GFEDCBA";
    private static final String ERIC_REQUEST_ID = "XaBcDeF12345";

    @Mock
    private CostService costService;

    @Mock
    private Transaction transaction;

    @InjectMocks
    private CostsController costsController;

    @Test
    void getCosts() throws ServiceException, CompanyNotFoundException {

        when(costService.getCosts(transaction)).thenReturn(new Cost());
        var response = costsController.getCosts(transaction, TRANSACTION_ID, SUBMISSION_ID, ERIC_REQUEST_ID);

        verify(costService, times(1)).getCosts(transaction);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getCostsThrowCompanyNotFoundException() throws ServiceException, CompanyNotFoundException {

        when(costService.getCosts(transaction)).thenThrow(CompanyNotFoundException.class);
        var response = costsController.getCosts(transaction, TRANSACTION_ID, SUBMISSION_ID, ERIC_REQUEST_ID);

        verify(costService, times(1)).getCosts(transaction);
        assertThrows(CompanyNotFoundException.class, () -> costService.getCosts(transaction));
        assertEquals(NOT_FOUND_RESPONSE, response);
    }

    @Test
    void getCostsThrowServiceException() throws ServiceException, CompanyNotFoundException {

        when(costService.getCosts(transaction)).thenThrow(new ServiceException("ERROR", new IOException()));
        var response = costsController.getCosts(transaction, TRANSACTION_ID, SUBMISSION_ID, ERIC_REQUEST_ID);

        verify(costService, times(1)).getCosts(transaction);
        assertThrows(ServiceException.class, () -> costService.getCosts(transaction));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}