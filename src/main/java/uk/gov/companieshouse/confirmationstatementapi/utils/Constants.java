package uk.gov.companieshouse.confirmationstatementapi.utils;

public class Constants {

    private Constants() {}

    public static final int MAX_COMPANY_NUMBER_LENGTH = 8;
    public static final int MAX_TRANSACTION_ID_LENGTH = 50;
    public static final int MAX_SUBMISSION_ID_LENGTH = 50;

    public static final String ERIC_REQUEST_ID_KEY = "X-Request-Id";
    public static final String TRANSACTION_ID_KEY = "transaction_id";
    public static final String CONFIRMATION_STATEMENT_ID_KEY = "confirmation_statement_id";
    public static final String FILING_KIND_CS = "confirmation-statement";
    public static final String COMPANY_NUMBER = "company-number";
}
