package uk.gov.companieshouse.confirmationstatementapi.exception;

public class RegisteredEmailNotFoundException extends Exception{
    public RegisteredEmailNotFoundException(String message) {
        super(message);
    }
}
