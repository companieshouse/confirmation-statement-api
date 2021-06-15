package uk.gov.companieshouse.confirmationstatementapi.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class CompanyTradedStatusTypeTest {

    @Test
    void findNotAdmittedToTrading() {
        var result = CompanyTradedStatusType.findByCompanyTradedStatusTypeId(0L);

        assertEquals(CompanyTradedStatusType.NOT_ADMITTED_TO_TRADING, result);
    }

    @Test
    void findAdmittedToTradingAndDtr5NotApplied() {
        var result = CompanyTradedStatusType.findByCompanyTradedStatusTypeId(1L);

        assertEquals(CompanyTradedStatusType.ADMITTED_TO_TRADING_AND_DTR5_NOT_APPLIED, result);
    }

    @Test
    void findAdmittedToTradingAndDtr5Applied() {
        var result = CompanyTradedStatusType.findByCompanyTradedStatusTypeId(3L);

        assertEquals(CompanyTradedStatusType.ADMITTED_TO_TRADING_AND_DTR5_APPLIED, result);
    }
}
