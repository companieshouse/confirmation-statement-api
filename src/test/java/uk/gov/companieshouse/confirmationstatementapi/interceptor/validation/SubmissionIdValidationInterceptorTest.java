package uk.gov.companieshouse.confirmationstatementapi.interceptor.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.HandlerMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.CONFIRMATION_STATEMENT_ID_KEY;
import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.TRANSACTION_ID_KEY;

@ExtendWith(MockitoExtension.class)
class SubmissionIdValidationInterceptorTest {

    static Stream<String> blankStrings() {
        return Stream.of("", "   ", null);
    }

    @Mock
    private HttpServletRequest mockHttpServletRequest;
    @InjectMocks
    private SubmissionIdValidationInterceptor submissionIdValidationInterceptor;

    @BeforeEach
    void setEnvironment() {
        ReflectionTestUtils.setField(submissionIdValidationInterceptor, "maxIdLength", 50);
        ReflectionTestUtils.setField(submissionIdValidationInterceptor, "submissionIdRegexPattern", "[^A-Za-z\\d -]");
    }

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

    @ParameterizedTest
    @MethodSource("blankStrings")
    void preHandleFalseForBlankString(String input) {
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        Object mockHandler = new Object();

        var pathParams = new HashMap<String, String>();
        pathParams.put(TRANSACTION_ID_KEY, input);
        when(mockHttpServletRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(pathParams);

        assertFalse(submissionIdValidationInterceptor.preHandle(mockHttpServletRequest, mockHttpServletResponse, mockHandler));
        assertEquals(HttpServletResponse.SC_BAD_REQUEST,  mockHttpServletResponse.getStatus());
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
        assertEquals(HttpServletResponse.SC_BAD_REQUEST,  mockHttpServletResponse.getStatus());
    }
}
