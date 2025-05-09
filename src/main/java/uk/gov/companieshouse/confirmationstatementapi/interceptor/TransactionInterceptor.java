package uk.gov.companieshouse.confirmationstatementapi.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.service.TransactionService;
import uk.gov.companieshouse.confirmationstatementapi.utils.ApiLogger;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.ERIC_REQUEST_ID_KEY;
import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.TRANSACTION_ID_KEY;

@Component
public class TransactionInterceptor implements HandlerInterceptor {

    private final TransactionService transactionService;

    @Autowired
    public TransactionInterceptor(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        final Map<String, String> pathVariables = (Map<String, String>) request
                .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        final var transactionId = pathVariables.get(TRANSACTION_ID_KEY);
        String passthroughHeader = request.getHeader(ApiSdkManager.getEricPassthroughTokenHeader());

        var logMap = new HashMap<String, Object>();
        logMap.put(TRANSACTION_ID_KEY,transactionId);
        String reqId = request.getHeader(ERIC_REQUEST_ID_KEY);
        try {
            ApiLogger.debugContext(reqId, "Getting transaction for request.", logMap);

            final var transaction = transactionService.getTransaction(transactionId, passthroughHeader);
            ApiLogger.debugContext(reqId, "Transaction retrieved.", logMap);

            request.setAttribute("transaction", transaction);
            return true;
        } catch (ServiceException ex) {
            ApiLogger.errorContext(reqId,"Error retrieving transaction", ex, logMap);
            response.setStatus(500);
            return false;
        }
    }
}
