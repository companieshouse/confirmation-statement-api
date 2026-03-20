package uk.gov.companieshouse.confirmationstatementapi.service;

import org.springframework.stereotype.Service;
import uk.gov.companieshouse.confirmationstatementapi.model.json.siccode.SicCodeJson;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SicCodeComparisonService {

    public boolean hasDifferences(List<SicCodeJson> sicCodeJsonList, String[] companyProfileSicCodeList) {
        if (sicCodeJsonList == null && companyProfileSicCodeList == null) {
            return false; //both null no differences
        }

        if (sicCodeJsonList == null || companyProfileSicCodeList == null) {
            return true; //one null, one not
        }

        if (sicCodeJsonList.isEmpty() && companyProfileSicCodeList.length == 0) {
            return false; //both empty
        }

        Set<String> sicCodeJsonSet = sicCodeJsonList.stream()
                .map(SicCodeJson::getCode)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Set<String> sicCodeCompanyProfileSet = Arrays.stream(companyProfileSicCodeList)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return !sicCodeJsonSet.equals(sicCodeCompanyProfileSet);
    }
}
