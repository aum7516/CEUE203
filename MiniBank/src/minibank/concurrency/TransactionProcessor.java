package minibank.concurrency;

import minibank.model.Account;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * High-throughput transaction engine managing an executor thread pool and concurrent map.
 */
public class TransactionProcessor {
    private final ExecutorService executor;
    private final ConcurrentHashMap<String, Account> concurrentAccounts;

    public TransactionProcessor(int poolSize) {
        this.executor = Executors.newFixedThreadPool(poolSize);
        this.concurrentAccounts = new ConcurrentHashMap<>();
    }

    public TransactionProcessor() {
        this(4);
    }

    public void registerAccount(Account account) {
        if (account != null) {
            concurrentAccounts.put(account.getAccountNumber(), account);
        }
    }

    public Account getAccount(String accountNumber) {
        return concurrentAccounts.get(accountNumber);
    }

    public void submit(Runnable task) {
        executor.execute(task);
    }

    public void stop() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
