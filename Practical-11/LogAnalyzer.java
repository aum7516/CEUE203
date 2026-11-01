import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class LogAnalyzer {
    public static void main(String[] args) throws Exception {
        // Create sample log files
        Path file1 = Path.of("server.log");
        Path file2 = Path.of("app.log");

        Files.writeString(file1, "INFO Server started\nERROR Database connection failed\nINFO Retrying connection\n");
        Files.writeString(file2, "INFO User login\nERROR Out of memory\nERROR Disk full\n");

        Path[] files = { file1, file2 };
        String keyword = "ERROR";
        int totalLines = 0;
        int keywordCount = 0;

        for (Path p : files) {
            List<String> lines = Files.readAllLines(p);
            totalLines += lines.size();
            long size = Files.size(p);

            int match = 0;
            for (String line : lines) {
                if (line.contains(keyword)) {
                    match++;
                }
            }
            keywordCount += match;
            System.out.println("File: " + p.getFileName() + " (Size: " + size + " bytes, Matches: " + match + ")");
        }

        System.out.println("Total lines across all files: " + totalLines);
        System.out.println("Total occurrences of \"" + keyword + "\": " + keywordCount);
    }
}
