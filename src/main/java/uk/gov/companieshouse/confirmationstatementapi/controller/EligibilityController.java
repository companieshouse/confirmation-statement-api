package uk.gov.companieshouse.confirmationstatementapi.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.exception.CompanyNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.model.response.CompanyValidationResponse;
import uk.gov.companieshouse.confirmationstatementapi.service.CompanyProfileService;
import uk.gov.companieshouse.confirmationstatementapi.service.EligibilityService;

@RestController
public class EligibilityController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EligibilityController.class);

    @Autowired
    private CompanyProfileService companyProfileService;

    @Autowired
    private EligibilityService eligibilityService;

    @GetMapping("/confirmation-statement/company/{company-number}/eligibility")
    public ResponseEntity<CompanyValidationResponse> getEligibility(@PathVariable("company-number") String companyNumber){
        try {
            var companyProfile =
                    companyProfileService.getCompanyProfile(companyNumber);
            var companyValidationResponse =
                    eligibilityService.checkCompanyEligibility(companyProfile);

            if (EligibilityStatusCode.COMPANY_VALID_FOR_SERVICE
                    == companyValidationResponse.getEligibilityStatusCode()) {
                return ResponseEntity.ok().body(companyValidationResponse);
            } else {
                return ResponseEntity.badRequest().body(companyValidationResponse);
            }
        } catch (CompanyNotFoundException e) {
            var companyNotFoundResponse = new CompanyValidationResponse();
            companyNotFoundResponse.setEligibilityStatusCode(EligibilityStatusCode.COMPANY_NOT_FOUND);
            return ResponseEntity.badRequest().body(companyNotFoundResponse);
        } catch (Exception e) {
            LOGGER.error(String.format("Error checking eligibility of company number %s", companyNumber), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
