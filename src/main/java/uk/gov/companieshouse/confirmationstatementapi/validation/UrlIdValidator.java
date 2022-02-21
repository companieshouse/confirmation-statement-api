package uk.gov.companieshouse.confirmationstatementapi.validation;

import org.apache.commons.lang.StringUtils;
import uk.gov.companieshouse.confirmationstatementapi.utils.ApiLogger;

public class UrlIdValidator {

    private static final int MAX_LENGTH = 50;

    public static boolean isUrlIdValid(String idName, String urlId) {

        if (StringUtils.isBlank(urlId)) {
          ApiLogger.debug("No " + idName + " URL id supplied");
          return false;
        }

        if (urlId.length() > MAX_LENGTH) {
            String truncatedUrlId = urlId.substring(0, MAX_LENGTH);
            ApiLogger.debug(idName + " URL id exceeds " + MAX_LENGTH + " characters - " + truncatedUrlId + "...");
            return false;
        }

        return true;
    }
}
