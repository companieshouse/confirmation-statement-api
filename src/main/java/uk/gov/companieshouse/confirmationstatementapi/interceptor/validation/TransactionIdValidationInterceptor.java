package uk.gov.companieshouse.confirmationstatementapi.interceptor.validation;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import uk.gov.companieshouse.confirmationstatementapi.utils.ApiLogger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.ERIC_REQUEST_ID_KEY;
import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.TRANSACTION_ID_KEY;

@Component
public class TransactionIdValidationInterceptor implements HandlerInterceptor {

    @Value("${MAX_ID_LENGTH}")
    private int maxIdLength;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        final Map<String, String> pathVariables = (Map<String, String>) request
                .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        final var transactionId = pathVariables.get(TRANSACTION_ID_KEY);
        var reqId = request.getHeader(ERIC_REQUEST_ID_KEY);

        if (StringUtils.isBlank(transactionId)) {
            ApiLogger.infoContext(reqId, "No transaction URL id supplied");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return false;
        }

        if (transactionId.length() > maxIdLength) {
            var truncatedUrlId = transactionId.substring(0, maxIdLength);
            var logMap = new HashMap<String, Object>();
            logMap.put(TRANSACTION_ID_KEY, truncatedUrlId);
            ApiLogger.infoContext(reqId, "Transaction URL id exceeds " + maxIdLength + " characters", logMap);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return false;
        }

        return true;
    }
}
