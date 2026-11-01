class Counter {
    private int count = 0;

    public void incrementUnsync() {
        count++; // Non-atomic read-modify-write causes race condition
    }

    public synchronized void incrementSync() {
        count++; // Thread-safe atomic execution
    }

    public int getCount() {
        return count;
    }

    public void reset() {
        count = 0;
    }
}

public class CounterRace {
    private static final int NUM_THREADS = 4;
    private static final int INCREMENTS_PER_THREAD = 25000;
    private static final int EXPECTED_TOTAL = NUM_THREADS * INCREMENTS_PER_THREAD;

    public static void main(String[] args) throws InterruptedException {
        Counter counter = new Counter();

        // 1. Run WITHOUT Synchronization
        System.out.println("=== 1. Running WITHOUT Synchronization ===");
        Thread[] unsyncThreads = new Thread[NUM_THREADS];
        for (int i = 0; i < NUM_THREADS; i++) {
            unsyncThreads[i] = new Thread(() -> {
                for (int j = 0; j < INCREMENTS_PER_THREAD; j++) {
                    counter.incrementUnsync();
                }
            });
            unsyncThreads[i].start();
        }

        for (Thread t : unsyncThreads) {
            t.join();
        }

        System.out.println("Expected count: " + EXPECTED_TOTAL);
        System.out.println("Actual count:   " + counter.getCount());
        if (counter.getCount() < EXPECTED_TOTAL) {
            System.out.println("-> RACE CONDITION DETECTED: Total came out too low due to lost updates!");
        }

        // 2. Run WITH Synchronization
        System.out.println("\n=== 2. Running WITH Synchronization ===");
        counter.reset();

        Thread[] syncThreads = new Thread[NUM_THREADS];
        for (int i = 0; i < NUM_THREADS; i++) {
            syncThreads[i] = new Thread(() -> {
                for (int j = 0; j < INCREMENTS_PER_THREAD; j++) {
                    counter.incrementSync();
                }
            });
            syncThreads[i].start();
        }

        for (Thread t : syncThreads) {
            t.join();
        }

        System.out.println("Expected count: " + EXPECTED_TOTAL);
        System.out.println("Actual count:   " + counter.getCount());
        if (counter.getCount() == EXPECTED_TOTAL) {
            System.out.println("-> SUCCESS: Synchronized increment is exactly right every run!");
        }
    }
}
