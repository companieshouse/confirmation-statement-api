package uk.gov.companieshouse.confirmationstatementapi.eligibility;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;
import java.util.function.Supplier;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;

public abstract class CompanyProfileApplicableEligibilityRule implements EligibilityRule<CompanyProfileApi> {

    private final Set<String> baselineCompanyTypes;
    private final Set<String> targetCompanyTypes;
    private final LocalDate activationDate;
    private final Supplier<LocalDate> localDateNow;

    protected CompanyProfileApplicableEligibilityRule(Set<String> baselineCompanyTypes, Set<String> targetCompanyTypes,
                                                   LocalDate activationDate, Supplier<LocalDate> localDateNow) {
        this.activationDate = activationDate;
        this.baselineCompanyTypes = baselineCompanyTypes == null ? Collections.emptySet() : baselineCompanyTypes ;
        this.targetCompanyTypes = targetCompanyTypes == null ? Collections.emptySet() : targetCompanyTypes;
        this.localDateNow = localDateNow;
    }

    protected boolean companyApplicableForRule(CompanyProfileApi companyProfile, LocalDate madeUpDate) {
        if (companyProfile == null) {
            return false;
        }

        LocalDate dateToCheck = madeUpDate == null ? localDateNow.get() : madeUpDate;

        Set<String> applicableCompanyTypes = dateToCheck.isBefore(activationDate) ? baselineCompanyTypes : targetCompanyTypes;

        return applicableCompanyTypes.contains(companyProfile.getType());
    }

    public abstract void validateAgainstMadeUpDate(CompanyProfileApi companyProfileApi, LocalDate madeUpDate) throws EligibilityException, ServiceException;

    @Override
    public void validate(CompanyProfileApi input) throws EligibilityException, ServiceException {
        validateAgainstMadeUpDate(input, null);
    }

}