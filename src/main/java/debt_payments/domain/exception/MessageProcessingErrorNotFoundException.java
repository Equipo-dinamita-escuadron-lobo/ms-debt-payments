package debt_payments.domain.exception;

public class MessageProcessingErrorNotFoundException extends RuntimeException {
    public MessageProcessingErrorNotFoundException(String message) {
        super(message);
    }
}
