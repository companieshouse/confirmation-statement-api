package uk.gov.companieshouse.confirmationstatementapi.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import uk.gov.companieshouse.confirmationstatementapi.ConfirmationStatementApiApplication;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.logging.util.RequestLogger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoggingInterceptor implements HandlerInterceptor, RequestLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfirmationStatementApiApplication.APP_NAME);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logStartRequestProcessing(request, LOGGER);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        logEndRequestProcessing(request, response, LOGGER);
    }
}
