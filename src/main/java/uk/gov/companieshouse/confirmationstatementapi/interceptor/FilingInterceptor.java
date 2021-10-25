package uk.gov.companieshouse.confirmationstatementapi.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.transaction.TransactionStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

import static uk.gov.companieshouse.confirmationstatementapi.ConfirmationStatementApiApplication.LOGGER;
import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.ERIC_REQUEST_ID_KEY;
import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.TRANSACTION_ID_KEY;

@Component
public class FilingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        final var transaction = (Transaction) request.getAttribute("transaction");
        final String reqId = request.getHeader(ERIC_REQUEST_ID_KEY);

        if (transaction == null) {
            LOGGER.infoContext(reqId, "No transaction found in request", null);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return false;
        }

        var logMap = new HashMap<String, Object>();
        logMap.put(TRANSACTION_ID_KEY, transaction.getId());

        if (TransactionStatus.CLOSED.equals(transaction.getStatus())) {
            LOGGER.debugContext(reqId, "Closed proceeding to generate filing", logMap);
            return true;
        }

        LOGGER.debugContext(reqId, "Closed rejecting filing request", logMap);

        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return false;
    }
}
