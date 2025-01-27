package uk.gov.companieshouse.confirmationstatementapi.interceptor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.transaction.TransactionStatus;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FilingInterceptorTest {

    private static final String CREATED_BY_ID = "12345";

    @Mock
    private HttpServletRequest mockHttpServletRequest;

    @InjectMocks
    private FilingInterceptor filingInterceptor;

    private Transaction transaction;
    @BeforeEach
    void init() {
        transaction = new Transaction();
        transaction.setStatus(TransactionStatus.CLOSED);
        transaction.setCreatedBy(Collections.singletonMap("id", CREATED_BY_ID));
        when(mockHttpServletRequest.getAttribute("transaction")).thenReturn(transaction);
    }

    @Test
    void preHandleClosed() throws IOException {
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        Object mockHandler = new Object();

        var result = filingInterceptor.preHandle(mockHttpServletRequest, mockHttpServletResponse, mockHandler);

        assertTrue(result);
    }

    @Test
    void preHandleOpen() throws IOException {
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        Object mockHandler = new Object();

        transaction.setStatus(TransactionStatus.OPEN);
        var result = filingInterceptor.preHandle(mockHttpServletRequest, mockHttpServletResponse, mockHandler);

        assertFalse(result);
    }

    @Test
    void preHandleNoTransaction() throws IOException {
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        Object mockHandler = new Object();

        when(mockHttpServletRequest.getAttribute("transaction")).thenReturn(null);
        var result = filingInterceptor.preHandle(mockHttpServletRequest, mockHttpServletResponse, mockHandler);

        assertFalse(result);
    }
}