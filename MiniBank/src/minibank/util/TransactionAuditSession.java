package minibank.util;

/**
 * AutoCloseable audit session demonstrating try-with-resources lifecycle management.
 */
public class TransactionAuditSession implements AutoCloseable {
    private final String operationName;
    private final long startTime;

    public TransactionAuditSession(String operationName) {
        this.operationName = operationName;
        this.startTime = System.currentTimeMillis();
        System.out.println("[AUDIT SESSION OPENED] Starting: " + operationName);
    }

    @Override
    public void close() {
        long duration = System.currentTimeMillis() - startTime;
        System.out.println("[AUDIT SESSION CLOSED] Finished: " + operationName + " (" + duration + " ms)");
    }
}
