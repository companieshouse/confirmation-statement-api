package uk.gov.companieshouse.confirmationstatementapi.model.mapping;

import org.apache.commons.lang3.StringUtils;
import uk.gov.companieshouse.api.model.common.Address;
import uk.gov.companieshouse.api.model.common.DateOfBirth;
import uk.gov.companieshouse.api.model.psc.NameElementsApi;
import uk.gov.companieshouse.confirmationstatementapi.model.PersonOfSignificantControl;
import uk.gov.companieshouse.confirmationstatementapi.model.json.PersonOfSignificantControlJson;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PscsMapper {

    public List<PersonOfSignificantControlJson> mapToPscsApi(List<PersonOfSignificantControl> pscList) {
        if (pscList == null) {
            return new ArrayList<>();
        }
        return pscList.stream()
               .map(this::mapToPscJson)
               .collect(Collectors.toList());
    }

    private PersonOfSignificantControlJson mapToPscJson(PersonOfSignificantControl psc) {
        Address address = new Address();
        address.setAddressLine1(psc.getAddressLine1());
        address.setPoBox(psc.getPoBox());
        address.setPostalCode(psc.getPostCode());
        address.setLocality(psc.getPostTown());
        address.setCountry(psc.getCountryName());
        address.setPremises(psc.getHouseNameNumber());
        address.setAddressLine2(psc.getStreet());
        address.setRegion(psc.getRegion());

        PersonOfSignificantControlJson pscJson = new PersonOfSignificantControlJson();
        pscJson.setAddress(address);
        pscJson.setAppointmentType(psc.getAppointmentTypeId());

        pscJson.setNaturesOfControl(psc.getNatureOfControl().split(";"));

        var dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate localDate = LocalDate.parse(psc.getOfficerDateOfBirth(), dateTimeFormatter);
        DateOfBirth dob = new DateOfBirth();
        dob.setMonth((long) localDate.getMonthValue());
        dob.setYear((long) localDate.getYear());
        pscJson.setDateOfBirth(dob);

        mapNames(psc, pscJson);

        pscJson.setNationality(psc.getOfficerNationality());

        pscJson.setServiceAddressLine1(psc.getServiceAddressLine1());
        pscJson.setServiceAddressPostCode(psc.getServiceAddressPostCode());
        pscJson.setServiceAddressPostTown(psc.getServiceAddressPostTown());

        // no company name or super secure ind or area
        pscJson.setSecureOfficerInd(psc.getSuperSecurePscInd());
        pscJson.setCompanyName(psc.getSuppliedCompanyName());

        return pscJson;
    }

    private void mapNames(PersonOfSignificantControl psc, PersonOfSignificantControlJson pscJson) {
        StringBuilder fullName = new StringBuilder();

        String[] names = {
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

        NameElementsApi nameElementsApi = new NameElementsApi();
        nameElementsApi.setForename(psc.getOfficerForename1());
        nameElementsApi.setOtherForenames(psc.getOfficerForename2());
        nameElementsApi.setSurname(psc.getOfficerSurname());
        pscJson.setNameElements(nameElementsApi);
    }
}
