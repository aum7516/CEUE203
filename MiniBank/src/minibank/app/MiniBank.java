package minibank.app;

import minibank.exception.BankException;
import minibank.exception.InsufficientFundsException;
import minibank.model.*;
import minibank.repository.Repository;
import minibank.service.BankService;
import minibank.util.AnnotationValidator;
import minibank.util.TransactionAuditSession;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

/**
 * Main application console entry point for the MiniBank Banking System.
 * Connects all OOP semester modules into a unified, interactive terminal application.
 */
public class MiniBank {
    private static final BankInfo BANK_INFO = new BankInfo("Charusat MiniBank", "CSPIT Changa");
    private static final Path STATE_FILE = Paths.get("data", "accounts.ser");
    private static final Path LOGS_DIR = Paths.get("data", "logs");
    private static final Path LOG_FILE = LOGS_DIR.resolve("transactions.log");
    private static final Path REPORT_FILE = Paths.get("data", "daily_report.txt");

    private final BankService service;
    private final Scanner scanner;

    public MiniBank() {
        this.service = new BankService(LOG_FILE);
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        MiniBank app = new MiniBank();
        app.initializeAndRun();
    }

    public void initializeAndRun() {
        System.out.println(BANK_INFO);
        System.out.println("System Initializing: Loading persistent account state...");

        try {
            service.loadState(STATE_FILE);
            int count = service.listAllAccounts().size();
            if (count == 0) {
                System.out.println("First run detected. Seeding starter accounts...");
                seedDemoData();
                count = service.listAllAccounts().size();
            }
            System.out.printf("Persistent state ready (%d accounts registered).\n", count);
        } catch (Exception e) {
            System.out.println("No existing account database found. Initializing starter accounts...");
            seedDemoData();
        }

        boolean running = true;
        while (running) {
            printMenu();
            System.out.print("Select an option (1-10): ");
            String input = scanner.nextLine().trim();
            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 10.\n");
                continue;
            }

            MenuOption option = MenuOption.fromChoice(choice);
            if (option == null) {
                System.out.println("Invalid choice. Please select an option between 1 and 10.\n");
                continue;
            }

            // Modern switch expression dispatch
            switch (option) {
                case OPEN_ACCOUNT -> handleOpenAccount();
                case DEPOSIT -> handleDeposit();
                case WITHDRAW -> handleWithdraw();
                case TRANSFER -> handleTransfer();
                case STATEMENT -> handleStatement();
                case LIST_ACCOUNTS -> handleListAccounts();
                case BATCH_PROCESS -> handleBatchProcess();
                case SUMMARY_REPORT -> handleSummaryReport();
                case DEMO_FEATURES -> handleDemonstrations();
                case EXIT -> {
                    handleExit();
                    running = false;
                }
            }
            System.out.println();
        }
    }

    private void printMenu() {
        System.out.println("\n----------------- MAIN APPLICATION MENU -----------------");
        for (MenuOption opt : MenuOption.values()) {
            System.out.printf("  [%2d] %s\n", opt.getOptionNumber(), opt.getDescription());
        }
        System.out.println("---------------------------------------------------------");
    }

    private void handleOpenAccount() {
        System.out.println("\n--- [1] OPEN NEW ACCOUNT ---");
        System.out.println("Select Account Type: 1) Savings  2) Current  3) Fixed Deposit");
        System.out.print("Choice: ");
        String typeChoice = scanner.nextLine().trim();

        System.out.print("Enter Owner Full Name: ");
        String owner = scanner.nextLine().trim();

        System.out.print("Enter Initial Deposit Amount (₹): ");
        long balance;
        try {
            balance = Long.parseLong(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Error: Balance must be a valid integer number.");
            return;
        }

        try (TransactionAuditSession session = new TransactionAuditSession("OPEN_ACCOUNT")) {
            Account created = switch (typeChoice) {
                case "1" -> {
                    System.out.print("Enter Minimum Balance requirement (default 1000): ");
                    String minStr = scanner.nextLine().trim();
                    long minBal = minStr.isEmpty() ? 1000 : Long.parseLong(minStr);
                    yield service.openSavingsAccount(owner, balance, minBal);
                }
                case "2" -> {
                    System.out.print("Enter Overdraft Limit (default 10000): ");
                    String odStr = scanner.nextLine().trim();
                    long odLimit = odStr.isEmpty() ? 10000 : Long.parseLong(odStr);
                    yield service.openCurrentAccount(owner, balance, odLimit);
                }
                case "3" -> {
                    System.out.print("Enter Fixed Deposit Lock Tenure in Months (e.g. 12): ");
                    String tenStr = scanner.nextLine().trim();
                    int tenure = tenStr.isEmpty() ? 12 : Integer.parseInt(tenStr);
                    yield service.openFixedDeposit(owner, balance, tenure);
                }
                default -> {
                    System.out.println("Invalid account type selected.");
                    yield null;
                }
            };

            if (created != null) {
                System.out.println("SUCCESS: Account Created Successfully!");
                System.out.println("Account Details: " + created);
            }
        } catch (BankException e) {
            System.err.println("Banking Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected Error: " + e.getMessage());
        }
    }

    private void handleDeposit() {
        System.out.println("\n--- [2] DEPOSIT FUNDS ---");
        System.out.print("Enter Account Number (e.g. AC1001): ");
        String accNum = scanner.nextLine().trim().toUpperCase();

        System.out.print("Enter Amount to Deposit (₹): ");
        long amount;
        try {
            amount = Long.parseLong(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Error: Amount must be an integer.");
            return;
        }

        try (TransactionAuditSession session = new TransactionAuditSession("DEPOSIT")) {
            service.deposit(accNum, amount);
            Account acc = service.getAccount(accNum);
            System.out.printf("SUCCESS: Deposited ₹%,d into %s. New Balance: ₹%,d\n", amount, accNum, acc.getBalance());
        } catch (BankException e) {
            System.err.println("Transaction Failed: " + e.getMessage());
        }
    }

    private void handleWithdraw() {
        System.out.println("\n--- [3] WITHDRAW FUNDS ---");
        System.out.print("Enter Account Number (e.g. AC1001): ");
        String accNum = scanner.nextLine().trim().toUpperCase();

        System.out.print("Enter Amount to Withdraw (₹): ");
        long amount;
        try {
            amount = Long.parseLong(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Error: Amount must be an integer.");
            return;
        }

        try (TransactionAuditSession session = new TransactionAuditSession("WITHDRAW")) {
            service.withdraw(accNum, amount);
            Account acc = service.getAccount(accNum);
            System.out.printf("SUCCESS: Withdrew ₹%,d from %s. New Balance: ₹%,d\n", amount, accNum, acc.getBalance());
        } catch (InsufficientFundsException e) {
            System.err.printf("DECLINED (Insufficient Funds): %s [Shortfall: ₹%,d]\n", e.getMessage(), e.getShortfall());
        } catch (BankException e) {
            System.err.println("Transaction Failed: " + e.getMessage());
        }
    }

    private void handleTransfer() {
        System.out.println("\n--- [4] DEADLOCK-FREE FUND TRANSFER ---");
        System.out.print("Enter Source Account Number: ");
        String from = scanner.nextLine().trim().toUpperCase();

        System.out.print("Enter Target Account Number: ");
        String to = scanner.nextLine().trim().toUpperCase();

        System.out.print("Enter Transfer Amount (₹): ");
        long amount;
        try {
            amount = Long.parseLong(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Error: Amount must be an integer.");
            return;
        }

        try (TransactionAuditSession session = new TransactionAuditSession("TRANSFER")) {
            service.transfer(from, to, amount);
            Account sourceAcc = service.getAccount(from);
            Account targetAcc = service.getAccount(to);
            System.out.printf("SUCCESS: Transferred ₹%,d from %s to %s.\n", amount, from, to);
            System.out.printf("  * %s Balance: ₹%,d\n", from, sourceAcc.getBalance());
            System.out.printf("  * %s Balance: ₹%,d\n", to, targetAcc.getBalance());
        } catch (BankException e) {
            System.err.println("Transfer Failed: " + e.getMessage());
        }
    }

    private void handleStatement() {
        System.out.println("\n--- [5] ACCOUNT STATEMENT ---");
        System.out.print("Enter Account Number: ");
        String accNum = scanner.nextLine().trim().toUpperCase();

        try {
            Account acc = service.getAccount(accNum);
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println("Account Number:     " + acc.getAccountNumber());
            System.out.println("Owner Name:         " + acc.getOwnerName());
            System.out.println("Account Type:       " + acc.getClass().getSimpleName());
            System.out.printf("Current Balance:    ₹%,d\n", acc.getBalance());
            System.out.printf("Base Interest Rate: %.2f%%\n", acc.interestRate());
            System.out.printf("Yearly Interest:    ₹%,.2f\n", acc.yearlyInterest());
            System.out.println("Active Status:      " + (acc.isActive() ? "ACTIVE" : "INACTIVE"));
            System.out.println("--------------------------------------------------------------------------------");
        } catch (BankException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void handleListAccounts() {
        System.out.println("\n--- [6] ALL ACCOUNTS (SORTED BY BALANCE DESCENDING) ---");
        List<Account> accounts = service.listAccountsSortedByBalance();
        if (accounts.isEmpty()) {
            System.out.println("No accounts registered in the database.");
            return;
        }
        for (Account a : accounts) {
            System.out.println(a);
        }
        System.out.printf("Total Accounts: %d\n", accounts.size());
    }

    private void handleBatchProcess() {
        System.out.println("\n--- [7] CONCURRENT BATCH SIMULATION (THREAD POOL) ---");
        List<Account> accounts = service.listAllAccounts();
        if (accounts.isEmpty()) {
            System.out.println("Please create at least one account first.");
            return;
        }

        String target = accounts.get(0).getAccountNumber();
        System.out.println("Running multithreaded stress test on account: " + target);
        int workers = 5;
        int txPerWorker = 20;
        long amountPerTx = 50;
        try {
            service.runBatchSimulation(target, workers, txPerWorker, amountPerTx);
        } catch (Exception e) {
            System.err.println("Batch execution failed: " + e.getMessage());
        }
    }

    private void handleSummaryReport() {
        System.out.println("\n--- [8] GENERATE NIO AUDIT REPORT ---");
        try {
            String report = service.generateReport(LOGS_DIR, REPORT_FILE);
            System.out.println(report);
            System.out.println("Saved complete report to: " + REPORT_FILE.toAbsolutePath());
        } catch (Exception e) {
            System.err.println("Report generation error: " + e.getMessage());
        }
    }

    private void handleDemonstrations() {
        System.out.println("\n--- [9] MILESTONE CONCEPT DEMONSTRATIONS ---");

        // 1. Reflection Validator Demo
        System.out.println("\n[A] Reflection & Custom Annotations (@Positive, @MaxLength):");
        Customer invalidCustomer = new Customer("Very Long Name That Exceeds The Maximum Length Restriction Configured In Annotation", "test@charusat.edu.in", "123");
        String[] customerErrors = AnnotationValidator.validate(invalidCustomer);
        System.out.println("Validating Customer entity with overly long name:");
        for (String err : customerErrors) {
            System.out.println("  * Validation Warning: " + err);
        }

        // 2. Functional Interface & Lambda Demo
        System.out.println("\n[B] Functional Interface (@FunctionalInterface WithdrawRule):");
        WithdrawRule strictLimit = (acc, amt) -> amt <= 25000;
        Account demoAcc = new SavingsAccount("Demo User", 50000);
        System.out.println("Withdraw ₹10,000 under ₹25,000 rule: " + strictLimit.allow(demoAcc, 10000));
        System.out.println("Withdraw ₹30,000 under ₹25,000 rule: " + strictLimit.allow(demoAcc, 30000));

        // 3. Generic Bounded Method Demo
        System.out.println("\n[C] Parametric Polymorphism & Bounded Generics (<N extends Number>):");
        Long[] balances = {5000L, 12000L, 25000L, 8000L};
        double avg = Repository.average(balances);
        System.out.printf("Average Balance from Long[] array: ₹%,.2f\n", avg);
    }

    private void handleExit() {
        System.out.println("\nSaving application state to disk: " + STATE_FILE.toAbsolutePath());
        try {
            service.persistState(STATE_FILE);
            System.out.println("Database successfully serialized and saved.");
        } catch (Exception e) {
            System.err.println("Warning: Failed to persist database state: " + e.getMessage());
        }
        System.out.println("\nThank you for using MiniBank Banking System. Goodbye!");
    }

    private void seedDemoData() {
        try {
            service.openSavingsAccount("Ruchir Agrawal", 25000, 1000);
            service.openCurrentAccount("Acme Tech Corp", 75000, 20000);
            service.openFixedDeposit("Senior Reserve", 100000, 24);
        } catch (Exception ignored) {
        }
    }
}
