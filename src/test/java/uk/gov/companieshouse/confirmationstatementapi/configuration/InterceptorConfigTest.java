package uk.gov.companieshouse.confirmationstatementapi.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.handler.MappedInterceptor;
import org.springframework.web.util.ServletRequestPathUtils;

import uk.gov.companieshouse.api.interceptor.CRUDAuthenticationInterceptor;
import uk.gov.companieshouse.api.interceptor.InternalUserInterceptor;
import uk.gov.companieshouse.confirmationstatementapi.interceptor.FilingInterceptor;
import uk.gov.companieshouse.confirmationstatementapi.interceptor.LoggingInterceptor;
import uk.gov.companieshouse.confirmationstatementapi.interceptor.SubmissionInterceptor;
import uk.gov.companieshouse.confirmationstatementapi.interceptor.TransactionInterceptor;
import uk.gov.companieshouse.confirmationstatementapi.interceptor.validation.CompanyNumberValidationInterceptor;
import uk.gov.companieshouse.confirmationstatementapi.interceptor.validation.SubmissionIdValidationInterceptor;
import uk.gov.companieshouse.confirmationstatementapi.interceptor.validation.TransactionIdValidationInterceptor;

@ExtendWith(MockitoExtension.class)
class InterceptorConfigTest {

    @Mock
    private InterceptorRegistry interceptorRegistry;

    @Mock
    private InterceptorRegistration interceptorRegistration;

    @Mock
    private CompanyNumberValidationInterceptor companyNumberValidationInterceptor;

    @Mock
    private TransactionIdValidationInterceptor transactionIdValidationInterceptor;

    @Mock
    private TransactionInterceptor transactionInterceptor;

    @Mock
    private LoggingInterceptor loggingInterceptor;

    @Mock
    private FilingInterceptor filingInterceptor;

    @Mock
    private InternalUserInterceptor internalUserInterceptor;

    @Mock
    private SubmissionIdValidationInterceptor submissionIdValidationInterceptor;

    @Mock
    private SubmissionInterceptor submissionInterceptor;

    @Mock
    private CRUDAuthenticationInterceptor crudAuthenticationInterceptor;

    @InjectMocks
    private InterceptorConfig interceptorConfig = new InterceptorConfig();

    private InterceptorRegistryExaminer interceptorRegistryExaminer;

    private static final String CONTEXT_PATH = "/APP-ROOT";

    @Test
    void addInterceptorsTest() {
        when(interceptorRegistry.addInterceptor(any())).thenReturn(interceptorRegistration);
        when(interceptorRegistration.addPathPatterns(any(String.class))).thenReturn(interceptorRegistration);

        interceptorConfig.addInterceptors(interceptorRegistry);

        InOrder inOrder = inOrder(interceptorRegistry, interceptorRegistration);

        // logging interceptor check
        inOrder.verify(interceptorRegistry).addInterceptor(loggingInterceptor);

        // User auth CRUD interceptor check
        inOrder.verify(interceptorRegistry).addInterceptor(any(CRUDAuthenticationInterceptor.class));
        inOrder.verify(interceptorRegistration).addPathPatterns(InterceptorConfig.USER_AUTH_ENDPOINTS);

        // Internal User auth interceptor check
        inOrder.verify(interceptorRegistry).addInterceptor(internalUserInterceptor);
        inOrder.verify(interceptorRegistration).addPathPatterns(InterceptorConfig.INTERNAL_AUTH_ENDPOINTS);

        // Company auth CRUD interceptor check
        inOrder.verify(interceptorRegistry).addInterceptor(any(CRUDAuthenticationInterceptor.class));
        inOrder.verify(interceptorRegistration).excludePathPatterns(InterceptorConfig.NOT_COMPANY_AUTH_ENDPOINTS);

        // Transactions endpoints interceptor check
        inOrder.verify(interceptorRegistry).addInterceptor(transactionInterceptor);
        inOrder.verify(interceptorRegistration).addPathPatterns(InterceptorConfig.TRANSACTIONS);

        // Filings endpoint interceptor check
        inOrder.verify(interceptorRegistry).addInterceptor(filingInterceptor);
        inOrder.verify(interceptorRegistration).addPathPatterns(InterceptorConfig.FILINGS);
    }

