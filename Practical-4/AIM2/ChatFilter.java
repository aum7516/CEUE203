public class ChatFilter {
    public static String filter(String[] logs, String keyword) {
        StringBuilder report = new StringBuilder();
        int count = 0;

        for (String line : logs) {
            String[] parts = line.split(" ", 3);
            // Skip malformed lines
            if (parts.length < 3) {
                continue;
            }

            String time = parts[0];
            String user = parts[1];
            String message = parts[2];

            if (message.toLowerCase().contains(keyword.toLowerCase())) {
                count++;
                report.append(time).append(" ").append(user).append(": ").append(message).append("\n");
            }
        }

        System.out.println("Matches: " + count);
        System.out.println(report);
        return report.toString();
    }
}
