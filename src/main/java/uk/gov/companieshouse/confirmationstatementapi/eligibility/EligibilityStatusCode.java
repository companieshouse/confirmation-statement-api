package uk.gov.companieshouse.confirmationstatementapi.eligibility;

public enum EligibilityStatusCode {
    INVALID_COMPANY_APPOINTMENTS_INVALID_NUMBER_OF_OFFICERS,
    INVALID_COMPANY_APPOINTMENTS_MORE_THAN_ONE_PSC,
    INVALID_COMPANY_APPOINTMENTS_MORE_THAN_FIVE_PSC,
    INVALID_COMPANY_APPOINTMENTS_MORE_THAN_ONE_SHAREHOLDER,
    INVALID_COMPANY_STATUS,
    INVALID_COMPANY_TRADED_STATUS_USE_WEBFILING,
    INVALID_COMPANY_TYPE_CS01_FILING_NOT_REQUIRED,
    INVALID_COMPANY_TYPE_PAPER_FILING_ONLY,
    INVALID_COMPANY_TYPE_USE_WEB_FILING,
    COMPANY_NOT_FOUND,
    COMPANY_VALID_FOR_SERVICE
}
