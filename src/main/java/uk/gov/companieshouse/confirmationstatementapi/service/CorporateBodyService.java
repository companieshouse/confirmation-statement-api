package uk.gov.companieshouse.confirmationstatementapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.confirmationstatementapi.client.OracleQueryClient;

@Service
public class CorporateBodyService {

    private final OracleQueryClient oracleQueryClient;

    @Autowired
    public CorporateBodyService(OracleQueryClient oracleQueryClient) {
        this.oracleQueryClient = oracleQueryClient;
    }

    //TODO Make enum for company traded status
    public Long getCompanyTradedStatus(String companyNumber) {
        return oracleQueryClient.getCompanyTradedStatus(companyNumber);
    }
}
