package uk.gov.companieshouse.confirmationstatementapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.api.model.company.StatementOfCapitalJson;
import uk.gov.companieshouse.confirmationstatementapi.client.OracleQueryClient;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.exception.StatementOfCapitalNotFoundException;

@Service
public class StatementOfCapitalService {

    @Autowired
    private OracleQueryClient oracleQueryClient;

    public StatementOfCapitalJson getStatementOfCapital(String companyNumber) throws ServiceException, StatementOfCapitalNotFoundException {
        return oracleQueryClient.getStatementOfCapitalData(companyNumber);
    }
}
