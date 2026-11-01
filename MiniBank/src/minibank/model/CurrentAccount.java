package minibank.model;

/**
 * Current account tailored for commercial usage with overdraft protection and 0% interest.
 */
public class CurrentAccount extends Account implements Premium {
    private static final long serialVersionUID = 1L;
    private final long overdraftLimit;

    public CurrentAccount(String ownerName, long balance, long overdraftLimit) {
        super(ownerName, balance);
        this.overdraftLimit = overdraftLimit;
    }

    public CurrentAccount(String ownerName, long balance) {
        this(ownerName, balance, 10000); // Default ₹10,000 overdraft limit
    }

    public long getOverdraftLimit() {
        return overdraftLimit;
    }

    @Override
    public double interestRate() {
        return 0.0;
    }

    @Override
    public boolean canWithdraw(long amount) {
        return (this.balance - amount) >= -overdraftLimit;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Type: Current (Overdraft: ₹%,d)", overdraftLimit);
    }
}
