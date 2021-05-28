package uk.gov.companieshouse.confirmationstatementapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.confirmationstatementapi.ConfirmationStatementApiApplication;
import uk.gov.companieshouse.confirmationstatementapi.exception.CompanyNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.model.response.CompanyValidationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

@Service
public class ConfirmationStatementService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfirmationStatementService.class);

    private final CompanyProfileService companyProfileService;
    private final EligibilityService eligibilityService;

    @Autowired
    public ConfirmationStatementService(CompanyProfileService companyProfileService,
                                        EligibilityService eligibilityService) {
        this.companyProfileService = companyProfileService;
        this.eligibilityService = eligibilityService;
    }

    public ResponseEntity<Object> createConfirmationStatement(Transaction transaction) throws ServiceException {
        CompanyProfileApi companyProfile;
        try {
            companyProfile = companyProfileService.getCompanyProfile(transaction.getCompanyNumber());
        } catch (CompanyNotFoundException e) {
            throw new ServiceException("Error retrieving company profile", e);
        }
        CompanyValidationResponse companyValidationResponse = eligibilityService.checkCompanyEligibility(companyProfile) ;
        if(companyValidationResponse.getEligibilityStatusCode() != null) {
            return ResponseEntity.badRequest().body(companyValidationResponse);
        }

        String createdUri = "/transactions/" + transaction.getId() + "/confirmation-statement/";

        LOGGER.info("Confirmation Statement created for transaction id: {}", transaction.getId());
        return ResponseEntity.created(URI.create(createdUri)).body("Created");
    }
}
