class Box<T> {
    private T item;
    public void set(T item) { this.item = item; }
    public T get() { return item; }
}

class Pair<A, B> {
    private A first;
    private B second;

    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }
    public A getFirst() { return first; }
    public B getSecond() { return second; }
}

public class GenericContainers {
    // Generic method returning the first element of any array
    public static <T> T getFirst(T[] array) {
        if (array != null && array.length > 0) {
            return array[0];
        }
        return null;
    }

    public static void main(String[] args) {
        // 1. Box with different types (no casting needed)
        Box<String> strBox = new Box<>();
        strBox.set("Hello Java");
        String s = strBox.get();

        Box<Integer> intBox = new Box<>();
        intBox.set(100);
        int n = intBox.get();

        System.out.println("Box contents: " + s + ", " + n);

        // 2. Pair with two different types
        Pair<String, Integer> pair = new Pair<>("RollNo", 101);
        System.out.println("Pair: " + pair.getFirst() + " -> " + pair.getSecond());

        // 3. Generic method getFirst
        String[] words = { "Apple", "Banana", "Cherry" };
        Integer[] numbers = { 10, 20, 30 };

        System.out.println("First word: " + getFirst(words));
        System.out.println("First number: " + getFirst(numbers));
    }
}
