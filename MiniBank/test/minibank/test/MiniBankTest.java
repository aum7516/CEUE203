package minibank.test;

import minibank.concurrency.AccountWorker;
import minibank.exception.InsufficientFundsException;
import minibank.exception.InvalidAmountException;
import minibank.model.Account;
import minibank.model.CurrentAccount;
import minibank.model.SavingsAccount;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Automated JUnit 4 unit and concurrency test suite for MiniBank core.
 */
public class MiniBankTest {
    private SavingsAccount savings;
    private CurrentAccount current;

    @Before
    public void setUp() {
        Account.resetCounter(5000);
        savings = new SavingsAccount("Alice", 10000, 1000);
        current = new CurrentAccount("Bob", 5000, 5000);
    }

    @Test
    public void testDepositIncreasesBalance() throws Exception {
        savings.deposit(3500);
        assertEquals("Balance should increase by 3500", 13500, savings.getBalance());
    }

    @Test(expected = InvalidAmountException.class)
    public void testNegativeDepositThrowsException() throws Exception {
        savings.deposit(-500);
    }

    @Test
    public void testWithdrawalWithinLimitSucceeds() throws Exception {
        boolean success = savings.withdraw(4000);
        assertTrue("Withdrawal within limit should succeed", success);
        assertEquals("Balance should be reduced by 4000", 6000, savings.getBalance());
    }

    @Test(expected = InsufficientFundsException.class)
    public void testWithdrawalOverLimitThrowsException() throws Exception {
        // Savings has 10000, minBalance is 1000. Maximum withdrawable is 9000.
        // Trying to withdraw 9500 leaves 500, which is below minBalance -> throws InsufficientFundsException
        savings.withdraw(9500);
    }

    @Test
    public void testCurrentAccountOverdraftWithdrawal() throws Exception {
        // Current has 5000, overdraft limit 5000. Total withdrawable 10000.
        current.withdraw(8000);
        assertEquals("Balance can fall into negative overdraft", -3000, current.getBalance());
    }

    @Test
    public void testConcurrentDepositsMaintainThreadSafety() throws Exception {
        long initial = savings.getBalance();
        int threadsCount = 10;
        int txPerThread = 100;
        long amountPerTx = 10;

        Thread[] threads = new Thread[threadsCount];
        for (int i = 0; i < threadsCount; i++) {
            threads[i] = new Thread(new AccountWorker(savings, txPerThread, amountPerTx));
            threads[i].start();
        }

        for (Thread t : threads) {
            t.join();
        }

        long expected = initial + (long) threadsCount * txPerThread * amountPerTx;
        assertEquals("Synchronized deposits must preserve thread safety without race conditions", expected, savings.getBalance());
    }
}
