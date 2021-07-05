package uk.gov.companieshouse.confirmationstatementapi.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import uk.gov.companieshouse.confirmationstatementapi.client.OracleQueryClient;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.model.StatementOfCapital;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StatementOfCapitalServiceTest {

    private static final String COMPANY_NUMBER = "11111111";

    @Mock
    private OracleQueryClient oracleQueryClient;

    @InjectMocks
    private StatementOfCapitalService statementOfCapitalService;

    @Test
    void testGetStatmentOfCapitalData() throws ServiceException {
        when(oracleQueryClient.getStatmentOfCapitalData(COMPANY_NUMBER)).thenReturn(new StatementOfCapital());
        assertNotNull(statementOfCapitalService.getStatementOfCapital(COMPANY_NUMBER));
    }

    @Test
    void testGetStatmentOfCapitalDataServiceException() throws ServiceException {
        when(oracleQueryClient.getStatmentOfCapitalData(COMPANY_NUMBER)).thenThrow(ServiceException.class);
        assertThrows(ServiceException.class, () -> statementOfCapitalService.getStatementOfCapital(COMPANY_NUMBER));
    }
}
