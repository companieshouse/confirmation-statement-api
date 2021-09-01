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

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;

@ExtendWith(MockitoExtension.class)
class UserAuthInterceptorTest {

    private static final String CREATED_BY_ID = "12345";
    private static final String FAILED_RESPONSE_BODY = "{\n    \"error\" : \"USER_NOT_AUTHORISED_TO_ACCESS_SUBMISSION\"\n}";

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
    void preHandle() throws IOException {
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        Object mockHandler = new Object();

        when(mockHttpServletRequest.getHeader("eric-identity")).thenReturn(CREATED_BY_ID);
        when(mockHttpServletRequest.getHeader("eric-identity-type")).thenReturn("oauth");
        var result = userAuthInterceptor.preHandle(mockHttpServletRequest, mockHttpServletResponse, mockHandler);

        assertTrue(result);
    }

    @Test
    void preHandleApiKey() throws IOException {
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        Object mockHandler = new Object();

        when(mockHttpServletRequest.getMethod()).thenReturn(GET.name());
        when(mockHttpServletRequest.getHeader("eric-identity-type")).thenReturn("key");
        when(mockHttpServletRequest.getHeader("ERIC-Authorised-Key-Roles")).thenReturn("*");
        var result = userAuthInterceptor.preHandle(mockHttpServletRequest, mockHttpServletResponse, mockHandler);

        assertTrue(result);
    }

    @Test
    void preHandleIdsDoNotMatch() throws IOException {
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        Object mockHandler = new Object();

        when(mockHttpServletRequest.getHeader("eric-identity")).thenReturn("654321");
        when(mockHttpServletRequest.getHeader("eric-identity-type")).thenReturn("oauth");
        var result = userAuthInterceptor.preHandle(mockHttpServletRequest, mockHttpServletResponse, mockHandler);

        assertFalse(result);
        assertEquals(401, mockHttpServletResponse.getStatus());
        assertEquals(FAILED_RESPONSE_BODY, mockHttpServletResponse.getContentAsString());
    }

    @Test
    void preHandleNoIdOnTransaction() throws IOException {
        var transaction = new Transaction();
        transaction.setCreatedBy(new HashMap<>());
        when(mockHttpServletRequest.getAttribute("transaction")).thenReturn(transaction);

        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        Object mockHandler = new Object();

        when(mockHttpServletRequest.getHeader("eric-identity")).thenReturn("654321");
        when(mockHttpServletRequest.getHeader("eric-identity-type")).thenReturn("oauth");
        var result = userAuthInterceptor.preHandle(mockHttpServletRequest, mockHttpServletResponse, mockHandler);

        assertFalse(result);
        assertEquals(401, mockHttpServletResponse.getStatus());
        assertEquals(FAILED_RESPONSE_BODY, mockHttpServletResponse.getContentAsString());
    }
}