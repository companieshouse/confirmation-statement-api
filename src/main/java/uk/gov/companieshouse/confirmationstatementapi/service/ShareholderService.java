package uk.gov.companieshouse.confirmationstatementapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.confirmationstatementapi.client.OracleQueryClient;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.model.json.shareholder.ShareholderJson;

import java.util.List;

@Service
public class ShareholderService {

    private final OracleQueryClient oracleQueryClient;

    @Autowired
    public ShareholderService(OracleQueryClient oracleQueryClient) {
        this.oracleQueryClient = oracleQueryClient;
    }

    public List<ShareholderJson> getShareholders(String companyNumber) throws ServiceException {
        return oracleQueryClient.getShareholders(companyNumber);
    }

    public int getShareholderCount(String companyNumber) throws ServiceException {
        return oracleQueryClient.getShareholderCount(companyNumber);
    }

}
