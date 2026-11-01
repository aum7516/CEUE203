package minibank.model;

/**
 * Enumeration representing all user operations available in the MiniBank console shell.
 */
public enum MenuOption {
    OPEN_ACCOUNT(1, "Open New Account"),
    DEPOSIT(2, "Deposit Money"),
    WITHDRAW(3, "Withdraw Money"),
    TRANSFER(4, "Fund Transfer (Deadlock-Free)"),
    STATEMENT(5, "Account Statement & Details"),
    LIST_ACCOUNTS(6, "List All Accounts (Sorted by Balance)"),
    BATCH_PROCESS(7, "Execute Concurrent Batch Transactions"),
    SUMMARY_REPORT(8, "Generate NIO Audit Summary Report"),
    DEMO_FEATURES(9, "Run Milestone Demonstrations (Reflection & Generics)"),
    EXIT(10, "Save & Exit");

    private final int optionNumber;
    private final String description;

    MenuOption(int optionNumber, String description) {
        this.optionNumber = optionNumber;
        this.description = description;
    }

    public int getOptionNumber() {
        return optionNumber;
    }

    public String getDescription() {
        return description;
    }

    public static MenuOption fromChoice(int choice) {
        for (MenuOption opt : values()) {
            if (opt.optionNumber == choice) {
                return opt;
            }
        }
        return null;
    }
}
