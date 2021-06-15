package uk.gov.companieshouse.confirmationstatementapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.confirmationstatementapi.client.OracleQueryClient;

@Service
public class ShareholderService {

    @Autowired
    private OracleQueryClient oracleQueryClient;

    public int getShareholderCount(String companyNumber) {
        return oracleQueryClient.getShareholderCount(companyNumber);
    }

}
