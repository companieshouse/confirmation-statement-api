package uk.gov.companieshouse.confirmationstatementapi.exception;

import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityFailureReason;

public class EligibilityException extends Exception {
    private final EligibilityFailureReason eligibilityFailureReason;

    public EligibilityException(EligibilityFailureReason eligibilityFailureReason) {
        this.eligibilityFailureReason = eligibilityFailureReason;
    }

    public EligibilityFailureReason getEligibilityFailureReason() {
        return eligibilityFailureReason;
    }
}
