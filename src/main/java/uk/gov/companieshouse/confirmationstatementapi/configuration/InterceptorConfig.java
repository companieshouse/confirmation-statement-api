package uk.gov.companieshouse.confirmationstatementapi.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.companieshouse.confirmationstatementapi.interceptor.TransactionInterceptor;

public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private TransactionInterceptor transactionInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(transactionInterceptor);
    }
}
