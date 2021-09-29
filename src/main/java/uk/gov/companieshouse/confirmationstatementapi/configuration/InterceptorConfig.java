package uk.gov.companieshouse.confirmationstatementapi.configuration;

import static uk.gov.companieshouse.api.util.security.Permission.Key.COMPANY_CONFIRMATION_STATEMENT;
import static uk.gov.companieshouse.api.util.security.Permission.Key.USER_PROFILE;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.companieshouse.api.interceptor.CRUDAuthenticationInterceptor;
import uk.gov.companieshouse.api.interceptor.InternalUserInterceptor;
import uk.gov.companieshouse.confirmationstatementapi.interceptor.FilingInterceptor;
import uk.gov.companieshouse.confirmationstatementapi.interceptor.LoggingInterceptor;
import uk.gov.companieshouse.confirmationstatementapi.interceptor.TransactionInterceptor;

@Configuration
@ComponentScan("uk.gov.companieshouse.api.interceptor")
public class InterceptorConfig implements WebMvcConfigurer {

    private static final String TRANSACTIONS = "/transactions/**";
    private static final String FILINGS = "/private/**/filings";
    private static final String NEXT_MADE_UP_TO_DATE = "/confirmation-statement/**/next-made-up-to-date";
    private static final String ELIGIBILITY = "/confirmation-statement/**/eligibility";
    private static final String COSTS = TRANSACTIONS + "/costs";

    private static final String[] USER_AUTH_ENDPOINTS = {
            NEXT_MADE_UP_TO_DATE,
            ELIGIBILITY
    };
    private static final String[] INTERNAL_AUTH_ENDPOINTS = {
            FILINGS,
            COSTS
    };

    @Autowired
    private TransactionInterceptor transactionInterceptor;

    @Autowired
    private LoggingInterceptor loggingInterceptor;

    @Autowired
    private FilingInterceptor filingInterceptor;

    @Autowired
    private InternalUserInterceptor internalUserInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        addLoggingInterceptor(registry);

        addUserAuthenticationEndpointsInterceptor(registry);

        addInternalUserAuthenticationEndpointsInterceptor(registry);

        addCompanyAuthenticatedEndpointsInterceptor(registry);

        addTransactionInterceptor(registry);

        addFilingsEndpointInterceptor(registry);
    }

    private void addLoggingInterceptor(InterceptorRegistry registry) {
        registry.addInterceptor(loggingInterceptor);
    }

    /**
     * Interceptor to authenticate access to specified endpoints using user permissions
     * @param registry
     */
    private void addUserAuthenticationEndpointsInterceptor(InterceptorRegistry registry) {
        registry.addInterceptor(getUserCrudAuthenticationInterceptor())
                .addPathPatterns(USER_AUTH_ENDPOINTS);
    }

    /**
     * Interceptor to authenticate access to specified endpoints using internal permissions
     * @param registry
     */
    private void addInternalUserAuthenticationEndpointsInterceptor(InterceptorRegistry registry) {
        registry.addInterceptor(internalUserInterceptor)
                .addPathPatterns(INTERNAL_AUTH_ENDPOINTS);
    }

    /**
     * Interceptor to authenticate access to specified endpoints using company permissions
     * @param registry
     */
    private void addCompanyAuthenticatedEndpointsInterceptor(InterceptorRegistry registry) {
        registry.addInterceptor(getCompanyCrudAuthenticationInterceptor())
                .excludePathPatterns((String[]) ArrayUtils.addAll(USER_AUTH_ENDPOINTS, INTERNAL_AUTH_ENDPOINTS));
    }

    /**
     *  Interceptor to get transaction and put in request for endpoints that require a transaction
     * @param registry
     */
    private void addTransactionInterceptor(InterceptorRegistry registry) {
        registry.addInterceptor(transactionInterceptor).addPathPatterns(TRANSACTIONS);
    }

    /**
     * Interceptor to check specific conditions for the /filings endpoint
     * @param registry
     */
    private void addFilingsEndpointInterceptor(InterceptorRegistry registry) {
        registry.addInterceptor(filingInterceptor).addPathPatterns(FILINGS);
    }

    private CRUDAuthenticationInterceptor getUserCrudAuthenticationInterceptor() {
        return new CRUDAuthenticationInterceptor(USER_PROFILE);
    }

    private CRUDAuthenticationInterceptor getCompanyCrudAuthenticationInterceptor() {
        return new CRUDAuthenticationInterceptor(COMPANY_CONFIRMATION_STATEMENT);
    }
}
