package minibank.repository;

import minibank.model.Account;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Dedicated storage container for bank accounts leveraging Java Collections Framework.
 * Provides sorted projections and transaction auditing.
 */
public class AccountStore {
    private final Map<String, Account> accounts = new HashMap<>();
    private final List<String> transactionHistory = new ArrayList<>();

    public synchronized void add(Account account) {
        if (account != null) {
            accounts.put(account.getAccountNumber(), account);
            transactionHistory.add("Registered account: " + account.getAccountNumber() + " for " + account.getOwnerName());
        }
    }

    public synchronized Account get(String accountNumber) {
        return accounts.get(accountNumber);
    }

    public synchronized List<Account> getAll() {
        return new ArrayList<>(accounts.values());
    }

    public synchronized void recordTransaction(String logEntry) {
        transactionHistory.add(logEntry);
    }

    public synchronized List<String> getTransactionHistory() {
        return new ArrayList<>(transactionHistory);
    }

    /**
     * Returns accounts ordered in descending order of current balance using a Comparator.
     */
    public synchronized ArrayList<Account> sortedByBalance() {
        ArrayList<Account> list = new ArrayList<>(accounts.values());
        list.sort(Comparator.comparingLong(Account::getBalance).reversed());
        return list;
    }
}
