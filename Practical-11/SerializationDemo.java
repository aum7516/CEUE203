import java.io.*;

class Student implements Serializable {
    String name;
    int roll;
    transient String secretPin; // transient: not saved

    public Student(String name, int roll, String secretPin) {
        this.name = name;
        this.roll = roll;
        this.secretPin = secretPin;
    }

    @Override
    public String toString() {
        return "Student[name=" + name + ", roll=" + roll + ", secretPin=" + secretPin + "]";
    }
}

public class SerializationDemo {
    public static void main(String[] args) throws Exception {
        Student[] students = {
            new Student("Alice", 101, "1234"),
            new Student("Bob", 102, "5678")
        };

        // 1. Save array of objects
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("students.ser"));
        out.writeObject(students);
        out.close();
        System.out.println("Saved objects to file.");

        // 2. Load objects back into fresh objects
        ObjectInputStream in = new ObjectInputStream(new FileInputStream("students.ser"));
        Student[] loaded = (Student[]) in.readObject();
        in.close();

        System.out.println("Loaded objects from file:");
        for (Student s : loaded) {
            System.out.println(s);
        }
    }
}
