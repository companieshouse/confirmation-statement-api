package uk.gov.companieshouse.confirmationstatementapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.confirmationstatementapi.client.OracleQueryClient;
import uk.gov.companieshouse.confirmationstatementapi.model.CompanyTradedStatusType;

@Service
public class CorporateBodyService {

    private final OracleQueryClient oracleQueryClient;

    @Autowired
    public CorporateBodyService(OracleQueryClient oracleQueryClient) {
        this.oracleQueryClient = oracleQueryClient;
    }

    public CompanyTradedStatusType getCompanyTradedStatus(String companyNumber) {
        var companyTradedStatus = oracleQueryClient.getCompanyTradedStatus(companyNumber);
        return CompanyTradedStatusType.findByCompanyTradedStatusTypeId(companyTradedStatus);
    }
}
