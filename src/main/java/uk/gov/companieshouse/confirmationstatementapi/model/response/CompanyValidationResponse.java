package uk.gov.companieshouse.confirmationstatementapi.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;

public class CompanyValidationResponse {

    @JsonProperty("validation_error")
    private EligibilityStatusCode validationError;

    public CompanyValidationResponse() {
    }

    public CompanyValidationResponse(EligibilityStatusCode validationError) {
        this.validationError = validationError;
    }

    public EligibilityStatusCode getValidationError() {
        return validationError;
    }

    public void setValidationError(EligibilityStatusCode validationError) {
        this.validationError = validationError;
    }
}
