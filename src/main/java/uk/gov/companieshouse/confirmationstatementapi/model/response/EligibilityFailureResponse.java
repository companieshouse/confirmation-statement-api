package uk.gov.companieshouse.confirmationstatementapi.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityFailureReason;

public class EligibilityFailureResponse {

    @JsonProperty("validation_error")
    private EligibilityFailureReason validationError;

    public EligibilityFailureResponse() {
    }

    public EligibilityFailureResponse(EligibilityFailureReason validationError) {
        this.validationError = validationError;
    }

    public EligibilityFailureReason getValidationError() {
        return validationError;
    }

    public void setValidationError(EligibilityFailureReason validationError) {
        this.validationError = validationError;
    }
}
