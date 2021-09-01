package uk.gov.companieshouse.confirmationstatementapi.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.util.security.AuthorisationUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.HttpMethod.GET;

@Component
public class UserAuthInterceptor implements HandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserAuthInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        final var transaction = (Transaction) request.getAttribute("transaction");
        final String identityType = request.getHeader("eric-identity-type");
        var isApiKeyRequest = false;
        if (identityType != null) {
            isApiKeyRequest = identityType.equals("key");
        }

        if (isApiKeyRequest) {
            return validateAPI(request, response);
        }

        final String requestUserId = request.getHeader("eric-identity");

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

    private boolean validateAPI(HttpServletRequest request, HttpServletResponse response){
        if(AuthorisationUtil.hasInternalUserRole(request) && GET.matches(request.getMethod())) {
            LOGGER.info("internal API is permitted to view the resource");
            return true;
        } else {
            LOGGER.info("API is not permitted to perform a {}", request.getMethod());
            response.setStatus(401);
            return false;
        }
    }
}
