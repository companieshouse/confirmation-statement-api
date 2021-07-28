package uk.gov.companieshouse.confirmationstatementapi.model.dao.siccode;

import org.springframework.data.mongodb.core.mapping.Field;

public class SicCodeDao {
    @Field("code")
    private String code;

    @Field("description")
    private String description;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
