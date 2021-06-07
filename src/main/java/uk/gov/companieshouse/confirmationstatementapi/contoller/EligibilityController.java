package uk.gov.companieshouse.confirmationstatementapi.contoller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.exception.CompanyNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.model.response.CompanyValidationResponse;
import uk.gov.companieshouse.confirmationstatementapi.service.CompanyProfileService;
import uk.gov.companieshouse.confirmationstatementapi.service.EligibilityService;
import uk.gov.companieshouse.confirmationstatementapi.service.ShareholderService;

@RestController
public class EligibilityController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EligibilityController.class);

    @Autowired
    private CompanyProfileService companyProfileService;

    @Autowired
    private ShareholderService shareholderService;

    @Autowired
    private EligibilityService eligibilityService;

    @GetMapping("/confirmation-statement/company/{company-number}/eligibility")
    public ResponseEntity<CompanyValidationResponse> getEligibility(
            @PathVariable("company-number") String companyNumber) {
        try {
            CompanyProfileApi companyProfile = companyProfileService.getCompanyProfile(companyNumber);
            CompanyValidationResponse companyValidationResponse = eligibilityService
                    .checkCompanyEligibility(companyProfile);

            if (EligibilityStatusCode.COMPANY_VALID_FOR_SERVICE == companyValidationResponse
                    .getEligibilityStatusCode()) {
                return ResponseEntity.ok().body(companyValidationResponse);
            } else {
                return ResponseEntity.badRequest().body(companyValidationResponse);
            }
        } catch (CompanyNotFoundException e) {
            var companyNotFoundResponse = new CompanyValidationResponse();
            companyNotFoundResponse.setEligibilityStatusCode(EligibilityStatusCode.COMPANY_NOT_FOUND);
            return ResponseEntity.badRequest().body(companyNotFoundResponse);
        } catch (ServiceException e) {
            LOGGER.error("Error checking eligibility of company", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/confirmation-statement/company/{company-number}/shareholder/count")
    public ResponseEntity<Object> getShareholderCount(@PathVariable("company-number") String companyNumber) {
        var response = shareholderService.getShareholderCount(companyNumber);
        return ResponseEntity.ok().body(response);
    }

}
