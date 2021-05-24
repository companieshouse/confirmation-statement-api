package uk.gov.companieshouse.confirmationstatementapi.contoller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.ConfirmationStatementApiApplication;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.model.response.EligibilityFailureResponse;
import uk.gov.companieshouse.confirmationstatementapi.service.CompanyProfileService;
import uk.gov.companieshouse.confirmationstatementapi.service.EligibilityService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.util.Optional;

@RestController
public class EligibilityController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfirmationStatementApiApplication.APP_NAME);

    @Autowired
    private CompanyProfileService companyProfileService;

    @Autowired
    private EligibilityService eligibilityService;

    @GetMapping("/confirmation-statement/company/{company-number}/eligibility")
    public ResponseEntity<Object> getEligibility(@PathVariable("company-number") String companyNumber){
        LOGGER.debug("Start Handling request  GET '/' for eligibility");
        try {
            CompanyProfileApi companyProfile =
                    companyProfileService.getCompanyProfile(companyNumber);
            Optional<EligibilityFailureResponse> validationErrorResponseBody =
                    eligibilityService.checkCompanyEligibility(companyProfile);

            if(validationErrorResponseBody.isPresent()) {
                return ResponseEntity.badRequest().body(validationErrorResponseBody.get());
            }
            return ResponseEntity.ok("ok");
        } catch (ServiceException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
