package uk.gov.companieshouse.confirmationstatementapi.checks;

import uk.gov.companieshouse.confirmationstatementapi.ConfirmationStatementApiApplication;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.service.PscService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

public class CompanyPscCheck {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfirmationStatementApiApplication.APP_NAME);
    private final PscService pscService;

    public CompanyPscCheck(PscService pscService) {
        this.pscService = pscService;
    }

    public boolean hasMultipleActivePscs(String companyNumber) throws ServiceException {
        try {
            var count = pscService.getPscs(companyNumber).getActiveCount();
            return count != null && count > 1;
        } catch (ServiceException e) {
            LOGGER.error(e);
            throw new ServiceException("Error Retrieving Persons of significant control", e);
        }
    }
    
}
