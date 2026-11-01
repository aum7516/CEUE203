import java.util.Scanner;

class DivideByZeroException extends Exception {
    public DivideByZeroException(String message) {
        super(message);
    }
}

public class GuardedCalculator {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean success = false;
        int attempt = 0;

        System.out.println("=== Guarded Calculator ===");

        while (!success) {
            attempt++;
            System.out.println("Attempt #" + attempt + ":");
            try {
                System.out.print("Enter first number: ");
                String input1 = scanner.next();
                double num1 = Double.parseDouble(input1); // May throw NumberFormatException

                System.out.print("Enter operator (+, -, *, /): ");
                char op = scanner.next().charAt(0);

                System.out.print("Enter second number: ");
                String input2 = scanner.next();
                double num2 = Double.parseDouble(input2); // May throw NumberFormatException

                if (op == '/' && num2 == 0) {
                    throw new DivideByZeroException("Division by zero is not permitted.");
                }

                double result = switch (op) {
                    case '+' -> num1 + num2;
                    case '-' -> num1 - num2;
                    case '*' -> num1 * num2;
                    case '/' -> num1 / num2;
                    default -> throw new IllegalArgumentException("Unknown operator '" + op + "'");
                };

                System.out.println("Result: " + num1 + " " + op + " " + num2 + " = " + result);
                success = true;

            } catch (NumberFormatException e) {
                System.out.println("Error [Invalid Number]: Please enter valid numeric values.");
            } catch (DivideByZeroException e) {
                System.out.println("Error [Zero Division]: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("Error [Operator]: " + e.getMessage());
            } finally {
                System.out.println("[finally] Logging: Attempt #" + attempt + " processed.");
                System.out.println();
            }
        }

        scanner.close();
        System.out.println("Calculation completed successfully.");
    }
}
