package uk.gov.companieshouse.confirmationstatementapi.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.model.json.shareholder.ShareholderJson;
import uk.gov.companieshouse.confirmationstatementapi.service.ShareholderService;

import java.util.List;

@RestController
public class ShareholderController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShareholderController.class);

    private ShareholderService shareholderService;

    @Autowired
    public ShareholderController(ShareholderService shareholderService) {
        this.shareholderService = shareholderService;
    }

    @GetMapping("/confirmation-statement/company/{company-number}/shareholders")
    public ResponseEntity<List<ShareholderJson>> getShareholders(@PathVariable("company-number") String companyNumber) {
        try {
            LOGGER.info("Calling service to retrieve shareholders data");
            var shareholders = shareholderService.getShareholders(companyNumber);
            return ResponseEntity.status(HttpStatus.OK).body(shareholders);
        } catch (ServiceException e) {
            LOGGER.error("Error retrieving shareholders data ", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
