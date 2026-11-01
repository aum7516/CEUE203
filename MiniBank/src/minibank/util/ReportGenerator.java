package minibank.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Modern Java NIO.2 file walker and report synthesizer.
 * Inspects transaction logs, aggregates deposit/withdrawal cashflows, and writes an audit summary.
 */
public class ReportGenerator {

    public static String generateDailyReport(Path logsDir, Path reportOutput) throws IOException {
        if (!Files.exists(logsDir)) {
            Files.createDirectories(logsDir);
        }

        long totalDeposits = 0;
        long totalWithdrawals = 0;
        long totalTransfers = 0;
        int transactionCount = 0;
        List<String> logFileSummary = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(logsDir, "*.log")) {
            for (Path logFile : stream) {
                BasicFileAttributes attrs = Files.readAttributes(logFile, BasicFileAttributes.class);
                logFileSummary.add(String.format("Log File: %-20s | Size: %6d bytes | Last Modified: %s",
                        logFile.getFileName(), attrs.size(), attrs.lastModifiedTime()));

                try (BufferedReader reader = Files.newBufferedReader(logFile, StandardCharsets.UTF_8)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        transactionCount++;
                        if (line.contains("DEPOSIT")) {
                            totalDeposits += extractAmount(line);
                        } else if (line.contains("WITHDRAW")) {
                            totalWithdrawals += extractAmount(line);
                        } else if (line.contains("TRANSFER")) {
                            totalTransfers += extractAmount(line);
                        }
                    }
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("======================================================================\n");
        sb.append("                   MINIBANK END-OF-DAY AUDIT REPORT                  \n");
        sb.append("                 Generated: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        sb.append("======================================================================\n\n");
        sb.append("--- LOG FILE INVENTORY ---\n");
        if (logFileSummary.isEmpty()) {
            sb.append("No active transaction log files found in directory.\n");
        } else {
            for (String fileInfo : logFileSummary) {
                sb.append("  * ").append(fileInfo).append("\n");
            }
        }
        sb.append("\n--- AGGREGATE TRANSACTION TOTALS ---\n");
        sb.append(String.format("  * Total Transactions Recorded: %d\n", transactionCount));
        sb.append(String.format("  * Total Inflow (Deposits):     ₹%,d\n", totalDeposits));
        sb.append(String.format("  * Total Outflow (Withdrawals): ₹%,d\n", totalWithdrawals));
        sb.append(String.format("  * Total Internal Transfers:    ₹%,d\n", totalTransfers));
        sb.append(String.format("  * Net Daily Cash Flow:         ₹%,d\n", (totalDeposits - totalWithdrawals)));
        sb.append("======================================================================\n");

        String reportContent = sb.toString();
        if (reportOutput.getParent() != null) {
            Files.createDirectories(reportOutput.getParent());
        }
        Files.writeString(reportOutput, reportContent, StandardCharsets.UTF_8);
        return reportContent;
    }

    private static long extractAmount(String line) {
        try {
            int amountIdx = line.indexOf("₹");
            if (amountIdx != -1) {
                int endIdx = line.indexOf(" ", amountIdx);
                if (endIdx == -1) endIdx = line.length();
                String raw = line.substring(amountIdx + 1, endIdx).replace(",", "").trim();
                return Long.parseLong(raw);
            }
        } catch (Exception ignored) {
        }
        return 0;
    }
}
