package uk.gov.companieshouse.confirmationstatementapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.confirmationstatementapi.client.OracleQueryClient;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.model.UsualResidentialAddress;

@Service
public class CorporateBodyAppointmentService {

    @Autowired
    private OracleQueryClient oracleQueryClient;

    public UsualResidentialAddress getUsualResidentialAddress(String corpBodyAppointmentId) throws ServiceException {
        return oracleQueryClient.getUsualResidentialAddress(corpBodyAppointmentId);
    }
}
