package zw.co.link.FinPay.exceptions;

// Custom exception for API errors
public class HotUserApiException extends RuntimeException {
    public HotUserApiException(String message) {
        super(message);
    }

    public HotUserApiException(String message, Throwable cause) {
        super(message, cause);
    }
}