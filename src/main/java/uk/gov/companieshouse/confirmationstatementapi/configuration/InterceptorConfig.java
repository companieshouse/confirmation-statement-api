package uk.gov.companieshouse.confirmationstatementapi.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.companieshouse.confirmationstatementapi.interceptor.FilingInterceptor;
import uk.gov.companieshouse.confirmationstatementapi.interceptor.LoggingInterceptor;
import uk.gov.companieshouse.confirmationstatementapi.interceptor.TransactionInterceptor;
import uk.gov.companieshouse.confirmationstatementapi.interceptor.UserAuthInterceptor;

@Component
public class InterceptorConfig implements WebMvcConfigurer {

    private static final String TRANSACTIONS = "/transactions/**";
    private static final String PRIVATE_TRANSACTIONS = "/private" + TRANSACTIONS;
    @Autowired
    private TransactionInterceptor transactionInterceptor;

    @Autowired
    private LoggingInterceptor loggingInterceptor;

    @Autowired
    private UserAuthInterceptor userAuthInterceptor;

    @Autowired
    private FilingInterceptor filingInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loggingInterceptor);
        registry.addInterceptor(transactionInterceptor).addPathPatterns(TRANSACTIONS, PRIVATE_TRANSACTIONS);
        registry.addInterceptor(userAuthInterceptor).addPathPatterns(TRANSACTIONS, PRIVATE_TRANSACTIONS);
        registry.addInterceptor(filingInterceptor).addPathPatterns(PRIVATE_TRANSACTIONS);
    }
}
