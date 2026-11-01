package minibank.exception;

/**
 * Thrown when an account lookup by account number fails.
 */
public class AccountNotFoundException extends BankException {
    public AccountNotFoundException(String accountNumber) {
        super("Account not found with ID: " + accountNumber);
    }
}
