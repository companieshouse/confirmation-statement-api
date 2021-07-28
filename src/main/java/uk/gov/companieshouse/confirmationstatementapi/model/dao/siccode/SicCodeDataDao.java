package uk.gov.companieshouse.confirmationstatementapi.model.dao.siccode;

import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.SectionDataDao;

public class SicCodeDataDao extends SectionDataDao{

    @Field("sic_code")
    private SicCodeDao sicCode;

    public SicCodeDao getSicCode() {
        return sicCode;
    }

    public void setSicCode(SicCodeDao sicCode) {
        this.sicCode = sicCode;
    }
}
