package br.com.leetcode.support.memory;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Immutable assessment record holding empirical memory allocation metrics and formatting reports.
 */
public record MemoryAssessment(
        String testName,
        List<Integer> sizes,
        List<Long> allocatedBytes,
        MemoryAllocation classifiedAllocation,
        boolean passed,
        String failureReason
) {

    public MemoryAssessment {
        sizes = sizes != null ? List.copyOf(sizes) : Collections.emptyList();
        allocatedBytes = allocatedBytes != null ? List.copyOf(allocatedBytes) : Collections.emptyList();
    }

    public static String formatBytes(long bytes) {
        if (bytes == 0) {
            return "0 bytes (0.00 B)";
        }
        if (bytes < 1024) {
            return String.format(Locale.ROOT, "%,d bytes", bytes);
        }
        if (bytes < 1024 * 1024) {
            return String.format(Locale.ROOT, "%,d bytes (%.2f KB)", bytes, bytes / 1024.0);
        }
        if (bytes < 1024 * 1024 * 1024) {
            return String.format(Locale.ROOT, "%,d bytes (%.2f MB)", bytes, bytes / (1024.0 * 1024.0));
        }
        return String.format(Locale.ROOT, "%,d bytes (%.2f GB)", bytes, bytes / (1024.0 * 1024.0 * 1024.0));
    }

    public String formatReport() {
        StringBuilder sb = new StringBuilder();
        sb.append(System.lineSeparator());
        sb.append("========================================================================").append(System.lineSeparator());
        sb.append("                  EMPIRICAL MEMORY ASSESSMENT REPORT                    ").append(System.lineSeparator());
        sb.append("========================================================================").append(System.lineSeparator());
        sb.append(String.format(Locale.ROOT, "Test Name: %s%n", testName));
        sb.append("------------------------------------------------------------------------").append(System.lineSeparator());

        if (sizes.isEmpty()) {
            long bytes = allocatedBytes.isEmpty() ? 0L : allocatedBytes.get(0);
            sb.append(String.format(Locale.ROOT, "%-25s | %-42s%n", "Metric", "Value"));
            sb.append("------------------------------------------------------------------------").append(System.lineSeparator());
            sb.append(String.format(Locale.ROOT, "%-25s | %-42s%n", "Heap Allocated", formatBytes(bytes)));
            sb.append(String.format(Locale.ROOT, "%-25s | %-42s%n", "Classified Allocation", classifiedAllocation.description()));
            sb.append(String.format(Locale.ROOT, "%-25s | %-42s%n", "Status", passed ? "PASSED (OK)" : "FAILED (" + failureReason + ")"));
        } else {
            sb.append(String.format(Locale.ROOT, "%-12s | %-25s | %-15s%n", "Size (N)", "Heap Allocated", "Growth Ratio"));
            sb.append("------------------------------------------------------------------------").append(System.lineSeparator());
            for (int i = 0; i < sizes.size(); i++) {
                int size = sizes.get(i);
                long bytes = i < allocatedBytes.size() ? allocatedBytes.get(i) : 0L;
                String ratioStr;
                if (i == 0) {
                    ratioStr = "baseline";
                } else {
                    long prev = Math.max(1L, allocatedBytes.get(i - 1));
                    double ratio = (double) bytes / prev;
                    ratioStr = String.format(Locale.ROOT, "%.2fx", ratio);
                }
                sb.append(String.format(Locale.ROOT, "%-12d | %-25s | %-15s%n", size, formatBytes(bytes), ratioStr));
            }
            sb.append("------------------------------------------------------------------------").append(System.lineSeparator());
            sb.append(String.format(Locale.ROOT, "Classified Space: %s%n", classifiedAllocation.description()));
            sb.append(String.format(Locale.ROOT, "Status:           %s%n", passed ? "PASSED (OK)" : "FAILED (" + failureReason + ")"));
        }

        sb.append("========================================================================").append(System.lineSeparator());
        return sb.toString();
    }
}
