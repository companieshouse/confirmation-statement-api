package uk.gov.companieshouse.confirmationstatementapi.model.dao.siccode;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.SectionDataDao;

public class SicCodeDataDao extends SectionDataDao{

    @Field("sic_codes")
    private List<String> sicCodes;

    public List<String> getSicCodes() {
        return sicCodes;
    }

    public void setSicCodes(List<String> sicCodes) {
        this.sicCodes = sicCodes;
    }

    // @Field("sic_codes")
    // private List<SicCodeDao> sicCode;

    // public List<SicCodeDao> getSicCode() {
    //     return sicCode;
    // }

    // public void setSicCode(List<SicCodeDao> sicCode) {
    //     this.sicCode = sicCode;
    // }
}
