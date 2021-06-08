package uk.gov.companieshouse.confirmationstatementapi.model;

public enum CompanyTradedStatusType {

    NOT_ADMITTED_TO_TRADING(0L),
    ADMITTED_TO_TRADING_AND_DTR5_NOT_APPLIED(1L),
    ADMITTED_TO_TRADING_AND_DTR5_APPLIED(3L);

    private final Long companyTradedStatusTypeId;

    CompanyTradedStatusType(Long companyTradedStatusTypeId) {
        this.companyTradedStatusTypeId = companyTradedStatusTypeId;
    }

    public static CompanyTradedStatusType findByCompanyTradedStatusTypeId(Long companyTradedStatusTypeId) {
        for (CompanyTradedStatusType type: values()) {
            if(type.companyTradedStatusTypeId.equals(companyTradedStatusTypeId)) {
                return type;
            }
        }
        return null;
    }
}
