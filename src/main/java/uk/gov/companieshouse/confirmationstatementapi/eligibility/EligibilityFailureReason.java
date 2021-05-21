package uk.gov.companieshouse.confirmationstatementapi.eligibility;

public enum EligibilityFailureReason {
    COMPANY_HAS_MULTIPLE_PSCS,
    INVALID_COMPANY_STATUS,
    INVALID_COMPANY_TYPE_CS01_FILING_NOT_REQUIRED,
    INVALID_COMPANY_TYPE_PAPER_FILING_ONLY,
    INVALID_COMPANY_TYPE_USE_WEB_FILING
}
