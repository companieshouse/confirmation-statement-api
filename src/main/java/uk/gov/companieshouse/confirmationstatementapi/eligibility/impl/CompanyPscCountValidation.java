package uk.gov.companieshouse.confirmationstatementapi.eligibility.impl;

import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityRule;
import uk.gov.companieshouse.confirmationstatementapi.exception.EligibilityException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.service.PscService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompanyPscCountValidation implements EligibilityRule<CompanyProfileApi> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyPscCountValidation.class);

    private final PscService pscService;

    public CompanyPscCountValidation(PscService pscService) {
        this.pscService = pscService;
    }

    @Override
    public void validate(CompanyProfileApi profileToValidate) throws EligibilityException, ServiceException {
        LOGGER.info("Validating Company PSCs for: {}", profileToValidate.getCompanyNumber());
        var count = pscService.getPscs(profileToValidate.getCompanyNumber()).getActiveCount();
        if (count != null && count > 1) {
            LOGGER.info("Company PSCs validation failed for: {}", profileToValidate.getCompanyNumber());
            throw new EligibilityException(EligibilityStatusCode.INVALID_COMPANY_APPOINTMENTS_MORE_THAN_ONE_PSC);
        }
        LOGGER.info("Company PSCs validation passed for: {}", profileToValidate.getCompanyNumber());
    }

}
