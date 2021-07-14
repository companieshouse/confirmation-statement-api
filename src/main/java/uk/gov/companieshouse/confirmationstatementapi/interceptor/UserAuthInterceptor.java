package uk.gov.companieshouse.confirmationstatementapi.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class UserAuthInterceptor implements HandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserAuthInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        final String requestUserId = request.getHeader("eric-identity");
        final var transaction = (Transaction) request.getAttribute("transaction");

        final String transactionUserId = transaction.getCreatedBy().get("id");
        if (transactionUserId == null){
            LOGGER.info("No user id found in transaction: {}", transaction.getId());
            writeErrorResponse(response);
            return false;
        }

        if (transactionUserId.equals(requestUserId)) {
            return true;
        }

        LOGGER.info("Request user id: {} does not match transaction user id {}", requestUserId, transactionUserId);
        writeErrorResponse(response);
        return false;
    }

    private void writeErrorResponse(HttpServletResponse response) throws IOException {
        response.setStatus(401);
        var out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        out.print("{\n    \"error\" : \"USER_NOT_AUTHORISED_TO_ACCESS_SUBMISSION\"\n}");
        out.flush();
    }
}
