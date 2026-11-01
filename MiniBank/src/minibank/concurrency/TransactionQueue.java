package minibank.concurrency;

/**
 * Thread-safe bounded buffer implementing classic Producer-Consumer coordination with wait() and notifyAll().
 */
public class TransactionQueue {
    private final String[] buffer;
    private int count = 0;
    private int putIndex = 0;
    private int takeIndex = 0;

    public TransactionQueue(int capacity) {
        this.buffer = new String[capacity];
    }

    public synchronized void produce(String transaction) throws InterruptedException {
        while (count == buffer.length) {
            wait();
        }
        buffer[putIndex] = transaction;
        putIndex = (putIndex + 1) % buffer.length;
        count++;
        notifyAll();
    }

    public synchronized String consume() throws InterruptedException {
        while (count == 0) {
            wait();
        }
        String transaction = buffer[takeIndex];
        takeIndex = (takeIndex + 1) % buffer.length;
        count--;
        notifyAll();
        return transaction;
    }

    public synchronized int size() {
        return count;
    }
}
