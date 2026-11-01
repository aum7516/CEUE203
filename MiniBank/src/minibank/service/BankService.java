package minibank.service;

import minibank.concurrency.AccountWorker;
import minibank.concurrency.TransactionProcessor;
import minibank.exception.AccountNotFoundException;
import minibank.exception.BankException;
import minibank.model.Account;
import minibank.model.CurrentAccount;
import minibank.model.FixedDepositAccount;
import minibank.model.SavingsAccount;
import minibank.model.annotation.Audit;
import minibank.repository.Repository;
import minibank.util.AnnotationValidator;
import minibank.util.ReportGenerator;
import minibank.util.StatePersister;
import minibank.util.TransactionLog;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Core business facade orchestrating account repository, validation, logging, and concurrency.
 */
public class BankService {
    private final Repository<Account> repository;
    private final Path logFilePath;

    public BankService(Path logFilePath) {
        this.repository = new Repository<>();
        this.logFilePath = logFilePath;
    }

    public BankService() {
        this(Paths.get("data", "transactions.log"));
    }

    @Audit(action = "OPEN_ACCOUNT")
    public Account openSavingsAccount(String ownerName, long openingBalance, long minBalance) throws BankException {
        SavingsAccount acc = new SavingsAccount(ownerName, openingBalance, minBalance);
        validateAndSave(acc);
        log("OPEN_ACCOUNT (SAVINGS) | " + acc.getAccountNumber() + " | Owner: " + ownerName + " | Bal: ₹" + openingBalance);
        return acc;
    }

    @Audit(action = "OPEN_ACCOUNT")
    public Account openCurrentAccount(String ownerName, long openingBalance, long overdraftLimit) throws BankException {
        CurrentAccount acc = new CurrentAccount(ownerName, openingBalance, overdraftLimit);
        validateAndSave(acc);
        log("OPEN_ACCOUNT (CURRENT) | " + acc.getAccountNumber() + " | Owner: " + ownerName + " | Bal: ₹" + openingBalance);
        return acc;
    }

    @Audit(action = "OPEN_ACCOUNT")
    public Account openFixedDeposit(String ownerName, long openingBalance, int tenureMonths) throws BankException {
        FixedDepositAccount acc = new FixedDepositAccount(ownerName, openingBalance, tenureMonths);
        validateAndSave(acc);
        log("OPEN_ACCOUNT (FD) | " + acc.getAccountNumber() + " | Owner: " + ownerName + " | Bal: ₹" + openingBalance);
        return acc;
    }

    @Audit(action = "DEPOSIT")
    public void deposit(String accountNumber, long amount) throws BankException {
        Account account = getAccount(accountNumber);
        account.deposit(amount);
        log("DEPOSIT | " + accountNumber + " | Amount: ₹" + amount + " | New Balance: ₹" + account.getBalance());
    }

    @Audit(action = "WITHDRAW")
    public void withdraw(String accountNumber, long amount) throws BankException {
        Account account = getAccount(accountNumber);
        account.withdraw(amount);
        log("WITHDRAW | " + accountNumber + " | Amount: ₹" + amount + " | New Balance: ₹" + account.getBalance());
    }

    @Audit(action = "TRANSFER")
    public void transfer(String fromAccNum, String toAccNum, long amount) throws BankException {
        Account from = getAccount(fromAccNum);
        Account to = getAccount(toAccNum);
        from.transfer(to, amount);
        log("TRANSFER | From: " + fromAccNum + " | To: " + toAccNum + " | Amount: ₹" + amount);
    }

    public Account getAccount(String accountNumber) throws AccountNotFoundException {
        Account account = repository.findById(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException(accountNumber);
        }
        return account;
    }

    public List<Account> listAllAccounts() {
        return repository.findAll();
    }

    public List<Account> listAccountsSortedByBalance() {
        List<Account> accounts = new ArrayList<>(repository.findAll());
        accounts.sort(Comparator.comparingLong(Account::getBalance).reversed());
        return accounts;
    }

    public void persistState(Path file) throws IOException {
        List<Account> accounts = repository.findAll();
        StatePersister.save(accounts.toArray(new Account[0]), file);
    }

    public void loadState(Path file) throws IOException, ClassNotFoundException {
        Account[] loaded = StatePersister.load(file);
        for (Account acc : loaded) {
            repository.save(acc);
        }
    }

    public String generateReport(Path logsDir, Path reportOutput) throws IOException {
        return ReportGenerator.generateDailyReport(logsDir, reportOutput);
    }

    public Repository<Account> getRepository() {
        return repository;
    }

    public void runBatchSimulation(String targetAccNum, int workerCount, int txPerWorker, long amountPerTx) throws Exception {
        Account account = getAccount(targetAccNum);
        long startingBalance = account.getBalance();
        System.out.printf("Starting concurrent batch simulation on %s (Current Balance: ₹%,d)...\n", targetAccNum, startingBalance);

        TransactionProcessor processor = new TransactionProcessor(workerCount);
        Thread[] threads = new Thread[workerCount];
        for (int i = 0; i < workerCount; i++) {
            AccountWorker worker = new AccountWorker(account, txPerWorker, amountPerTx);
            threads[i] = new Thread(worker, "WorkerThread-" + (i + 1));
            threads[i].start();
        }

        for (Thread t : threads) {
            t.join();
        }
        processor.stop();

        long expectedBalance = startingBalance + (long) workerCount * txPerWorker * amountPerTx;
        System.out.printf("Batch execution completed! Final Balance: ₹%,d (Expected: ₹%,d)\n", account.getBalance(), expectedBalance);
        log(String.format("BATCH_SIMULATION | %s | Workers: %d | Total Inflow: ₹%,d | Final Balance: ₹%,d",
                targetAccNum, workerCount, (expectedBalance - startingBalance), account.getBalance()));
    }

    private void validateAndSave(Account acc) throws BankException {
        String[] errors = AnnotationValidator.validate(acc);
        if (errors.length > 0) {
            throw new BankException("Validation Failed: " + String.join("; ", errors));
        }
        repository.save(acc);
    }

    private void log(String line) {
        try {
            TransactionLog.append(logFilePath, line);
        } catch (IOException e) {
            System.err.println("Failed to write to transaction log: " + e.getMessage());
        }
    }
}
