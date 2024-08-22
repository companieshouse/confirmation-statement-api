package uk.gov.companieshouse.confirmationstatementapi.interceptor.validation;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import uk.gov.companieshouse.confirmationstatementapi.utils.ApiLogger;
import uk.gov.companieshouse.confirmationstatementapi.utils.InputProcessor;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.CONFIRMATION_STATEMENT_ID_KEY;
import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.ERIC_REQUEST_ID_KEY;

@Component
public class SubmissionIdValidationInterceptor implements HandlerInterceptor {

    @Value("${MAX_ID_LENGTH}")
    private int maxIdLength;

    @Value("${SUBMISSION_ID_REGEX_PATTERN}")
    private String submissionIdRegexPattern;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        final Map<String, String> pathVariables = (Map<String, String>) request
                .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        final var submissionId = pathVariables.get(CONFIRMATION_STATEMENT_ID_KEY);
        var reqId = request.getHeader(ERIC_REQUEST_ID_KEY);

        if (StringUtils.isBlank(submissionId)) {
            ApiLogger.infoContext(reqId, "No submission URL id supplied");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return false;
        }

        if (submissionId.length() > maxIdLength) {
            var processedUrlId = InputProcessor.sanitiseString(submissionId, submissionIdRegexPattern);
            processedUrlId = processedUrlId.substring(0, maxIdLength).concat("...");
            var logMap = new HashMap<String, Object>();
            logMap.put(CONFIRMATION_STATEMENT_ID_KEY, processedUrlId);
            ApiLogger.infoContext(reqId, "Submission URL id exceeds " + maxIdLength + " characters", logMap);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return false;
        }
        return true;
    }
}
