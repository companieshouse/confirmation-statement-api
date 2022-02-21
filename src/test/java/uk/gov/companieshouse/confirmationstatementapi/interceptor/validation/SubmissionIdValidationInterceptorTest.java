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
import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.CONFIRMATION_STATEMENT_ID_KEY;

@ExtendWith(MockitoExtension.class)
class SubmissionIdValidationInterceptorTest {

    @Mock
    private HttpServletRequest mockHttpServletRequest;
    @InjectMocks
    private SubmissionIdValidationInterceptor submissionIdValidationInterceptor;

    @Test
    void preHandleTrueForStringWithSpecialChars() throws Exception {
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        Object mockHandler = new Object();

        String special = "1123-abc$456%def";
        var pathParams = new HashMap<String, String>();
        pathParams.put(CONFIRMATION_STATEMENT_ID_KEY, special);
        when(mockHttpServletRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(pathParams);

        assertTrue(submissionIdValidationInterceptor.preHandle(mockHttpServletRequest, mockHttpServletResponse, mockHandler));
    }

    @Test
    void preHandleTrueForFiftyLengthString() throws Exception {
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        Object mockHandler = new Object();

        String fifty = "12345678901234567890123456789012345678901234567890";
        var pathParams = new HashMap<String, String>();
        pathParams.put(CONFIRMATION_STATEMENT_ID_KEY, fifty);
        when(mockHttpServletRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(pathParams);

        assertTrue(submissionIdValidationInterceptor.preHandle(mockHttpServletRequest, mockHttpServletResponse, mockHandler));
    }

    @Test
    void preHandleFalseForFiftyPlusLengthString() {
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        Object mockHandler = new Object();

        String fiftyPlus = "123456789012345678901234567890123456789012345678901";
        var pathParams = new HashMap<String, String>();
        pathParams.put(CONFIRMATION_STATEMENT_ID_KEY, fiftyPlus);
        when(mockHttpServletRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(pathParams);

        assertFalse(submissionIdValidationInterceptor.preHandle(mockHttpServletRequest, mockHttpServletResponse, mockHandler));
        assertEquals(500,  mockHttpServletResponse.getStatus());
    }
}
