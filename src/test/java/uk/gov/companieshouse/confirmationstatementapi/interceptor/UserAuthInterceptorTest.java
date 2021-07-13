package uk.gov.companieshouse.confirmationstatementapi.interceptor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAuthInterceptorTest {

    private static final String CREATED_BY_ID = "12345";
    @Mock
    private HttpServletRequest mockHttpServletRequest;

    @InjectMocks
    private UserAuthInterceptor userAuthInterceptor;

    @BeforeEach
    void init() {
        var transaction = new Transaction();
        transaction.setCreatedBy(Collections.singletonMap("id", CREATED_BY_ID));
        when(mockHttpServletRequest.getAttribute("transaction")).thenReturn(transaction);
    }

    @Test
    void preHandle() {
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        Object mockHandler = new Object();

        when(mockHttpServletRequest.getHeader("eric-identity")).thenReturn(CREATED_BY_ID);
        var result = userAuthInterceptor.preHandle(mockHttpServletRequest, mockHttpServletResponse, mockHandler);

        assertTrue(result);
    }

    @Test
    void preHandleIdsDoNotMatch() {
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        Object mockHandler = new Object();

        when(mockHttpServletRequest.getHeader("eric-identity")).thenReturn("654321");
        var result = userAuthInterceptor.preHandle(mockHttpServletRequest, mockHttpServletResponse, mockHandler);

        assertFalse(result);
        assertEquals(401, mockHttpServletResponse.getStatus());
    }

    @Test
    void preHandleNoIdOnTransaction() {
        var transaction = new Transaction();
        transaction.setCreatedBy(new HashMap<>());
        when(mockHttpServletRequest.getAttribute("transaction")).thenReturn(transaction);

        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        Object mockHandler = new Object();

        when(mockHttpServletRequest.getHeader("eric-identity")).thenReturn("654321");
        var result = userAuthInterceptor.preHandle(mockHttpServletRequest, mockHttpServletResponse, mockHandler);

        assertFalse(result);
        assertEquals(401, mockHttpServletResponse.getStatus());
    }
}