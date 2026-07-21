package uk.gov.companieshouse.confirmationstatementapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.model.payment.Cost;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.confirmationstatementapi.exception.CompanyNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;

import java.util.Collections;

import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.LIMITED_PARTNERSHIP_LP_SUBTYPE;
import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.LIMITED_PARTNERSHIP_PFLP_SUBTYPE;
import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.LIMITED_PARTNERSHIP_SLP_SUBTYPE;
import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.LIMITED_PARTNERSHIP_SPFLP_SUBTYPE;
import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.LIMITED_PARTNERSHIP_TYPE;


@Service
public class CostService {

    private final CompanyProfileService companyProfileService;

    @Value("${CS01_COST}")
    private String costAmount;
    private static final String COST_DESC = "Annual confirmation statement fee";
    private static final String PAYMENT_ACCOUNT = "data-maintenance";

    @Autowired
    public CostService(CompanyProfileService companyProfileService) {
        this.companyProfileService = companyProfileService;
    }

    public Cost getCosts(Transaction transaction) throws ServiceException, CompanyNotFoundException {
        var cost = new Cost();
        cost.setAmount(costAmount);
        cost.setAvailablePaymentMethods(Collections.singletonList("credit-card"));
        cost.setClassOfPayment(Collections.singletonList(PAYMENT_ACCOUNT));
        cost.setDescription(COST_DESC);
        cost.setDescriptionIdentifier("description-identifier");
        cost.setDescriptionValues(Collections.singletonMap("Key", "Value"));
        cost.setKind("payment-session#payment-session");
        cost.setResourceKind("resource-kind");
        setCostProductType(cost, transaction);

        return cost;
    }

    private void setCostProductType(Cost cost, Transaction transaction) throws ServiceException, CompanyNotFoundException {
        //set the default product type
        cost.setProductType("confirmation-statement");

        String companyNumber = transaction.getCompanyNumber();
        var companyProfile = companyProfileService.getCompanyProfile(companyNumber);

        if (companyProfile != null
                && companyProfile.getType() != null
                && LIMITED_PARTNERSHIP_TYPE.equals(companyProfile.getType())
                && companyProfile.getSubtype() != null) {

            String companySubtype = companyProfile.getSubtype();
            if (LIMITED_PARTNERSHIP_LP_SUBTYPE.equals(companySubtype)
                    || LIMITED_PARTNERSHIP_PFLP_SUBTYPE.equals(companySubtype)) {
                cost.setProductType("lp-confirmation-statement");
            } else if (LIMITED_PARTNERSHIP_SLP_SUBTYPE.equals(companySubtype)
                    || LIMITED_PARTNERSHIP_SPFLP_SUBTYPE.equals(companySubtype)) {
                cost.setProductType("slp-confirmation-statement");
            }
        }
    }

}
