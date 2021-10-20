package uk.gov.companieshouse.confirmationstatementapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.confirmationstatementapi.client.OracleQueryClient;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.exception.StatementOfCapitalNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.model.json.statementofcapital.StatementOfCapitalJson;

@Service
public class StatementOfCapitalService {

    @Autowired
    private OracleQueryClient oracleQueryClient;

    public StatementOfCapitalJson getStatementOfCapital(String companyNumber) throws ServiceException, StatementOfCapitalNotFoundException {
        return oracleQueryClient.getStatementOfCapitalData(companyNumber);
    }
}
