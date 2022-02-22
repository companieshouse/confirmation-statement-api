package uk.gov.companieshouse.confirmationstatementapi.interceptor.validation;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import uk.gov.companieshouse.confirmationstatementapi.utils.ApiLogger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.CONFIRMATION_STATEMENT_ID_KEY;
import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.ERIC_REQUEST_ID_KEY;

@Component
public class SubmissionIdValidationInterceptor implements HandlerInterceptor {

    private static final int MAX_LENGTH = 50;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        final Map<String, String> pathVariables = (Map<String, String>) request
                .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        final var submissionId = pathVariables.get(CONFIRMATION_STATEMENT_ID_KEY);
        var reqId = request.getHeader(ERIC_REQUEST_ID_KEY);

        if (StringUtils.isBlank(submissionId)) {
            ApiLogger.infoContext(reqId,"No submission URL id supplied");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return false;
        }

        if (submissionId.length() > MAX_LENGTH) {
            var truncatedUrlId = submissionId.substring(0, MAX_LENGTH);
            ApiLogger.infoContext(reqId, "Submission URL id exceeds " + MAX_LENGTH + " characters - " + truncatedUrlId + "...");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return false;
        }

        return true;
    }
}
