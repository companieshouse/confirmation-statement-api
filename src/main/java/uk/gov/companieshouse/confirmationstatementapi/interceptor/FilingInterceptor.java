package uk.gov.companieshouse.confirmationstatementapi.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.transaction.TransactionStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static uk.gov.companieshouse.confirmationstatementapi.ConfirmationStatementApiApplication.LOGGER;

@Component
public class FilingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        final var transaction = (Transaction) request.getAttribute("transaction");

        if (transaction == null) {
            LOGGER.info("No transaction found in request");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return false;
        }

        if (TransactionStatus.CLOSED.equals(transaction.getStatus())) {
            LOGGER.debug(String.format("%s closed proceeding to generate filing", transaction.getId()));
            return true;
        }

        LOGGER.debug(String.format("%s not closed rejecting filing request", transaction.getId()));

        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return false;
    }
}
