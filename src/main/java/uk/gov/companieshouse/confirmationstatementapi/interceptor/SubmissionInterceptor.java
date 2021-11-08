package uk.gov.companieshouse.confirmationstatementapi.interceptor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.model.json.ConfirmationStatementSubmissionJson;
import uk.gov.companieshouse.confirmationstatementapi.service.ConfirmationStatementService;
import uk.gov.companieshouse.confirmationstatementapi.utils.ApiLogger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.*;

@Component
public class SubmissionInterceptor implements HandlerInterceptor {

    private final ConfirmationStatementService confirmationStatementService;

    @Autowired
    public SubmissionInterceptor(ConfirmationStatementService confirmationStatementService) {
        this.confirmationStatementService = confirmationStatementService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        final Map<String, String> pathVariables = (Map<String, String>) request
                .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        final var submissionId = pathVariables.get(CONFIRMATION_STATEMENT_ID_KEY);

        var logMap = new HashMap<String, Object>();
        String reqId = request.getHeader(ERIC_REQUEST_ID_KEY);

        if (StringUtils.isBlank(submissionId)) {
            logMap.put(CONFIRMATION_STATEMENT_ID_KEY, submissionId);
            logMap.put(ApiLogger.LOG_MAP_KEY_HTTP_REQUEST_URL, request.getRequestURI());
            var exceptionMessage = String.format("SubmissionInterceptor - %s is blank", CONFIRMATION_STATEMENT_ID_KEY);
            ApiLogger.infoContext(reqId, exceptionMessage, logMap);
            throw new ServiceException(exceptionMessage);
        }

        Optional<ConfirmationStatementSubmissionJson> submission = confirmationStatementService.getConfirmationStatement(submissionId);
        boolean isPresent = submission.isPresent();
        if (isPresent) {
            ApiLogger.infoContext(reqId, "Submission found for " + submissionId, logMap);
            request.setAttribute("submission", submission);
            return true;
        } else {
            ApiLogger.infoContext(reqId, "Submission not found for " + submissionId, logMap);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return false;
        }
    }
}
