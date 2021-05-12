package uk.gov.companieshouse.confirmationstatementapi.client;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

@Component
public class ApiClientService {

    private final ApiClient apiKeyAuthenticatedClient;

    public ApiClientService() {
        apiKeyAuthenticatedClient = ApiSdkManager.getSDK();
    }

    public ApiClient getApiKeyAuthenticatedClient() {
        return apiKeyAuthenticatedClient;
    }
}
