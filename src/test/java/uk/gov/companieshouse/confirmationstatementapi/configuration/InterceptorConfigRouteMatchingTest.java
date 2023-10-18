package uk.gov.companieshouse.confirmationstatementapi.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class InterceptorConfigRouteMatchingTest {

    @Autowired
    private RequestMappingHandlerMapping mapping;

    @Test
    void interceptorsMatchIntendedRoutesTest() throws Exception {

        Map<String,Set<String>> testCases = new HashMap<>();
        Set<String> COMPANY_NUMBER_INTERCEPTORS =
            Set.of("LoggingInterceptor", "CRUDAuthenticationInterceptor", "CompanyNumberValidationInterceptor");
        Set<String> TRANSACTION_INTERCEPTORS =
            Set.of("LoggingInterceptor", "CRUDAuthenticationInterceptor", "TransactionInterceptor", "TransactionIdValidationInterceptor");
        Set<String> CS_ID_INTERCEPTORS =
            Set.of("LoggingInterceptor", "CRUDAuthenticationInterceptor", "TransactionInterceptor", "TransactionIdValidationInterceptor",
                "SubmissionInterceptor", "SubmissionIdValidationInterceptor");

        // company number
        testCases.put("/confirmation-statement/company/12345/eligibility", COMPANY_NUMBER_INTERCEPTORS);
        testCases.put("/confirmation-statement/company/12345/next-made-up-to-date", COMPANY_NUMBER_INTERCEPTORS);
        // txn id
        testCases.put("/transactions/12345/confirmation-statement/", TRANSACTION_INTERCEPTORS); // note trailing slash required
        // txn id + cs id
        testCases.put("/transactions/12345/confirmation-statement/12345", CS_ID_INTERCEPTORS);
        testCases.put("/transactions/12345/confirmation-statement/12345/validation-status", CS_ID_INTERCEPTORS);
        testCases.put("/transactions/12345/confirmation-statement/12345/statement-of-capital", CS_ID_INTERCEPTORS);
        testCases.put("/transactions/12345/confirmation-statement/12345/active-director-details", CS_ID_INTERCEPTORS);
        testCases.put("/transactions/12345/confirmation-statement/12345/persons-of-significant-control", CS_ID_INTERCEPTORS);
        testCases.put("/transactions/12345/confirmation-statement/12345/shareholders", CS_ID_INTERCEPTORS);
        testCases.put("/transactions/12345/confirmation-statement/12345/register-locations", CS_ID_INTERCEPTORS);
        // internal endpoints
        testCases.put("/private/transactions/12345/confirmation-statement/12345/filings",
            Set.of("LoggingInterceptor", "InternalUserInterceptor", "TransactionInterceptor", "FilingInterceptor"));
        testCases.put("/transactions/12345/confirmation-statement/12345/costs",
            Set.of("LoggingInterceptor", "InternalUserInterceptor", "TransactionInterceptor", "TransactionIdValidationInterceptor", 
                "SubmissionInterceptor", "SubmissionIdValidationInterceptor"));
        testCases.put("/private/confirmation-statement/company/12345/registered-email-address",
            Set.of("LoggingInterceptor", "InternalUserInterceptor", "CompanyNumberValidationInterceptor"));

        for (String requestPath : testCases.keySet()){

            Set<String> expectedInterceptors = testCases.get(requestPath);

            MockHttpServletRequest request = new MockHttpServletRequest("GET", requestPath);
            HandlerExecutionChain chain;
            try {
                 chain = mapping.getHandler(request);
            } catch (HttpRequestMethodNotSupportedException e) {
                request = new MockHttpServletRequest("POST", requestPath);
                chain = mapping.getHandler(request);
            }
            assertNotNull(chain, "No handler found for path "+requestPath);

            Set<String> foundInterceptors = chain.getInterceptorList()
                .stream()
                .map((s) -> s.getClass())
                .filter((s) -> s.getPackageName().startsWith("uk.gov.companieshouse"))
                .map((s) -> s.getSimpleName())
                .collect(Collectors.toSet());

            assertEquals(expectedInterceptors, foundInterceptors, "Interceptors not as expected for path "+requestPath);
        }
    }
}
