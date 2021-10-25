package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.service.PscService;

import static uk.gov.companieshouse.confirmationstatementapi.ConfirmationStatementApiApplication.LOGGER;

public class CompanyPscCountValidation implements EligibilityRule<CompanyProfileApi> {

    private final PscService pscService;

    private final boolean companyPscCountValidationFeatureFlag;

    private final boolean multiplePscJourneyFlag;

    public CompanyPscCountValidation(PscService pscService, boolean companyPscCountValidationFeatureFlag, boolean multiplePscJourneyFlag) {
        this.pscService = pscService;
        this.companyPscCountValidationFeatureFlag = companyPscCountValidationFeatureFlag;
        this.multiplePscJourneyFlag = multiplePscJourneyFlag;
        LOGGER.debug(String.format("MULTIPLE PSC JOURNEY FEATURE FLAG: %s", multiplePscJourneyFlag));
    }

    @Override
    public void validate(CompanyProfileApi profileToValidate) throws EligibilityException, ServiceException {
        LOGGER.info(String.format("Validating Company PSCs for: %s", profileToValidate.getCompanyNumber()));
        if (!companyPscCountValidationFeatureFlag) {
            LOGGER.debug("Company PSC Count FEATURE FLAG off skipping validation");
            return;
        }
        var count = pscService.getPSCsFromCHS(profileToValidate.getCompanyNumber()).getActiveCount();
        if (!multiplePscJourneyFlag) {
            if (count != null && count > 1) {
                LOGGER.info(String.format("Company PSCs validation failed for: %s", profileToValidate.getCompanyNumber()));
                throw new EligibilityException(EligibilityStatusCode.INVALID_COMPANY_APPOINTMENTS_MORE_THAN_ONE_PSC);
            }
        } else {
            if (count != null && count > 5) {
                LOGGER.info(String.format("Company PSCs validation failed for: %s", profileToValidate.getCompanyNumber()));
                throw new EligibilityException(EligibilityStatusCode.INVALID_COMPANY_APPOINTMENTS_MORE_THAN_FIVE_PSCS);
            }
        }
        LOGGER.info(String.format("Company PSCs validation passed for: %s", profileToValidate.getCompanyNumber()));
    }

}
