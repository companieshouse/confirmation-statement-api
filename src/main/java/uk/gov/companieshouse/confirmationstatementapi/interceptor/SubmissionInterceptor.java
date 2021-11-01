package uk.gov.companieshouse.confirmationstatementapi.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import uk.gov.companieshouse.confirmationstatementapi.exception.SubmissionNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.service.ConfirmationStatementService;
import uk.gov.companieshouse.confirmationstatementapi.utils.ApiLogger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.Map;

import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.*;

@Component
public class SubmissionInterceptor implements HandlerInterceptor {

    private final ConfirmationStatementService confirmationStatementService;

    @Autowired
    public SubmissionInterceptor(ConfirmationStatementService confirmationStatementService) {
        this.confirmationStatementService = confirmationStatementService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        final Map<String, String> pathVariables = (Map<String, String>) request
                .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        final var submissionId = pathVariables.get(CONFIRMATION_STATEMENT_ID_KEY);

        var logMap = new HashMap<String, Object>();
        String reqId = request.getHeader(ERIC_REQUEST_ID_KEY);
        try {
            confirmationStatementService.getConfirmationStatement(submissionId).isPresent();
            ApiLogger.debugContext(reqId, "Submission found", logMap);
            return true;
        } catch (SubmissionNotFoundException e) {
            ApiLogger.errorContext(reqId, e.getMessage(), e, logMap);
            response.setStatus(404);
            return false;
        }
    }
}
