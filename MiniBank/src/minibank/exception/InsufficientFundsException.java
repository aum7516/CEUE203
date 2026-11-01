package minibank.exception;

/**
 * Thrown when a withdrawal or transfer fails due to lack of funds or overdraft limits.
 * Carries the exact shortfall amount for explicit error feedback.
 */
public class InsufficientFundsException extends BankException {
    private final long shortfall;

    public InsufficientFundsException(String message, long shortfall) {
        super(message + " (Shortfall: ₹" + shortfall + ")");
        this.shortfall = shortfall;
    }

    public long getShortfall() {
        return shortfall;
    }
}
