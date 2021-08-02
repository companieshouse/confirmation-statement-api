package uk.gov.companieshouse.confirmationstatementapi.model.dao.statementofcapital;

import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.SectionDataDao;

public class StatementOfCapitalDataDao extends SectionDataDao {

    @Field("statement_of_capital")
    private StatementOfCapitalDao statementOfCapital;

    public StatementOfCapitalDao getStatementOfCapital() {
        return statementOfCapital;
    }

    public void setStatementOfCapital(StatementOfCapitalDao statementOfCapital) {
        this.statementOfCapital = statementOfCapital;
    }
}
