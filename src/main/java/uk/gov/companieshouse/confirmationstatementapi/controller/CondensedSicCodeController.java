package uk.gov.companieshouse.confirmationstatementapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.confirmationstatementapi.service.CondensedSicCodeService;
import uk.gov.companieshouse.confirmationstatementapi.utils.ApiLogger;

@RestController
@RequestMapping("/confirmation-statement/condensed-sic-codes")
public class CondensedSicCodeController {

    private final CondensedSicCodeService condensedSicCodeService;

    @Autowired
    public CondensedSicCodeController(CondensedSicCodeService condensedSicCodeService) {
        this.condensedSicCodeService = condensedSicCodeService;
    }

    @GetMapping
    public ResponseEntity<Object> getCondensedSicCodeList() {
        ApiLogger.info("Calling service to retrieve full condensed sic code list");

        return ResponseEntity.ok(condensedSicCodeService.getCondensedSicCodeList());
    }
}
