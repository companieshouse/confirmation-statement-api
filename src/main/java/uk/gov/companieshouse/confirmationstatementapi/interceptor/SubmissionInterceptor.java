package uk.gov.companieshouse.confirmationstatementapi.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import uk.gov.companieshouse.confirmationstatementapi.model.json.ConfirmationStatementSubmissionJson;
import uk.gov.companieshouse.confirmationstatementapi.service.ConfirmationStatementService;
import uk.gov.companieshouse.confirmationstatementapi.utils.ApiLogger;
import uk.gov.companieshouse.confirmationstatementapi.utils.InputProcessor;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.*;

@Component
public class SubmissionInterceptor implements HandlerInterceptor {

    @Value("${SUBMISSION_ID_REGEX_PATTERN}")
    private String submissionIdRegexPattern;

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
        final var transactionId = pathVariables.get(TRANSACTION_ID_KEY);

        var logMap = new HashMap<String, Object>();
        logMap.put(CONFIRMATION_STATEMENT_ID_KEY, InputProcessor.sanitiseString(submissionId, submissionIdRegexPattern));
        logMap.put(TRANSACTION_ID_KEY, transactionId);
        String reqId = request.getHeader(ERIC_REQUEST_ID_KEY);

        Optional<ConfirmationStatementSubmissionJson> submission = confirmationStatementService.getConfirmationStatement(submissionId);

        if (!submission.isPresent()){
            ApiLogger.infoContext(reqId, "Confirmation statement submission does not exists", logMap);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return false;
        }

        boolean containsTransaction = submission.get().getLinks().get("self").contains(transactionId);

        if (containsTransaction) {
            ApiLogger.infoContext(reqId, "Confirmation statement submission found for " + submissionId, logMap);
            request.setAttribute("submission", submission);
            return true;
        } else {
            ApiLogger.infoContext(reqId, "Confirmation statement submission does not belong to the transaction", logMap);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return false;
        }
    }
}
