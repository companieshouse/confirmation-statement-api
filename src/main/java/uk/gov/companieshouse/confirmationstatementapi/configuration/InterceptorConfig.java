package uk.gov.companieshouse.confirmationstatementapi.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.companieshouse.confirmationstatementapi.interceptor.LoggingInterceptor;
import uk.gov.companieshouse.confirmationstatementapi.interceptor.TransactionInterceptor;
import uk.gov.companieshouse.confirmationstatementapi.interceptor.UserAuthInterceptor;

@Component
public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private TransactionInterceptor transactionInterceptor;

    @Autowired
    private LoggingInterceptor loggingInterceptor;

    @Autowired
    private UserAuthInterceptor userAuthInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loggingInterceptor);
        registry.addInterceptor(transactionInterceptor).addPathPatterns("/transactions/**");
        registry.addInterceptor(userAuthInterceptor).addPathPatterns("/transactions/**");
    }
}
