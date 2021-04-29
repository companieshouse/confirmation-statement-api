package uk.gov.companieshouse.confirmationstatementapi.common;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.confirmationstatementapi.ConfirmationStatementApiApplication;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Component
public class ApiLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfirmationStatementApiApplication.APP_NAMESPACE);

    public void debugContext(String context, String message, Map<String, Object> dataMap) {
        LOGGER.debugContext(context, message, cloneMapData(dataMap));
    }

    public void info(String message) {
        LOGGER.info(message, null);
    }

    public void infoContext(String context, String message, Map<String, Object> dataMap) {
        LOGGER.infoContext(context, message, cloneMapData(dataMap));
    }

    public void errorContext(String context, String message, Exception e, Map<String, Object> dataMap) {
        LOGGER.errorContext(context, message, e, cloneMapData(dataMap));
    }

    /**
     * The Companies House logging implementation modifies the data map content which means that
     * if the same data map is used for subsequent calls any new message that might be passed in
     * is not displayed in certain log format outputs. Creating a clone of the data map gets around
     * this issue.
     *
     * @param dataMap The map data to log
     * @return A cloned copy of the map data
     */
    private Map<String, Object> cloneMapData(Map<String, Object> dataMap) {
        Map<String, Object> clonedMapData = new HashMap<>();
        clonedMapData.putAll(dataMap);

        return clonedMapData;
    }
}
