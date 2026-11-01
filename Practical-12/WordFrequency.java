import java.util.*;

public class WordFrequency {
    public static void main(String[] args) {
        String text = "java is fun and java is powerful and java is simple";
        String[] words = text.split(" ");

        Map<String, Integer> map = new HashMap<>();
        for (String w : words) {
            map.put(w, map.getOrDefault(w, 0) + 1);
        }

        List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
        // Sort highest frequency first; break ties alphabetically
        list.sort((e1, e2) -> {
            int cmp = e2.getValue().compareTo(e1.getValue());
            return (cmp != 0) ? cmp : e1.getKey().compareTo(e2.getKey());
        });

        System.out.println("Word frequencies (highest first):");
        for (Map.Entry<String, Integer> entry : list) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}
