package uk.gov.companieshouse.confirmationstatementapi.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UrlIdValidatorTest {

    private static final String ID_NAME = "Test";

    @Test
    void testAlphanumericStringWithSpecialCharsIsValid() {
        String str  = "123-abc$456%def";
        assertTrue(UrlIdValidator.isUrlIdValid(ID_NAME, str));
    }

    @Test
    void testFiftyCharsIsValid() {
        String str  = "12345678901234567890123456789012345678901234567890";
        assertTrue(UrlIdValidator.isUrlIdValid(ID_NAME, str));
    }

    @Test
    void testNullIsNotValid() {
        assertFalse(UrlIdValidator.isUrlIdValid(ID_NAME, null));
    }

    @Test
    void testEmptyIsNotValid() {
        assertFalse(UrlIdValidator.isUrlIdValid(ID_NAME, ""));
    }

    @Test
    void testWhitespacesAreNotValid() {
        assertFalse(UrlIdValidator.isUrlIdValid(ID_NAME, " "));
    }

    @Test
    void testFiftyPlusCharsIsNotValid() {
        String str  = "123456789012345678901234567890123456789012345678901";
        assertFalse(UrlIdValidator.isUrlIdValid(ID_NAME, str));
    }
}
