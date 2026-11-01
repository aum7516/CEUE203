public class CinemaShow {
    private String title;
    private int seatsAvailable;
    private final int capacity;
    private static int totalBooked = 0;

    public CinemaShow(String title, int capacity) {
        this.title = title;
        this.capacity = capacity;
        this.seatsAvailable = capacity;
    }

    public CinemaShow(String title) {
        this(title, 100);
    }

    public boolean book(int n) {
        if (n <= seatsAvailable) {
            seatsAvailable -= n;
            totalBooked += n;
            return true;
        } else {
            return false;
        }
    }

    public void cancel(int n) {
        seatsAvailable = Math.min(capacity, seatsAvailable + n);
    }

    public int getSeatsAvailable() {
        return seatsAvailable;
    }

    public static int getTotalBooked() {
        return totalBooked;
    }

    public static void main(String[] args) {
        CinemaShow show = new CinemaShow("Avatar", 50);

        System.out.println("Book 30: " + show.book(30) + ", Seats: " + show.getSeatsAvailable());
        System.out.println("Book 25: " + show.book(25) + ", Seats: " + show.getSeatsAvailable());
        System.out.println("Book 15: " + show.book(15) + ", Seats: " + show.getSeatsAvailable());
        show.cancel(10);
        System.out.println("Cancel 10, Seats: " + show.getSeatsAvailable());
        show.cancel(50);
        System.out.println("Cancel 50, Seats: " + show.getSeatsAvailable());
        System.out.println("Total booked: " + getTotalBooked());
    }
}
