package br.com.leetcode.support.benchmark;

import java.util.Locale;

/**
 * Immutable metrics record containing calculated execution elapsed time, CPU time, and allocated memory.
 *
 * @param elapsedTimeNanos Total wall-clock time elapsed in nanoseconds
 * @param cpuTimeNanos     Thread CPU time in nanoseconds, or -1 if unsupported
 * @param allocatedBytes   Thread heap allocated memory delta in bytes, or -1 if unsupported
 */
public record BenchmarkMetrics(
        long elapsedTimeNanos,
        long cpuTimeNanos,
        long allocatedBytes
) {

    public double elapsedTimeMillis() {
        return elapsedTimeNanos / 1_000_000.0;
    }

    public double elapsedTimeSeconds() {
        return elapsedTimeNanos / 1_000_000_000.0;
    }

    public double cpuTimeMillis() {
        return cpuTimeNanos >= 0 ? cpuTimeNanos / 1_000_000.0 : -1.0;
    }

    public double cpuTimeSeconds() {
        return cpuTimeNanos >= 0 ? cpuTimeNanos / 1_000_000_000.0 : -1.0;
    }

    public String formatElapsedTime() {
        if (elapsedTimeNanos < 1_000) {
            return String.format(Locale.ROOT, "%d ns", elapsedTimeNanos);
        } else if (elapsedTimeNanos < 1_000_000) {
            return String.format(Locale.ROOT, "%.2f us (%d ns)", elapsedTimeNanos / 1_000.0, elapsedTimeNanos);
        } else if (elapsedTimeNanos < 1_000_000_000) {
            return String.format(Locale.ROOT, "%.3f ms (%d ns)", elapsedTimeMillis(), elapsedTimeNanos);
        } else {
            return String.format(Locale.ROOT, "%.2f s (%.3f ms)", elapsedTimeSeconds(), elapsedTimeMillis());
        }
    }

    public String formatShortElapsedTime() {
        if (elapsedTimeNanos < 1_000) {
            return String.format(Locale.ROOT, "%d ns", elapsedTimeNanos);
        } else if (elapsedTimeNanos < 1_000_000) {
            return String.format(Locale.ROOT, "%.2f us", elapsedTimeNanos / 1_000.0);
        } else if (elapsedTimeNanos < 1_000_000_000) {
            return String.format(Locale.ROOT, "%.3f ms", elapsedTimeMillis());
        } else {
            return String.format(Locale.ROOT, "%.2f s", elapsedTimeSeconds());
        }
    }

    public String formatCpuTime() {
        if (cpuTimeNanos < 0) {
            return "N/A (CPU time measurement not supported or enabled)";
        }
        if (cpuTimeNanos < 1_000) {
            return String.format(Locale.ROOT, "%d ns", cpuTimeNanos);
        } else if (cpuTimeNanos < 1_000_000) {
            return String.format(Locale.ROOT, "%.2f us (%d ns)", cpuTimeNanos / 1_000.0, cpuTimeNanos);
        } else if (cpuTimeNanos < 1_000_000_000) {
            return String.format(Locale.ROOT, "%.3f ms (%d ns)", cpuTimeMillis(), cpuTimeNanos);
        } else {
            return String.format(Locale.ROOT, "%.2f s (%.3f ms)", cpuTimeSeconds(), cpuTimeMillis());
        }
    }

    public String formatShortCpuTime() {
        if (cpuTimeNanos < 0) {
            return "N/A";
        }
        if (cpuTimeNanos < 1_000) {
            return String.format(Locale.ROOT, "%d ns", cpuTimeNanos);
        } else if (cpuTimeNanos < 1_000_000) {
            return String.format(Locale.ROOT, "%.2f us", cpuTimeNanos / 1_000.0);
        } else if (cpuTimeNanos < 1_000_000_000) {
            return String.format(Locale.ROOT, "%.3f ms", cpuTimeMillis());
        } else {
            return String.format(Locale.ROOT, "%.2f s", cpuTimeSeconds());
        }
    }

    public String formatAllocatedMemory() {
        if (allocatedBytes < 0) {
            return "N/A (Memory measurement not supported)";
        }
        if (allocatedBytes < 1024) {
            return String.format(Locale.ROOT, "%d B", allocatedBytes);
        } else if (allocatedBytes < 1024 * 1024) {
            return String.format(Locale.ROOT, "%.2f KB (%d bytes)", allocatedBytes / 1024.0, allocatedBytes);
        } else if (allocatedBytes < 1024L * 1024L * 1024L) {
            return String.format(Locale.ROOT, "%.2f MB (%d bytes)", allocatedBytes / (1024.0 * 1024.0), allocatedBytes);
        } else {
            return String.format(Locale.ROOT, "%.2f GB (%d bytes)", allocatedBytes / (1024.0 * 1024.0 * 1024.0), allocatedBytes);
        }
    }

    public String formatShortAllocatedMemory() {
        if (allocatedBytes < 0) {
            return "N/A";
        }
        if (allocatedBytes < 1024) {
            return String.format(Locale.ROOT, "%d B", allocatedBytes);
        } else if (allocatedBytes < 1024 * 1024) {
            return String.format(Locale.ROOT, "%.2f KB", allocatedBytes / 1024.0);
        } else if (allocatedBytes < 1024L * 1024L * 1024L) {
            return String.format(Locale.ROOT, "%.2f MB", allocatedBytes / (1024.0 * 1024.0));
        } else {
            return String.format(Locale.ROOT, "%.2f GB", allocatedBytes / (1024.0 * 1024.0 * 1024.0));
        }
    }
}
