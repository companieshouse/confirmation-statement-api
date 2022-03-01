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
import java.util.regex.Pattern;

import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.COMPANY_NUMBER;
import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.ERIC_REQUEST_ID_KEY;

@Component
public class CompanyNumberValidationInterceptor implements HandlerInterceptor {

    @Value("${MAX_ID_LENGTH}")
    private String maxIdLengthString;

    @Value("${MAX_COMPANY_NUMBER_LENGTH}")
    private String maxComapnyNumberLengthString;

    @Value("${COMPANY_NUMBER_PATTERN}")
    private String confirmationNumberPattern;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        final Map<String, String> pathVariables = (Map<String, String>) request
                .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        String companyNumber = pathVariables.get(COMPANY_NUMBER);
        var reqId = request.getHeader(ERIC_REQUEST_ID_KEY);

        if (StringUtils.isBlank(companyNumber)) {
            ApiLogger.infoContext(reqId, "No company number supplied");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return false;
        }

        var maxIdLength = Integer.parseInt(maxIdLengthString);
        var truncatedNumber = (companyNumber.length() > maxIdLength) ?
                companyNumber.substring(0, maxIdLength) : companyNumber;
        var logMap = new HashMap<String, Object>();
        logMap.put(COMPANY_NUMBER, truncatedNumber);

        if (companyNumber.length() != Integer.parseInt(maxComapnyNumberLengthString)) {
            ApiLogger.infoContext(reqId, "Company number length is invalid", logMap);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return false;
        }
        var matcher = Pattern.compile(
                confirmationNumberPattern, Pattern.CASE_INSENSITIVE).matcher(companyNumber);
        if(!matcher.find()){
            ApiLogger.infoContext(reqId, "Company number contains invalid characters", logMap);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return false;
        }
        return true;
    }
}
