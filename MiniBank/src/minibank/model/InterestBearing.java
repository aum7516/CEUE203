package minibank.model;

/**
 * Interface with default method to compute accrued yearly interest polymorphically.
 */
public interface InterestBearing {
    double interestRate();
    long getBalance();

    default double yearlyInterest() {
        return (getBalance() * interestRate()) / 100.0;
    }
}
