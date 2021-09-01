package uk.gov.companieshouse.confirmationstatementapi.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.model.payment.Cost;
import uk.gov.companieshouse.confirmationstatementapi.service.CostService;

@RestController
@RequestMapping("/transactions/{transaction_id}/confirmation-statement/{confirmation-statement-id}/costs")
public class CostsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CostsController.class);

    private final CostService costService;

    @Autowired
    public CostsController(CostService costService) {
        this.costService = costService;
    }

    @GetMapping
    public ResponseEntity<Cost> getCosts() {

        LOGGER.info("HERE");
        var cost = costService.getCosts();

        return ResponseEntity.ok(cost);
    }
}
