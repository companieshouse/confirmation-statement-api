package uk.gov.companieshouse.confirmationstatementapi.eligibility;

import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;

public interface EligibilityRule<T> {
    void validate(T input) throws EligibilityException, ServiceException;
}
