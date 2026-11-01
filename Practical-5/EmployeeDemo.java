abstract class Employee {
    private int id;
    private String name;

    public Employee(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }
    public String getName() { return name; }

    public abstract double calculateSalary();

    public void printDetails() {
        System.out.printf("Employee [ID: %d, Name: %-12s, Salary: $%.2f]\n", id, name, calculateSalary());
    }
}

class FullTimeEmployee extends Employee {
    private double monthlySalary;

    public FullTimeEmployee(int id, String name, double monthlySalary) {
        super(id, name);
        this.monthlySalary = monthlySalary;
    }

    @Override
    public double calculateSalary() {
        return monthlySalary;
    }
}

class PartTimeEmployee extends Employee {
    private int hoursWorked;
    private double hourlyRate;

    public PartTimeEmployee(int id, String name, int hoursWorked, double hourlyRate) {
        super(id, name);
        this.hoursWorked = hoursWorked;
        this.hourlyRate = hourlyRate;
    }

    @Override
    public double calculateSalary() {
        return hoursWorked * hourlyRate;
    }
}

public class EmployeeDemo {
    public static void main(String[] args) {
        Employee[] employees = {
            new FullTimeEmployee(101, "Alice Smith", 5500.0),
            new PartTimeEmployee(102, "Bob Jones", 40, 35.0),
            new FullTimeEmployee(103, "Charlie Day", 6200.0),
            new PartTimeEmployee(104, "Diana Prince", 25, 45.0)
        };

        System.out.println("=== Employee Payroll System (Inheritance & Super) ===");
        double totalPayroll = 0.0;
        for (Employee emp : employees) {
            emp.printDetails();
            totalPayroll += emp.calculateSalary();
        }
        System.out.printf("Total Monthly Payroll: $%.2f\n", totalPayroll);
    }
}
