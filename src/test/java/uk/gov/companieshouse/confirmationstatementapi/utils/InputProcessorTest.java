package uk.gov.companieshouse.confirmationstatementapi.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InputProcessorTest {

    private static String regex = "[^A-Za-z\\d -]";

    static Stream<String> validStrings() {
        return Stream.of("019316-096616-323970",
                "614c66f05c553622bfa4b90c",
                "Transaction URL id exceeds 50 characters");
    }

    @ParameterizedTest
    @MethodSource("validStrings")
    void testValidIdStrings(String input) {
        String processed = InputProcessor.sanitiseString(input, regex);
        assertEquals(input, processed);
    }

    @Test
    void testXssStringIsEscaped() {
        String input = "<script>foobarTheCode()</script>\tWhatever";
        String processed = InputProcessor.sanitiseString(input, regex);
        assertEquals("\\<script\\>foobarTheCode\\(\\)\\<\\/script\\>\\\tWhatever", processed);
    }
}
