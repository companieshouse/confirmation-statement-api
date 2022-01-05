package uk.gov.companieshouse.confirmationstatementapi.model.mapping;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.common.DateOfBirth;
import uk.gov.companieshouse.api.model.psc.NameElementsApi;
import uk.gov.companieshouse.confirmationstatementapi.model.PersonOfSignificantControl;
import uk.gov.companieshouse.confirmationstatementapi.model.json.PersonOfSignificantControlJson;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PscsMapper {

    private static final String PSC_APPOINTMENT_TYPE_ID = "5007";

    public List<PersonOfSignificantControlJson> mapToPscsApi(List<PersonOfSignificantControl> pscList) {
        if (pscList == null) {
            return new ArrayList<>();
        }
        return pscList.stream()
               .map(this::mapToPscJson)
               .collect(Collectors.toList());
    }

    private PersonOfSignificantControlJson mapToPscJson(PersonOfSignificantControl psc) {

        var pscJson = new PersonOfSignificantControlJson();
        pscJson.setAddress(psc.getAddress());
        pscJson.setServiceAddress(psc.getServiceAddress());
        pscJson.setAppointmentType(psc.getAppointmentTypeId());
        mapAppointmentDate(psc, pscJson);

        if (StringUtils.isNotBlank(psc.getNatureOfControl())) {
            pscJson.setNaturesOfControl(psc.getNatureOfControl().split(";"));
        }

        mapDob(psc, pscJson);

        mapNames(psc, pscJson);

        pscJson.setNationality(psc.getOfficerNationality());
        pscJson.setCompanyName(psc.getSuppliedCompanyName());
        pscJson.setRegisterLocation(psc.getRegisterLocation());
        pscJson.setRegistrationNumber(psc.getRegistrationNumber());
        pscJson.setLawGoverned(psc.getLawGoverned());
        pscJson.setLegalForm(psc.getLegalForm());

        if(psc.getAppointmentTypeId().equals(PSC_APPOINTMENT_TYPE_ID)) {
            pscJson.setCountryOfResidence(psc.getUsualResidentialCountry());
        } else {
            pscJson.setCountryOfResidence(psc.getPscCountry());
        }

        return pscJson;
    }

    private void mapAppointmentDate(PersonOfSignificantControl psc, PersonOfSignificantControlJson pscJson) {
        if (StringUtils.isNotBlank(psc.getAppointmentDate())) {
            var appointmentDate = Timestamp.valueOf(psc.getAppointmentDate()).toLocalDateTime().toLocalDate();
            var isoAppointmentDate = appointmentDate.format(DateTimeFormatter.ISO_DATE);
            pscJson.setAppointmentDate(isoAppointmentDate);
        }
    }

    private void mapDob(PersonOfSignificantControl psc, PersonOfSignificantControlJson pscJson) {
        if (StringUtils.isNotBlank(psc.getOfficerDateOfBirth())) {
            var timestamp = Timestamp.valueOf(psc.getOfficerDateOfBirth());
            var localDate = timestamp.toLocalDateTime().toLocalDate();

            var dob = new DateOfBirth();
            dob.setMonth((long) localDate.getMonthValue());
            dob.setYear((long) localDate.getYear());
            pscJson.setDateOfBirth(dob);

            pscJson.setDateOfBirthIso(localDate.format(DateTimeFormatter.ISO_DATE));
        }
    }

    private void mapNames(PersonOfSignificantControl psc, PersonOfSignificantControlJson pscJson) {
        var fullName = new StringBuilder();

        var names = new String[]{
                psc.getOfficerForename1(),
                psc.getOfficerForename2(),
                psc.getOfficerSurname()
        };

        Arrays.stream(names).forEach(name -> {
            if (StringUtils.isNotBlank(name)) {
                if (fullName.length() > 0) {
                    fullName.append(" ");
                }
                fullName.append(name);
            }
        });
        pscJson.setName(fullName.toString());

        var nameElementsApi = new NameElementsApi();
        nameElementsApi.setForename(psc.getOfficerForename1());
        nameElementsApi.setOtherForenames(psc.getOfficerForename2());
        nameElementsApi.setSurname(psc.getOfficerSurname());
        pscJson.setNameElements(nameElementsApi);
    }
}
