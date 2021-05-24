package uk.gov.companieshouse.confirmationstatementapi.exception;

import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;

public class EligibilityException extends Exception {
    private final EligibilityStatusCode eligibilityStatusCode;

    public EligibilityException(EligibilityStatusCode eligibilityStatusCode) {
        this.eligibilityStatusCode = eligibilityStatusCode;
    }

    public EligibilityStatusCode getEligibilityStatusCode() {
        return eligibilityStatusCode;
    }
}
