package uk.gov.companieshouse.confirmationstatementapi.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class UserAuthInterceptor implements HandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserAuthInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        final String requestUserId = request.getHeader("eric-identity");
        final var transaction = (Transaction) request.getAttribute("transaction");

        final String transactionUserId = transaction.getCreatedBy().get("id");
        if (transactionUserId == null){
            LOGGER.info("No user id found in transaction: {}", transaction.getId());
            response.setStatus(401);
            return false;
        }

        if (transactionUserId.equals(requestUserId)) {
            return true;
        }

        LOGGER.info("Request user id: {} does not match transaction user id {}", requestUserId, transactionUserId);
        response.setStatus(401);
        return false;
    }
}
