package uk.gov.companieshouse.confirmationstatementapi.model.dao;

import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.confirmationstatementapi.model.SectionStatus;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.statementofcapital.StatementOfCapitalDao;

public class StatementOfCapitalDataDao {
    @Field("section_status")
    private SectionStatus sectionStatus;

    @Field("statement_of_capital")
    private StatementOfCapitalDao statementOfCapital;

    public SectionStatus getSectionStatus() {
        return sectionStatus;
    }

    public void setSectionStatus(SectionStatus sectionStatus) {
        this.sectionStatus = sectionStatus;
    }

    public StatementOfCapitalDao getStatementOfCapital() {
        return statementOfCapital;
    }

    public void setStatementOfCapital(StatementOfCapitalDao statementOfCapital) {
        this.statementOfCapital = statementOfCapital;
    }
}
