package uk.gov.companieshouse.confirmationstatementapi.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.service.TransactionService;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Component
public class TransactionInterceptor implements HandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionInterceptor.class);

    private final TransactionService transactionService;

    @Autowired
    public TransactionInterceptor(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        final Map<String, String> pathVariables = (Map<String, String>) request
                .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        final var transactionId = pathVariables.get("transaction_id");
        String passthroughHeader = request
                .getHeader(ApiSdkManager.getEricPassthroughTokenHeader());
        try {
            LOGGER.debug("Getting transaction for request");
            final var transaction = transactionService.getTransaction(transactionId, passthroughHeader);
            LOGGER.debug("Transaction retrieved: {}", transaction);
            request.setAttribute("transaction", transaction);
            return true;
        } catch (ServiceException ex) {
            LOGGER.error("Error retrieving transaction", ex);
            response.setStatus(500);
            return false;
        }
    }
}
