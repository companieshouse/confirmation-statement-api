package uk.gov.companieshouse.confirmationstatementapi.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;

public class CompanyValidationResponse {

    @JsonProperty("eligibility_status_code")
    private EligibilityStatusCode eligibilityStatusCode;

    public CompanyValidationResponse() {
    }

    public CompanyValidationResponse(EligibilityStatusCode eligibilityStatusCode) {
        this.eligibilityStatusCode = eligibilityStatusCode;
    }

    public EligibilityStatusCode getEligibilityStatusCode() {
        return eligibilityStatusCode;
    }

    public void setEligibilityStatusCode(EligibilityStatusCode eligibilityStatusCode) {
        this.eligibilityStatusCode = eligibilityStatusCode;
    }
}
