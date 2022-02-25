package uk.gov.companieshouse.confirmationstatementapi.utils;

import java.util.regex.Pattern;

public class Constants {

    private Constants() {}

    public static final int MAX_COMPANY_NUMBER_LENGTH = 8;
    public static final int MAX_ID_LENGTH = 50;

    public static final Pattern COMPANY_NUMBER_PATTERN = Pattern.compile(
            "^([a-z]|[a-z][a-z])?\\d{6,8}$", Pattern.CASE_INSENSITIVE);

    public static final Pattern ID_PATTERN = Pattern.compile(
            "[^A-Za-z\\d -]");

    public static final String ERIC_REQUEST_ID_KEY = "X-Request-Id";
    public static final String TRANSACTION_ID_KEY = "transaction_id";
    public static final String CONFIRMATION_STATEMENT_ID_KEY = "confirmation_statement_id";
    public static final String FILING_KIND_CS = "confirmation-statement";
    public static final String COMPANY_NUMBER = "company-number";
}