    @Test
    void addInterceptorsWithRegistryCheckTest() {

        Map<String,Set<HandlerInterceptor>> testCases = new HashMap<>();
        Set<HandlerInterceptor> COMPANY_NUMBER_INTERCEPTORS =
            Set.of(loggingInterceptor, crudAuthenticationInterceptor, companyNumberValidationInterceptor);
        Set<HandlerInterceptor> TRANSACTION_INTERCEPTORS =
            Set.of(loggingInterceptor, crudAuthenticationInterceptor, transactionInterceptor, transactionIdValidationInterceptor);
        Set<HandlerInterceptor> CS_ID_INTERCEPTORS =
            Set.of(loggingInterceptor, crudAuthenticationInterceptor, transactionInterceptor, transactionIdValidationInterceptor,
            submissionInterceptor, submissionIdValidationInterceptor);

        // company number
        testCases.put("/confirmation-statement/company/{company_number}/eligibility", COMPANY_NUMBER_INTERCEPTORS);
        testCases.put("/confirmation-statement/company/{company-number}/next-made-up-to-date", COMPANY_NUMBER_INTERCEPTORS);
        // txn id
        testCases.put("/transactions/{transaction_id}/confirmation-statement", TRANSACTION_INTERCEPTORS);
        // txn id + cs id
        testCases.put("/transactions/{transaction_id}/confirmation-statement/{confirmation_statement_id}", CS_ID_INTERCEPTORS);
        testCases.put("/transactions/{transaction_id}/confirmation-statement/{confirmation_statement_id}/validation-status", CS_ID_INTERCEPTORS);
        testCases.put("/transactions/{transaction_id}/confirmation-statement/{confirmation_statement_id}/statement-of-capital", CS_ID_INTERCEPTORS);
        testCases.put("/transactions/{transaction_id}/confirmation-statement/{confirmation_statement_id}/active-director-details", CS_ID_INTERCEPTORS);
        testCases.put("/transactions/{transaction_id}/confirmation-statement/{confirmation_statement_id}/persons-of-significant-control", CS_ID_INTERCEPTORS);
        testCases.put("/transactions/{transaction_id}/confirmation-statement/{confirmation_statement_id}/shareholders", CS_ID_INTERCEPTORS);
        testCases.put("/transactions/{transaction_id}/confirmation-statement/{confirmation_statement_id}/register-locations", CS_ID_INTERCEPTORS);
        // internal endpoints
        testCases.put("/private/transactions/{transaction_id}/confirmation-statement/{confirmation_statement_id}/filings",
            Set.of(loggingInterceptor, internalUserInterceptor, transactionInterceptor, filingInterceptor));
        testCases.put("/transactions/{transaction_id}/confirmation-statement/{confirmation_statement_id}/costs",
            Set.of(loggingInterceptor, internalUserInterceptor, transactionInterceptor, transactionIdValidationInterceptor, submissionInterceptor, submissionIdValidationInterceptor));
        testCases.put("/private/confirmation-statement/company/{company-number}/registered-email-address",
            Set.of(loggingInterceptor, internalUserInterceptor, companyNumberValidationInterceptor));

        interceptorRegistryExaminer = new InterceptorRegistryExaminer();
        interceptorConfig.addInterceptors(interceptorRegistryExaminer);
        List<Object> interceptors = interceptorRegistryExaminer.getInterceptors();

        for (String requestPath : testCases.keySet()){

            MockHttpServletRequest request = new MockHttpServletRequest("GET", CONTEXT_PATH+requestPath);
            request.setContextPath(CONTEXT_PATH);
            ServletRequestPathUtils.parseAndCache(request);

            Set<HandlerInterceptor> foundInterceptors = new HashSet<>();
            for  (Object i : interceptors){
                if (i instanceof org.springframework.web.servlet.handler.MappedInterceptor) {
                    MappedInterceptor mi = (MappedInterceptor)i;
                    if (mi.matches(request)){
                        if (mi.getInterceptor() instanceof CRUDAuthenticationInterceptor){
                            // workaround - using our mock so it matches
                            // this does NOT check what token (user profile/company cs) it is set up for
                            foundInterceptors.add(crudAuthenticationInterceptor);
                        } else foundInterceptors.add(mi.getInterceptor());
                    }
                } else {
                    foundInterceptors.add((HandlerInterceptor) i);
                }
            }

            assertEquals(testCases.get(requestPath), foundInterceptors, "Interceptors not as expected for path "+requestPath);

        }
    }

    // class created to allow calling of protected getInterceptors method
    private class InterceptorRegistryExaminer extends InterceptorRegistry {

        public List<Object> getInterceptors() {
            return super.getInterceptors();
        }

    }

}
