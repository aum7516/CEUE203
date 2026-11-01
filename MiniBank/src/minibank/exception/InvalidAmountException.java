package minibank.exception;

/**
 * Thrown when a non-positive or invalid currency amount is provided for transaction.
 */
public class InvalidAmountException extends BankException {
    public InvalidAmountException(String message) {
        super(message);
    }
}
