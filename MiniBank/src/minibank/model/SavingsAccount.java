package minibank.model;

/**
 * Savings account requiring a minimum balance and providing 4.0% annual interest.
 */
public class SavingsAccount extends Account {
    private static final long serialVersionUID = 1L;
    private final long minBalance;

    public SavingsAccount(String ownerName, long balance, long minBalance) {
        super(ownerName, balance);
        this.minBalance = minBalance;
    }

    public SavingsAccount(String ownerName, long balance) {
        this(ownerName, balance, 1000); // Default ₹1000 minimum balance
    }

    public long getMinBalance() {
        return minBalance;
    }

    @Override
    public double interestRate() {
        return 4.0;
    }

    @Override
    public boolean canWithdraw(long amount) {
        return (this.balance - amount) >= minBalance;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Type: Savings (MinBal: ₹%,d)", minBalance);
    }
}
