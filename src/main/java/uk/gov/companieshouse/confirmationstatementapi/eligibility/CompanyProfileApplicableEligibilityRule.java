package uk.gov.companieshouse.confirmationstatementapi.eligibility;

import uk.gov.companieshouse.api.model.company.CompanyProfileApi;

import java.time.LocalDate;
import java.util.Set;
import java.util.function.Supplier;

public abstract class CompanyProfileApplicableEligibilityRule implements EligibilityRule<CompanyProfileApi> {

    private Set<String> applicableCompanyTypes;

    public CompanyProfileApplicableEligibilityRule(Set<String> baselineCompanyTypes, Set<String> targetCompanyTypes,
                                                   LocalDate activationDate, Supplier<LocalDate> localDateNow) {


        LocalDate now = localDateNow.get();

        applicableCompanyTypes = now.isBefore(activationDate) ? baselineCompanyTypes : targetCompanyTypes;
    }

    protected boolean companyApplicableForRule(CompanyProfileApi companyProfile) {
        if (companyProfile == null || applicableCompanyTypes == null || applicableCompanyTypes.isEmpty()) return false;

        return applicableCompanyTypes.contains(companyProfile.getType());
    }

}
