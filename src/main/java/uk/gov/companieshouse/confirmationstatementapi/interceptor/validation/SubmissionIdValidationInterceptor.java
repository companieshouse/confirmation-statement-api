package uk.gov.companieshouse.confirmationstatementapi.interceptor.validation;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import uk.gov.companieshouse.confirmationstatementapi.utils.ApiLogger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.CONFIRMATION_STATEMENT_ID_KEY;
import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.ERIC_REQUEST_ID_KEY;
import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.MAX_ID_LENGTH;

@Component
public class SubmissionIdValidationInterceptor implements HandlerInterceptor {

    @Override
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

        if (submissionId.length() > MAX_ID_LENGTH) {
            var truncatedUrlId = submissionId.substring(0, MAX_ID_LENGTH);
            var logMap = new HashMap<String, Object>();
            logMap.put(CONFIRMATION_STATEMENT_ID_KEY, truncatedUrlId);
            ApiLogger.infoContext(reqId, "Submission URL id exceeds " + MAX_ID_LENGTH + " characters.", logMap);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return false;
        }
        return true;
    }
}
