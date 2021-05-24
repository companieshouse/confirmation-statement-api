package uk.gov.companieshouse.confirmationstatementapi.contoller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.ConfirmationStatementApiApplication;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.model.response.CompanyValidationResponse;
import uk.gov.companieshouse.confirmationstatementapi.service.CompanyProfileService;
import uk.gov.companieshouse.confirmationstatementapi.service.EligibilityService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@RestController
public class EligibilityController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfirmationStatementApiApplication.APP_NAME);

    @Autowired
    private CompanyProfileService companyProfileService;

    @Autowired
    private EligibilityService eligibilityService;

    @GetMapping("/confirmation-statement/company/{company-number}/eligibility")
    public ResponseEntity<CompanyValidationResponse> getEligibility(@PathVariable("company-number") String companyNumber){
        LOGGER.debug("Start Handling request  GET '/' for eligibility");
        try {
            CompanyProfileApi companyProfile =
                    companyProfileService.getCompanyProfile(companyNumber);
           CompanyValidationResponse companyValidationResponse =
                    eligibilityService.checkCompanyEligibility(companyProfile);

            if(EligibilityStatusCode.COMPANY_VALID_FOR_SERVICE
                    == companyValidationResponse.getEligibilityStatusCode()) {
                return ResponseEntity.ok().body(companyValidationResponse);
            } else {
                return ResponseEntity.badRequest().body(companyValidationResponse);
            }
        } catch (ServiceException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
