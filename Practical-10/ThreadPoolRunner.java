import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadPoolRunner {
    public static void main(String[] args) {
        int poolSize = 3;
        int totalTasks = 10;

        System.out.println("Creating Fixed Thread Pool of size " + poolSize + "...");
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);

        System.out.println("Submitting " + totalTasks + " Runnable tasks to the pool:\n");

        for (int i = 1; i <= totalTasks; i++) {
            final int taskId = i;
            executor.submit(() -> {
                String threadName = Thread.currentThread().getName();
                System.out.println("[START] Task #" + taskId + " executing on " + threadName);
                try {
                    Thread.sleep(60); // brief sleep
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.println("   [DONE] Task #" + taskId + " finished on " + threadName);
            });
        }

        // Initiating orderly shutdown
        executor.shutdown();
        System.out.println("\nAll tasks submitted. Awaiting pool termination...");

        try {
            if (executor.awaitTermination(5, TimeUnit.SECONDS)) {
                System.out.println("\nAll tasks completed successfully.");
                System.out.println("Observation: The 10 tasks were distributed and executed across only " 
                    + poolSize + " reused worker threads.");
            } else {
                System.out.println("Tasks timed out.");
            }
        } catch (InterruptedException e) {
            System.out.println("Execution interrupted.");
        }
    }
}
