package uk.gov.companieshouse.confirmationstatementapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.confirmationstatementapi.client.OracleQueryClient;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.model.StatementOfCapital;

@Service
public class StatementOfCapitalService {

    @Autowired
    private OracleQueryClient oracleQueryClient;

    public StatementOfCapital getStatementOfCapital(String companyNumber) throws ServiceException {
        return oracleQueryClient.getStatmentOfCapitalData(companyNumber);
    }
}
