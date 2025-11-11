package debt_payments.domain.exception;

public class PortfolioWriteOffNotFoundException extends RuntimeException {
    public PortfolioWriteOffNotFoundException(String message) {
        super(message);
    }
}
