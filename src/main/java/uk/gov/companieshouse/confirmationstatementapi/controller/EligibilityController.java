package uk.gov.companieshouse.confirmationstatementapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.exception.CompanyNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.model.response.CompanyValidationResponse;
import uk.gov.companieshouse.confirmationstatementapi.service.CompanyProfileService;
import uk.gov.companieshouse.confirmationstatementapi.service.EligibilityService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.util.HashMap;

import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.ERIC_REQUEST_ID;

@RestController
public class EligibilityController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EligibilityController.class.getName());

    @Autowired
    private CompanyProfileService companyProfileService;

    @Autowired
    private EligibilityService eligibilityService;

    @GetMapping("/confirmation-statement/company/{company-number}/eligibility")
    public ResponseEntity<CompanyValidationResponse> getEligibility(@PathVariable("company-number") String companyNumber,
            @RequestHeader(value = ERIC_REQUEST_ID) String requestId) {

        var logMap = new HashMap<String, Object>();
        logMap.put("requestId", requestId);
        logMap.put("companyNumber", companyNumber);
        LOGGER.infoContext(requestId, "Calling service to retrieve company eligibility", logMap);

        try {
            var companyProfile = companyProfileService.getCompanyProfile(companyNumber);
            var companyValidationResponse = eligibilityService.checkCompanyEligibility(companyProfile);

            if (EligibilityStatusCode.COMPANY_VALID_FOR_SERVICE == companyValidationResponse.getEligibilityStatusCode()) {
                return ResponseEntity.ok().body(companyValidationResponse);
            } else {
                return ResponseEntity.badRequest().body(companyValidationResponse);
            }
        } catch (CompanyNotFoundException e) {
            var companyNotFoundResponse = new CompanyValidationResponse();
            companyNotFoundResponse.setEligibilityStatusCode(EligibilityStatusCode.COMPANY_NOT_FOUND);
            return ResponseEntity.badRequest().body(companyNotFoundResponse);
        } catch (Exception e) {
            LOGGER.errorContext(requestId, "Error checking eligibility of company number.", e, logMap);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
