package minibank.model;

import minibank.exception.BankException;
import minibank.exception.InsufficientFundsException;
import minibank.exception.InvalidAmountException;
import minibank.model.annotation.Id;
import minibank.model.annotation.MaxLength;
import minibank.model.annotation.Positive;

import java.io.Serializable;
import java.util.Objects;

/**
 * Abstract base class for all bank accounts.
 * Encapsulates core state, enforces thread safety via synchronized operations,
 * and integrates custom annotations and standard Java contracts.
 */
public abstract class Account implements Transactable, InterestBearing, Comparable<Account>, Serializable {
    private static final long serialVersionUID = 1L;
    private static long accountCounter = 1000;

    @Id
    protected final String accountNumber;

    @MaxLength(50)
    protected String ownerName;

    @Positive
    protected long balance;

    protected boolean active;

    public Account(String ownerName, long balance) {
        this.accountNumber = generateAccountNumber();
        this.ownerName = ownerName;
        this.balance = balance;
        this.active = true;
    }

    public Account(String ownerName) {
        this(ownerName, 0);
    }

    private static synchronized String generateAccountNumber() {
        return "AC" + (++accountCounter);
    }

    public static synchronized void resetCounter(long nextVal) {
        accountCounter = nextVal;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    @Override
    public long getBalance() {
        return balance;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Polymorphic interest rate percentage (e.g. 4.0 for Savings, 0.0 for Current, 7.0 for FD).
     */
    @Override
    public abstract double interestRate();

    /**
     * Polymorphic rule verifying if an amount can be debited given account rules and limits.
     */
    public abstract boolean canWithdraw(long amount);

    /**
     * Thread-safe deposit with positive amount guard.
     */
    @Override
    public synchronized void deposit(long amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Deposit amount must be strictly positive: ₹" + amount);
        }
        this.balance += amount;
    }

    /**
     * Thread-safe withdrawal with rule checking and custom shortfall exception.
     */
    @Override
    public synchronized boolean withdraw(long amount) throws InsufficientFundsException, InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be strictly positive: ₹" + amount);
        }
        if (!canWithdraw(amount)) {
            long shortfall = amount - this.balance;
            throw new InsufficientFundsException("Withdrawal rejected: Exceeds available balance/overdraft.", shortfall > 0 ? shortfall : amount);
        }
        this.balance -= amount;
        return true;
    }

    /**
     * Deadlock-free funds transfer between accounts by enforcing deterministic lock acquisition order.
     */
    public void transfer(Account target, long amount) throws BankException {
        if (target == null) {
            throw new BankException("Target account cannot be null.");
        }
        if (this.accountNumber.equals(target.getAccountNumber())) {
            throw new BankException("Cannot transfer funds to the same account.");
        }

        // Deterministic lock ordering based on account number to eliminate deadlock
        Account firstLock = this.accountNumber.compareTo(target.getAccountNumber()) < 0 ? this : target;
        Account secondLock = (firstLock == this) ? target : this;

        synchronized (firstLock) {
            synchronized (secondLock) {
                this.withdraw(amount);
                target.deposit(amount);
            }
        }
    }

    @Override
    public int compareTo(Account other) {
        return this.accountNumber.compareTo(other.accountNumber);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account account)) return false;
        return Objects.equals(accountNumber, account.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }

    @Override
    public String toString() {
        return String.format("[%s] Owner: %-15s | Balance: ₹%,9d | Rate: %4.1f%% | Yearly Interest: ₹%,8.2f",
                accountNumber, ownerName, balance, interestRate(), yearlyInterest());
    }
}
