package uk.gov.companieshouse.confirmationstatementapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.confirmationstatementapi.client.OracleQueryClient;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.model.json.registerlocation.RegisterLocationJson;

import java.util.List;

@Service
public class RegisterLocationService {

    private final OracleQueryClient oracleQueryClient;

    @Autowired
    public RegisterLocationService(OracleQueryClient oracleQueryClient) {
        this.oracleQueryClient = oracleQueryClient;
    }

    public List<RegisterLocationJson> getRegisterLocations(String companyNumber) throws ServiceException {
        return oracleQueryClient.getRegisterLocations(companyNumber);
    }
}
