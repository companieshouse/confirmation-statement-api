package uk.gov.companieshouse.confirmationstatementapi.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.http.ApiKeyHttpClient;

@Component
public class ApiKeyClient {

    @Value("${CHS_API_KEY}")
    private String apiKey;

    private final ApiClient apiKeyAuthenticatedClient;

    public ApiKeyClient() {
        ApiKeyHttpClient httpClient = new ApiKeyHttpClient(apiKey);
        apiKeyAuthenticatedClient = new ApiClient(httpClient);
    }

    public ApiClient getApiKeyAuthenticatedClient() {
        return apiKeyAuthenticatedClient;
    }
}
