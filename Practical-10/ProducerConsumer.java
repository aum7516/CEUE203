import java.util.LinkedList;
import java.util.Queue;

class SharedBuffer {
    private final Queue<Integer> queue = new LinkedList<>();
    private final int capacity;

    public SharedBuffer(int capacity) {
        this.capacity = capacity;
    }

    // Producer calls this to add an item
    public synchronized void produce(int item) throws InterruptedException {
        while (queue.size() == capacity) {
            System.out.println("   [Buffer FULL] Producer waiting...");
            wait(); // Wait until consumer frees up space
        }

        queue.add(item);
        System.out.println("[PRODUCED] Item: " + item + " (Buffer size: " + queue.size() + ")");
        notify(); // Notify waiting consumer
    }

    // Consumer calls this to remove an item
    public synchronized int consume() throws InterruptedException {
        while (queue.isEmpty()) {
            System.out.println("   [Buffer EMPTY] Consumer waiting...");
            wait(); // Wait until producer adds an item
        }

        int item = queue.poll();
        System.out.println("   [CONSUMED] Item: " + item + " (Buffer size: " + queue.size() + ")");
        notify(); // Notify waiting producer
        return item;
    }
}

public class ProducerConsumer {
    private static final int TOTAL_ITEMS = 8;

    public static void main(String[] args) throws InterruptedException {
        SharedBuffer buffer = new SharedBuffer(2); // Small shared buffer of capacity 2

        System.out.println("Starting Producer-Consumer system (Buffer capacity: 2, Total items: " 
            + TOTAL_ITEMS + ")...\n");

        Thread producerThread = new Thread(() -> {
            try {
                for (int i = 1; i <= TOTAL_ITEMS; i++) {
                    buffer.produce(i);
                    Thread.sleep(30); // simulate production work
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Producer");

        Thread consumerThread = new Thread(() -> {
            try {
                for (int i = 1; i <= TOTAL_ITEMS; i++) {
                    buffer.consume();
                    Thread.sleep(60); // simulate consumption work
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Consumer");

        producerThread.start();
        consumerThread.start();

        producerThread.join();
        consumerThread.join();

        System.out.println("\nAll " + TOTAL_ITEMS + " items successfully produced and consumed in order with none lost.");
    }
}
