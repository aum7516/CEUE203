package minibank.model;

/**
 * Fixed Deposit account offering high interest (7.0%) but locked against on-demand withdrawals.
 */
public class FixedDepositAccount extends Account {
    private static final long serialVersionUID = 1L;
    private final int tenureMonths;

    public FixedDepositAccount(String ownerName, long balance, int tenureMonths) {
        super(ownerName, balance);
        this.tenureMonths = tenureMonths;
    }

    public FixedDepositAccount(String ownerName, long balance) {
        this(ownerName, balance, 12); // Default 12-month tenure
    }

    public int getTenureMonths() {
        return tenureMonths;
    }

    @Override
    public double interestRate() {
        return 7.0;
    }

    @Override
    public boolean canWithdraw(long amount) {
        // Funds are locked in FD until maturity
        return false;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Type: Fixed Deposit (%d Mo. Lock)", tenureMonths);
    }
}
