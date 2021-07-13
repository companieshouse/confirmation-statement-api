package uk.gov.companieshouse.confirmationstatementapi.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.gov.companieshouse.confirmationstatementapi.ConfirmationStatementApiApplication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoggingInterceptor implements HandlerInterceptor{

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Long startTime = System.currentTimeMillis();
        request.getSession().setAttribute("start-time", startTime);

        MDC.put("context", requestId(request));
        MDC.put("namespace", ConfirmationStatementApiApplication.APP_NAME);
        if(LOGGER.isInfoEnabled()) {
            LOGGER.info("Start of request. Method: {} Path: {}", requestMethod(request), requestPath(request));
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        Long startTime = (Long) request.getSession().getAttribute("start-time");
        long responseTime = System.currentTimeMillis() - startTime;

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("End of request. Method: {} Path: {} Duration: {}ms Status: {}",
                    requestMethod(request), requestPath(request), responseTime, response.getStatus());
        }

        MDC.clear();
    }

    private String requestPath(HttpServletRequest request) {
        return (String) request
                .getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
    }

    private String requestMethod(HttpServletRequest request) {
        return request.getMethod();
    }

    private String requestId(HttpServletRequest request) {
        return request.getHeader("X-request-id");
    }
}
