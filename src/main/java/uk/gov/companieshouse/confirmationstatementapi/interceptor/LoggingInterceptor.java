package uk.gov.companieshouse.confirmationstatementapi.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import uk.gov.companieshouse.confirmationstatementapi.ConfirmationStatementApiApplication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static uk.gov.companieshouse.confirmationstatementapi.ConfirmationStatementApiApplication.APP_NAME;

@Component
public class LoggingInterceptor implements HandlerInterceptor{

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Long startTime = System.currentTimeMillis();
        request.getSession().setAttribute("start-time", startTime);

        if(LOGGER.isInfoEnabled()) {
            LOGGER.info("Start of request. Method: {} Path: {}", requestMethod(request), requestPath(request));
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        Long startTime = (Long) request.getSession().getAttribute("start-time");
        long responseTime = System.currentTimeMillis() - startTime;

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("End of request. Method: {} Path: {} Duration: {}ms Status: {}",
                    requestMethod(request), requestPath(request), responseTime, response.getStatus());
        }
    }

    private String requestPath(HttpServletRequest request) {
        return request.getRequestURI();
    }

    private String requestMethod(HttpServletRequest request) {
        return request.getMethod();
    }

    private String requestId(HttpServletRequest request) {
        return request.getHeader("X-Request-Id");
    }
}
