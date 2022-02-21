package uk.gov.companieshouse.confirmationstatementapi.interceptor.validation;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import uk.gov.companieshouse.confirmationstatementapi.utils.ApiLogger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.CONFIRMATION_STATEMENT_ID_KEY;

@Component
public class SubmissionIdValidationInterceptor implements HandlerInterceptor {

    private static final int MAX_LENGTH = 50;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        final Map<String, String> pathVariables = (Map<String, String>) request
                .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        final var submissionId = pathVariables.get(CONFIRMATION_STATEMENT_ID_KEY);

        if (submissionId.length() > MAX_LENGTH) {
            String truncatedUrlId = submissionId.substring(0, MAX_LENGTH);
            ApiLogger.debug("Submission URL id exceeds " + MAX_LENGTH + " characters - " + truncatedUrlId + "...");
            return false;
        }

        return true;
    }
}
