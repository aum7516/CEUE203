import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;

@Retention(RetentionPolicy.RUNTIME)
@interface Run {}

class MyTests {
    @Run
    public void testOne() {
        System.out.println("Running testOne");
    }

    public void notATest() {
        System.out.println("This should not run");
    }

    @Run
    public void testTwo() {
        System.out.println("Running testTwo");
    }
}

public class MiniTestRunner {
    public static void main(String[] args) throws Exception {
        MyTests tests = new MyTests();
        int count = 0;

        Method[] methods = tests.getClass().getDeclaredMethods();
        Arrays.sort(methods, Comparator.comparing(Method::getName));

        for (Method m : methods) {
            if (m.isAnnotationPresent(Run.class)) {
                m.invoke(tests);
                count++;
            }
        }

        System.out.println("Total @Run methods executed: " + count);
    }
}
