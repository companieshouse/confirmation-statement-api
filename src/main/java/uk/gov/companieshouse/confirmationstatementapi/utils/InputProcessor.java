package uk.gov.companieshouse.confirmationstatementapi.utils;

import java.util.HashSet;
import java.util.Set;

import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.ID_PATTERN;

public class InputProcessor {

    private InputProcessor() {
        throw new IllegalStateException("Utility class");
    }

    public static String sanitiseString(String input) {
        var matcher = ID_PATTERN.matcher(input);
        Set<String> allMatches = new HashSet<>();

        while (matcher.find()) {
            allMatches.add(matcher.group());
        }

        for(String unsafe : allMatches) {
            input = input.replace(unsafe, "\\" + unsafe);
        }
        return input;
    }
}
