package zw.co.link.PaymentsCenter.exceptions;

public class USSDProcessingException extends RuntimeException {
    public USSDProcessingException(String message) {
        super(message);
    }

    public USSDProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}