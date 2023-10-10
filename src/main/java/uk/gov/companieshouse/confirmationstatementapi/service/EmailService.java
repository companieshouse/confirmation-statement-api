package uk.gov.companieshouse.confirmationstatementapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.model.company.RegisteredEmailAddressJson;
import uk.gov.companieshouse.confirmationstatementapi.client.OracleQueryClient;
import uk.gov.companieshouse.confirmationstatementapi.exception.RegisteredEmailNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;

@Service
public class EmailService {

    private final OracleQueryClient oracleQueryClient;

    @Autowired
    public EmailService(OracleQueryClient oracleQueryClient) {
        this.oracleQueryClient = oracleQueryClient;
    }

    public RegisteredEmailAddressJson getRegisteredEmailAddress(String companyNumber) throws ServiceException, RegisteredEmailNotFoundException {
        return oracleQueryClient.getRegisteredEmailAddress(companyNumber);
    }

}
