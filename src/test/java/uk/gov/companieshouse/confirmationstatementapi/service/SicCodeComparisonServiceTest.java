package uk.gov.companieshouse.confirmationstatementapi.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.confirmationstatementapi.model.json.siccode.SicCodeJson;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
public class SicCodeComparisonServiceTest {

    @InjectMocks
    private SicCodeComparisonService sicCodeComparisonService;

    @ParameterizedTest
    @CsvSource({
            "'70229,71122,74909,01120', '70229,71122,74909,01120', false",
            "'70229,71122,74909,01120', '01120,74909,71122,70229', false",
            "'70229,71122,74909,01120', '70229,01120', true",
            "'70229,71122,74909,01120', '74909', true",
            "'', '70229,71122', true",
            "'01120,70229', '70229,74909', true",
            "'71122', '71122,70229,74909,01120', true",
            "'', '', false",
            "'74909,01120', '', true"
    })
    void testSicCodeHasDifferences(String companyProfileSicCodes, String submissionSicCodes, boolean expectedHasDifferences) {
        String[] companyProfileSicCodeList = companyProfileSicCodes.isBlank() ? null : companyProfileSicCodes.split(",");
        List<String> submissionSicCodeList = submissionSicCodes.isBlank() ? null : List.of((submissionSicCodes.split(",")));

        List<SicCodeJson> sicCodeJsonList = buildSicCodeJsonList(submissionSicCodeList);

        boolean actualHasDifferences = sicCodeComparisonService.hasDifferences(sicCodeJsonList, companyProfileSicCodeList);
        assertEquals(expectedHasDifferences, actualHasDifferences);
    }

    @Test
    void shouldReturnFalseFromHasDifferencesWhenBothSicCodeListAreEmpty() {
        String[] companyProfileSicCodeList = new String[]{};
        List<String> submissionSicCodeList = List.of();

        List<SicCodeJson> sicCodeJsonList = buildSicCodeJsonList(submissionSicCodeList);

        boolean actualHasDifferences = sicCodeComparisonService.hasDifferences(sicCodeJsonList, companyProfileSicCodeList);
        assertFalse(actualHasDifferences);
    }


    private static List<SicCodeJson> buildSicCodeJsonList(List<String> sicCodeList) {
        if (null != sicCodeList) {
            List<SicCodeJson> sicCodeJsonList = new ArrayList<>();
            sicCodeList
                    .stream()
                    .forEach(sicCode -> {
                        SicCodeJson sicCodeJson = new SicCodeJson();
                        sicCodeJson.setCode(sicCode);
                        sicCodeJsonList.add(sicCodeJson);
                    });

            return sicCodeJsonList;
        }
        return null;
    }
}
