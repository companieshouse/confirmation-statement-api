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
import uk.gov.companieshouse.confirmationstatementapi.model.UsualResidentialAddress;
import uk.gov.companieshouse.confirmationstatementapi.service.CorporateBodyAppointmentService;

@RestController
class UsualResidentialAddressController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UsualResidentialAddressController.class);

    @Autowired
    private CorporateBodyAppointmentService corporateBodyAppointmentService;

    @GetMapping("/confirmation-statement/corporate-body-appointment/{corpBodyAppointmentId}/usual-residential-address")
    ResponseEntity<UsualResidentialAddress> getUsualResidentialAddress(@PathVariable String corpBodyAppointmentId) {
        try {
            LOGGER.info("Calling CorporateBodyAppointment service to retrieve usual residential address data for corporateBodyAppointment id {}", corpBodyAppointmentId);
            var usualResidentialAddress = corporateBodyAppointmentService.getUsualResidentialAddress(corpBodyAppointmentId);
            return ResponseEntity.status(HttpStatus.OK).body(usualResidentialAddress);
        } catch (ServiceException e) {
            logErrorMessage(corpBodyAppointmentId, e);
            return ResponseEntity.internalServerError().build();
        } catch (Exception e) {
            logErrorMessage(corpBodyAppointmentId, e);
            throw e;
        }
    }

    private void logErrorMessage(String corpBodyAppointmentId, Exception e) {
        LOGGER.error("Error retrieving usual residential address for corporateBodyAppointmentId {}", corpBodyAppointmentId, e);
    }
}
