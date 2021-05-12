package uk.gov.companieshouse.confirmationstatementapi.exception;

public class ServiceException extends Exception {
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
