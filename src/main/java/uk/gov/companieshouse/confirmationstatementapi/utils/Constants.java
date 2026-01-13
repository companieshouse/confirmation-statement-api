package uk.gov.companieshouse.confirmationstatementapi.utils;

public class Constants {

    private Constants() {}

    public static final String ERIC_REQUEST_ID_KEY = "X-Request-Id";
    public static final String TRANSACTION_ID_KEY = "transaction_id";
    public static final String CONFIRMATION_STATEMENT_ID_KEY = "confirmation_statement_id";
    public static final String FILING_KIND_CS = "confirmation-statement";
    public static final String FILING_KIND_LPCS = "limited-partnership-confirmation-statement";
    public static final String FILING_KIND_SLPCS = "scottish-limited-partnership-confirmation-statement";
    public static final String COMPANY_NUMBER = "company-number";
    public static final String DATE_FORMAT_YYYYMD = "yyyy-M-d";
    public static final String LIMITED_PARTNERSHIP_TYPE = "limited-partnership";
    public static final String LIMITED_PARTNERSHIP_LP_TYPE = "limited-partnership";
    public static final String LIMITED_PARTNERSHIP_SLP_SUBTYPE = "scottish-limited-partnership";
    public static final String LIMITED_PARTNERSHIP_PFLP_SUBTYPE = "private-fund-limited-partnership";
    public static final String LIMITED_PARTNERSHIP_SPFLP_SUBTYPE = "scottish-private-fund-limited-partnership";

}
