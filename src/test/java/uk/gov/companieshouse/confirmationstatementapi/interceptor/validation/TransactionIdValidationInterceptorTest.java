package uk.gov.companieshouse.confirmationstatementapi.interceptor.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.TRANSACTION_ID_KEY;

@ExtendWith(MockitoExtension.class)
public class TransactionIdValidationInterceptorTest {

    @Mock
    private HttpServletRequest mockHttpServletRequest;
    @InjectMocks
    private TransactionIdValidationInterceptor transactionIdValidationInterceptor;

    @Test
    void preHandleTrueForStringWithSpecialChars() throws Exception {
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        Object mockHandler = new Object();

        String special = "1123-abc$456%def";
        var pathParams = new HashMap<String, String>();
        pathParams.put(TRANSACTION_ID_KEY, special);
        when(mockHttpServletRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(pathParams);

        assertTrue(transactionIdValidationInterceptor.preHandle(mockHttpServletRequest, mockHttpServletResponse, mockHandler));
    }

    @Test
    void preHandleTrueForFiftyLengthString() throws Exception {
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        Object mockHandler = new Object();

        String fifty = "12345678901234567890123456789012345678901234567890";
        var pathParams = new HashMap<String, String>();
        pathParams.put(TRANSACTION_ID_KEY, fifty);
        when(mockHttpServletRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(pathParams);

        assertTrue(transactionIdValidationInterceptor.preHandle(mockHttpServletRequest, mockHttpServletResponse, mockHandler));
    }

    @Test
    void preHandleFalseForNullString() {
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        Object mockHandler = new Object();

        var pathParams = new HashMap<String, String>();
        pathParams.put(TRANSACTION_ID_KEY, null);
        when(mockHttpServletRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(pathParams);

        assertFalse(transactionIdValidationInterceptor.preHandle(mockHttpServletRequest, mockHttpServletResponse, mockHandler));
        assertEquals(500,  mockHttpServletResponse.getStatus());
    }

    @Test
    void preHandleFalseForEmptyString() {
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        Object mockHandler = new Object();

        var pathParams = new HashMap<String, String>();
        pathParams.put(TRANSACTION_ID_KEY, "");
        when(mockHttpServletRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(pathParams);

        assertFalse(transactionIdValidationInterceptor.preHandle(mockHttpServletRequest, mockHttpServletResponse, mockHandler));
        assertEquals(500,  mockHttpServletResponse.getStatus());
    }

    @Test
    void preHandleFalseForBlankString() {
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        Object mockHandler = new Object();

        var pathParams = new HashMap<String, String>();
        pathParams.put(TRANSACTION_ID_KEY, " ");
        when(mockHttpServletRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(pathParams);

        assertFalse(transactionIdValidationInterceptor.preHandle(mockHttpServletRequest, mockHttpServletResponse, mockHandler));
        assertEquals(500,  mockHttpServletResponse.getStatus());
    }

    @Test
    void preHandleFalseForFiftyPlusLengthString() {
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        Object mockHandler = new Object();

        String fiftyPlus = "123456789012345678901234567890123456789012345678901";
        var pathParams = new HashMap<String, String>();
        pathParams.put(TRANSACTION_ID_KEY, fiftyPlus);
        when(mockHttpServletRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(pathParams);

        assertFalse(transactionIdValidationInterceptor.preHandle(mockHttpServletRequest, mockHttpServletResponse, mockHandler));
        assertEquals(500,  mockHttpServletResponse.getStatus());
    }
}
