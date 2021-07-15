package uk.gov.companieshouse.confirmationstatementapi.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.confirmationstatementapi.client.OracleQueryClient;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.model.json.statementofcapital.StatementOfCapitalJson;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatementOfCapitalJsonServiceTest {

    private static final String COMPANY_NUMBER = "11111111";

    @Mock
    private OracleQueryClient oracleQueryClient;

    @InjectMocks
    private StatementOfCapitalService statementOfCapitalService;

    @Test
    void testGetStatementOfCapitalData() throws ServiceException {
        when(oracleQueryClient.getStatementOfCapitalData(COMPANY_NUMBER)).thenReturn(new StatementOfCapitalJson());
        assertNotNull(statementOfCapitalService.getStatementOfCapital(COMPANY_NUMBER));
    }

    @Test
    void testGetStatementOfCapitalDataServiceException() throws ServiceException {
        when(oracleQueryClient.getStatementOfCapitalData(COMPANY_NUMBER)).thenThrow(ServiceException.class);
        assertThrows(ServiceException.class, () -> statementOfCapitalService.getStatementOfCapital(COMPANY_NUMBER));
    }
}
