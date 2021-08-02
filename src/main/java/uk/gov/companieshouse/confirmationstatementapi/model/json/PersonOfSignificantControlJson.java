package uk.gov.companieshouse.confirmationstatementapi.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.api.model.psc.PscApi;

public class PersonOfSignificantControlJson extends PscApi {

    @JsonProperty("appointment_type")
    private Integer appointmentType;

    @JsonProperty("service_address_line_1")
    private String serviceAddressLine1;

    @JsonProperty("service_address_post_code")
    private String serviceAddressPostCode;

    @JsonProperty("service_address_post_town")
    private String serviceAddressPostTown;

    public Integer getAppointmentType() {
        return appointmentType;
    }

    public void setAppointmentType(Integer appointmentType) {
        this.appointmentType = appointmentType;
    }

    public String getServiceAddressLine1() {
        return serviceAddressLine1;
    }

    public void setServiceAddressLine1(String serviceAddressLine1) {
        this.serviceAddressLine1 = serviceAddressLine1;
    }

    public String getServiceAddressPostCode() {
        return serviceAddressPostCode;
    }

    public void setServiceAddressPostCode(String serviceAddressPostCode) {
        this.serviceAddressPostCode = serviceAddressPostCode;
    }

    public String getServiceAddressPostTown() {
        return serviceAddressPostTown;
    }

    public void setServiceAddressPostTown(String serviceAddressPostTown) {
        this.serviceAddressPostTown = serviceAddressPostTown;
    }
}
