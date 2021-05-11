package uk.gov.companieshouse.confirmationstatementapi.eligibility;

import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;

public interface EligibilityRule<T> {
    void validate(T input) throws EligibilityException;
}
