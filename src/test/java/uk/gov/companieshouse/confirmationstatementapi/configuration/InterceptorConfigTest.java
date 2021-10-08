package uk.gov.companieshouse.confirmationstatementapi.configuration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import uk.gov.companieshouse.api.interceptor.CRUDAuthenticationInterceptor;
import uk.gov.companieshouse.api.interceptor.InternalUserInterceptor;
import uk.gov.companieshouse.confirmationstatementapi.interceptor.FilingInterceptor;
import uk.gov.companieshouse.confirmationstatementapi.interceptor.LoggingInterceptor;
import uk.gov.companieshouse.confirmationstatementapi.interceptor.TransactionInterceptor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InterceptorConfigTest {

    @Mock
    private InterceptorRegistry interceptorRegistry;

    @Mock
    private InterceptorRegistration interceptorRegistration;

    @Mock
    private TransactionInterceptor transactionInterceptor;

    @Mock
    private LoggingInterceptor loggingInterceptor;

    @Mock
    private FilingInterceptor filingInterceptor;

    @Mock
    private InternalUserInterceptor internalUserInterceptor;

    @InjectMocks
    private InterceptorConfig interceptorConfig;

    @Test
    void addInterceptorsTest() {
        when(interceptorRegistry.addInterceptor(any())).thenReturn(interceptorRegistration);

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
        inOrder.verify(interceptorRegistration).addPathPatterns(InterceptorConfig.TRANSACTIONS, InterceptorConfig.FILINGS);

        // Filings endpoint interceptor check
        inOrder.verify(interceptorRegistry).addInterceptor(filingInterceptor);
        inOrder.verify(interceptorRegistration).addPathPatterns(InterceptorConfig.FILINGS);
    }
}
