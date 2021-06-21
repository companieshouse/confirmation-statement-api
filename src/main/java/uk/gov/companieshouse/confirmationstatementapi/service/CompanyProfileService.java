package uk.gov.companieshouse.confirmationstatementapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.client.ApiClientService;
import uk.gov.companieshouse.confirmationstatementapi.exception.CompanyNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;

@Service
public class CompanyProfileService {

    private static final String EXCEPTION_MESSAGE = "Error Retrieving Company Profile for company number %s";
    private static final String EXCEPTION_MESSAGE_WITH_HTTP_CODE = EXCEPTION_MESSAGE + ", http status code %s";

    private final ApiClientService apiClientService;

    @Autowired
    public CompanyProfileService(ApiClientService apiClientService) {
        this.apiClientService = apiClientService;
    }

    public CompanyProfileApi getCompanyProfile(String companyNumber) throws ServiceException, CompanyNotFoundException {
        try {
            var uri = "/company/" + companyNumber;
            return apiClientService.getApiKeyAuthenticatedClient().company().get(uri).execute().getData();
        } catch (URIValidationException e) {
            throw new ServiceException(String.format(EXCEPTION_MESSAGE, companyNumber), e);
        } catch (ApiErrorResponseException e) {
            if (HttpStatus.NOT_FOUND.value() == e.getStatusCode()) {
                throw new CompanyNotFoundException();
            }
            String message = String.format(
                    EXCEPTION_MESSAGE_WITH_HTTP_CODE,
                    companyNumber,
                    e.getStatusCode());
            throw new ServiceException(message, e);
        }
    }
}
