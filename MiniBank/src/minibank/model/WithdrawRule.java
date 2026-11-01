package minibank.model;

/**
 * Functional interface representing customizable authorization rules for withdrawals.
 */
@FunctionalInterface
public interface WithdrawRule {
    boolean allow(Account account, long amount);
}
