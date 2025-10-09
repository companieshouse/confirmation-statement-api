package uk.gov.companieshouse.confirmationstatementapi.model.dao.siccode;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.SectionDataDao;

public class SicCodeDataDao extends SectionDataDao{

    @Field("sic_codes")
    private List<SicCodeDao> sicCodes;

    public List<SicCodeDao> getSicCode() {
        return sicCodes;
    }

    public void setSicCode(List<SicCodeDao> sicCodes) {
        this.sicCodes = sicCodes;
    }
}
