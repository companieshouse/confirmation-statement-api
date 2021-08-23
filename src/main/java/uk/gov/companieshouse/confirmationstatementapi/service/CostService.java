package uk.gov.companieshouse.confirmationstatementapi.service;

import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.model.payment.Cost;

import java.util.Collections;

@Service
public class CostService {

    private static final String COST_DESC = "The amount to pay for filing a CS01";
    private static final String PAYMENT_ACCOUNT = "data-maintenance";

    public Cost getCosts() {
        var cost = new Cost();
        cost.setAmount("13.00");
        cost.setAvailablePaymentMethods(Collections.singletonList("credit-card"));
        cost.setClassOfPayment(Collections.singletonList(PAYMENT_ACCOUNT));
        cost.setDescription(COST_DESC);
        cost.setDescriptionIdentifier("description-identifier");
        cost.setDescriptionValues(Collections.singletonMap("Key", "Value"));
        cost.setKind("payment-session#payment-session");
        cost.setResourceKind("resource-kind");

        return cost;
    }
}
