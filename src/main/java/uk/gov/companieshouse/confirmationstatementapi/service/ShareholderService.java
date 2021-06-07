package uk.gov.companieshouse.confirmationstatementapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.confirmationstatementapi.client.OracleQueryApiClientService;

@Service
public class ShareholderService {

    private OracleQueryApiClientService oracleQueryService;

    @Autowired
    public ShareholderService(OracleQueryApiClientService oracleQueryService) {
        this.oracleQueryService = oracleQueryService;
    }

    public int getShareholderCount(String companyNumber) {
        return oracleQueryService.getShareholderCount(companyNumber);
    }

}
