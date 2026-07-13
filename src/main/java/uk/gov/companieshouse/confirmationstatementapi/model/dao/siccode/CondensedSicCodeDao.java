package uk.gov.companieshouse.confirmationstatementapi.model.dao.siccode;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "condensed_sic_codes")
public class CondensedSicCodeDao {

    @Id
    @Field("_id")
    private String id;

    @TextIndexed
    @Field("sic_code")
    private String sicCode;

    @Field("sic_description")
    private String sicDescription;

    public CondensedSicCodeDao() {
    }

    public CondensedSicCodeDao(String id, String sicCode, String sicDescription) {
        this.id = id;
        this.sicCode = sicCode;
        this.sicDescription = sicDescription;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSicCode() {
        return sicCode;
    }

    public void setSicCode(String sicCode) {
        this.sicCode = sicCode;
    }

    public String getSicDescription() {
        return sicDescription;
    }

    public void setSicDescription(String sicDescription) {
        this.sicDescription = sicDescription;
    }
}
