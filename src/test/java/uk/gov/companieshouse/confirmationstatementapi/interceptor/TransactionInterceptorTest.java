package uk.gov.companieshouse.confirmationstatementapi.interceptor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerMapping;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.confirmationstatementapi.client.ApiKeyClient;
import uk.gov.companieshouse.confirmationstatementapi.service.TransactionService;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionInterceptorTest {

    private static final String TRANSACTION_ID = "12345678";

    @Mock
    private TransactionService transactionService;

    @Mock
    private HttpServletRequest mockHttpServletRequest;
    @InjectMocks
    private TransactionInterceptor transactionInterceptor;

    @Test
    void preHandle() throws Exception {
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        Object mockHandler = new Object();
        Transaction dummyTransaction = new Transaction();
        dummyTransaction.setId(TRANSACTION_ID);

        var pathParams = new HashMap<String, String>();
        pathParams.put("transaction_id", TRANSACTION_ID);

        when(transactionService.getTransaction(TRANSACTION_ID)).thenReturn(dummyTransaction);
        when(mockHttpServletRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(pathParams);

        assertTrue(transactionInterceptor.preHandle(mockHttpServletRequest, mockHttpServletResponse, mockHandler));
        verify(mockHttpServletRequest, times(1)).setAttribute("transaction", dummyTransaction);
    }
}