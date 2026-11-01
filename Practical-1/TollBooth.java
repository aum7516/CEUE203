import java.util.Scanner;

record Vehicle(String number, String type) {}

public class TollBooth {
    public static void main(String[] args) {
        int totalToll = 0;
        int bike = 0;
        int car = 0;
        int truck = 0;
        Scanner scanner = new Scanner(System.in);

        while (true) {
            String number = scanner.next();
            if (number.equalsIgnoreCase("done")) {
                break;
            }
            String type = scanner.next();
            Vehicle vehicle = new Vehicle(number, type);

            int toll = switch (vehicle.type().toLowerCase()) {
                case "bike" -> {
                    bike++;
                    yield 20;
                }
                case "car" -> {
                    car++;
                    yield 50;
                }
                case "truck" -> {
                    truck++;
                    yield 150;
                }
                default -> 0;
            };

            totalToll += toll;
        }

        System.out.println("Total toll: " + totalToll);

        String mostFrequent = "none";
        if (bike >= car && bike >= truck) {
            mostFrequent = "bike";
        } else if (car >= bike && car >= truck) {
            mostFrequent = "car";
        } else {
            mostFrequent = "truck";
        }

        System.out.println("Most frequent: " + mostFrequent);
        scanner.close();
    }
}
