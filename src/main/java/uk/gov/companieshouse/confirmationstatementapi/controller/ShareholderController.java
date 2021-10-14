package uk.gov.companieshouse.confirmationstatementapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.model.json.shareholder.ShareholderJson;
import uk.gov.companieshouse.confirmationstatementapi.service.ShareholderService;

import java.util.HashMap;
import java.util.List;

import static uk.gov.companieshouse.confirmationstatementapi.ConfirmationStatementApiApplication.LOGGER;
import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.ERIC_REQUEST_ID_KEY;

@RestController
public class ShareholderController {

    private ShareholderService shareholderService;

    @Autowired
    public ShareholderController(ShareholderService shareholderService) {
        this.shareholderService = shareholderService;
    }

    @GetMapping("/confirmation-statement/company/{companyNumber}/shareholders")
    public ResponseEntity<List<ShareholderJson>> getShareholders(
            @PathVariable String companyNumber,
            @RequestHeader(value = ERIC_REQUEST_ID_KEY) String requestId) {

        var map = new HashMap<String, Object>();
        map.put("company_number", companyNumber);

        try {
            LOGGER.infoContext(requestId, "Calling service to retrieve shareholders data", map);
            var shareholders = shareholderService.getShareholders(companyNumber);
            return ResponseEntity.status(HttpStatus.OK).body(shareholders);
        } catch (ServiceException e) {
            LOGGER.errorContext(requestId,"Error retrieving shareholders data", e, map);
            return ResponseEntity.internalServerError().build();
        }
    }
}
