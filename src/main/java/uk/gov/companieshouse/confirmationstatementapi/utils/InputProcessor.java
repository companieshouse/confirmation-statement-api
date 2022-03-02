package uk.gov.companieshouse.confirmationstatementapi.utils;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class InputProcessor {

    private InputProcessor() {
        throw new IllegalStateException("Utility class");
    }

    public static String sanitiseString(String input, String regex) {
        var matcher = Pattern.compile(regex).matcher(input);
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
