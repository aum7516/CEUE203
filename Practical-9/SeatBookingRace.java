import java.util.concurrent.atomic.AtomicInteger;

class ReservationSystem {
    private int seatsLeft;
    private int successfulBookings = 0;

    public ReservationSystem(int seats) {
        this.seatsLeft = seats;
    }

    // Unsynchronized booking: window of vulnerability between check and decrement
    public boolean bookUnsync(String passenger) {
        if (seatsLeft > 0) {
            try {
                // Sleep to simulate network / database latency and expose race condition
                Thread.sleep(10);
            } catch (InterruptedException ignored) {}

            seatsLeft--;
            successfulBookings++;
            System.out.println("   [UNSYNC SUCCESS] " + passenger + " booked! Remaining: " + seatsLeft);
            return true;
        } else {
            System.out.println("   [UNSYNC FAILED]  " + passenger + " - No seats left.");
            return false;
        }
    }

    // Synchronized booking: atomic check-then-act
    public synchronized boolean bookSync(String passenger) {
        if (seatsLeft > 0) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ignored) {}

            seatsLeft--;
            successfulBookings++;
            System.out.println("   [SYNC SUCCESS]   " + passenger + " booked! Remaining: " + seatsLeft);
            return true;
        } else {
            System.out.println("   [SYNC FAILED]    " + passenger + " - No seats left.");
            return false;
        }
    }

    public int getSuccessfulBookings() {
        return successfulBookings;
    }

    public int getSeatsLeft() {
        return seatsLeft;
    }
}

public class SeatBookingRace {
    public static void main(String[] args) throws InterruptedException {
        // --- 1. Without Synchronization (Oversell Demonstration) ---
        System.out.println("=== 1. Without Synchronization (Capacity = 5, 10 Threads) ===");
        ReservationSystem unsyncSystem = new ReservationSystem(5);
        Thread[] unsyncThreads = new Thread[10];

        for (int i = 0; i < 10; i++) {
            final String passenger = "Passenger-" + (i + 1);
            unsyncThreads[i] = new Thread(() -> unsyncSystem.bookUnsync(passenger));
            unsyncThreads[i].start();
        }

        for (Thread t : unsyncThreads) {
            t.join();
        }

        System.out.println("Result: Total successful bookings = " + unsyncSystem.getSuccessfulBookings());
        if (unsyncSystem.getSuccessfulBookings() > 5) {
            System.out.println("-> RACE CONDITION: Oversold! More than 5 passengers succeeded without synchronization.\n");
        }

        // --- 2. With Synchronization (Exactly 5 Succeed) ---
        System.out.println("=== 2. With Synchronized book() (Capacity = 5, 10 Threads) ===");
        ReservationSystem syncSystem = new ReservationSystem(5);
        Thread[] syncThreads = new Thread[10];

        for (int i = 0; i < 10; i++) {
            final String passenger = "Passenger-" + (i + 1);
            syncThreads[i] = new Thread(() -> syncSystem.bookSync(passenger));
            syncThreads[i].start();
        }

        for (Thread t : syncThreads) {
            t.join();
        }

        System.out.println("Result: Total successful bookings = " + syncSystem.getSuccessfulBookings());
        if (syncSystem.getSuccessfulBookings() == 5) {
            System.out.println("-> SUCCESS: Exactly 5 passengers booked. Zero overselling occurs!");
        }
    }
}
