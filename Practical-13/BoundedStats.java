public class BoundedStats {
    // Bounded generic method: only accepts Number and its subclasses
    public static <N extends Number> double average(N[] values) {
        double sum = 0.0;
        for (N v : values) {
            sum += v.doubleValue();
        }
        return sum / values.length;
    }

    public static void main(String[] args) {
        Integer[] intArr = { 10, 20, 30, 40 };
        Double[] doubleArr = { 2.5, 3.5, 4.0 };
        Long[] longArr = { 100L, 200L, 300L };

        System.out.println("Integer average: " + average(intArr));
        System.out.println("Double average:  " + average(doubleArr));
        System.out.println("Long average:    " + average(longArr));

        // Note: Passing String[] fails at compile-time:
        // average(new String[]{"a", "b"});
        // Error: inferred type String does not conform to upper bound Number
    }
}
