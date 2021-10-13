package uk.gov.companieshouse.confirmationstatementapi.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.service.TransactionService;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import static uk.gov.companieshouse.confirmationstatementapi.ConfirmationStatementApiApplication.LOGGER;

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
        final var transactionId = pathVariables.get("transaction_id");
        String passthroughHeader = request.getHeader(ApiSdkManager.getEricPassthroughTokenHeader());

        var logMap = new HashMap<String, Object>();
        logMap.put("transaction_id",transactionId);
        try {
            LOGGER.debug("Getting transaction for request.", logMap);
            final var transaction = transactionService.getTransaction(transactionId, passthroughHeader);
            LOGGER.debug("Transaction retrieved.", logMap);

            request.setAttribute("transaction", transaction);
            return true;
        } catch (ServiceException ex) {
            LOGGER.error("Error retrieving transaction", ex);
            response.setStatus(500);
            return false;
        }
    }
}
