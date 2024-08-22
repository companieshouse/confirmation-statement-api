package uk.gov.companieshouse.confirmationstatementapi.interceptor.validation;

import org.junit.jupiter.api.BeforeEach;
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
import java.util.HashMap;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.COMPANY_NUMBER;

@ExtendWith(MockitoExtension.class)
class CompanyNumberValidationInterceptorTest {

    static Stream<String> validStrings() {
        return Stream.of("11111111", "A1111111", "AB111111", "ab111111", "IP00366C");
    }

    static Stream<String> blankStrings() {
        return Stream.of("", "   ", null);
    }

    static Stream<String> invalidStrings() {
        return Stream.of("7777777", "999999999", "LONG1111111111111111111111", "AB11111111!", "$A111111", "1111111!");
    }

    @Mock
    private HttpServletRequest mockHttpServletRequest;
    @InjectMocks
    private CompanyNumberValidationInterceptor companyNumberValidationInterceptor;

    @BeforeEach
    void setEnvironment() {
        ReflectionTestUtils.setField(companyNumberValidationInterceptor, "truncationLength", 50);
        ReflectionTestUtils.setField(companyNumberValidationInterceptor, "companyNumberPattern", "^[A-Za-z0-9]{8}$");
    }

    @ParameterizedTest
    @MethodSource("validStrings")
    void preHandleTrueForValidStrings(String company) {
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        Object mockHandler = new Object();

        var pathParams = new HashMap<String, String>();
        pathParams.put(COMPANY_NUMBER, company);
        when(mockHttpServletRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(pathParams);

        assertTrue(companyNumberValidationInterceptor.preHandle(mockHttpServletRequest, mockHttpServletResponse, mockHandler));
    }

    @ParameterizedTest
    @MethodSource("invalidStrings")
    void preHandleTrueForInvalidStrings(String company) {
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        Object mockHandler = new Object();

        var pathParams = new HashMap<String, String>();
        pathParams.put(COMPANY_NUMBER, company);
        when(mockHttpServletRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(pathParams);

        assertFalse(companyNumberValidationInterceptor.preHandle(mockHttpServletRequest, mockHttpServletResponse, mockHandler));
    }

    @ParameterizedTest
    @MethodSource("blankStrings")
    void preHandleFalseForBlankString(String company) {
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        Object mockHandler = new Object();

        var pathParams = new HashMap<String, String>();
        pathParams.put(COMPANY_NUMBER, company);
        when(mockHttpServletRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(pathParams);

        assertFalse(companyNumberValidationInterceptor.preHandle(mockHttpServletRequest, mockHttpServletResponse, mockHandler));
    }

}
