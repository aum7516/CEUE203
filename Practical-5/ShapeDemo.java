abstract class Shape {
    private String color;

    public Shape(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public abstract double area();

    public void display() {
        System.out.printf("%s (Color: %s, Area: %.2f)\n", getClass().getSimpleName(), color, area());
    }
}

class Circle extends Shape {
    private double radius;

    public Circle(String color, double radius) {
        super(color);
        this.radius = radius;
    }

    @Override
    public double area() {
        return Math.PI * radius * radius;
    }
}

class Rectangle extends Shape {
    private double width;
    private double height;

    public Rectangle(String color, double width, double height) {
        super(color);
        this.width = width;
        this.height = height;
    }

    @Override
    public double area() {
        return width * height;
    }
}

public class ShapeDemo {
    public static void main(String[] args) {
        Shape[] shapes = {
            new Circle("Red", 5.0),
            new Rectangle("Blue", 4.0, 6.0),
            new Circle("Green", 2.5)
        };

        System.out.println("=== Geometric Shapes (Polymorphism & Abstraction) ===");
        double totalArea = 0.0;
        for (Shape s : shapes) {
            s.display(); // Dynamic method dispatch
            totalArea += s.area();
        }
        System.out.printf("Total Area: %.2f\n", totalArea);
    }
}
