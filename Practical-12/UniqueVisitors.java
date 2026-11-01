import java.util.*;

public class UniqueVisitors {
    public static void main(String[] args) {
        List<String> logins = List.of("alice", "bob", "alice", "charlie", "bob", "david", "alice");

        Set<String> uniqueSet = new HashSet<>(logins);
        System.out.println("Total unique visitors: " + uniqueSet.size());

        Set<String> orderedSet = new LinkedHashSet<>(logins);
        System.out.println("First three distinct visitors in arrival order:");
        int count = 0;
        for (String name : orderedSet) {
            if (count < 3) {
                System.out.println((count + 1) + ". " + name);
                count++;
            }
        }
    }
}
