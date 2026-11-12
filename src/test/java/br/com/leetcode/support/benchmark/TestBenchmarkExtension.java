package br.com.leetcode.support.benchmark;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * JUnit 5 Extension that captures execution wall time, CPU time, and thread-allocated heap memory.
 * Emits individual test banners and a consolidated, sanitized leaderboard table upon class execution completion.
 */
public class TestBenchmarkExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback, AfterAllCallback {

    private static final ExtensionContext.Namespace TEST_NAMESPACE =
            ExtensionContext.Namespace.create(TestBenchmarkExtension.class);

    private static final String CLASS_ENTRIES_KEY = "BENCHMARK_CLASS_ENTRIES";
    private static final int LEADERBOARD_WIDTH = 112;

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BOLD = "\u001B[1m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_BRIGHT_GREEN = "\u001B[92m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BRIGHT_RED = "\u001B[91m";

    private static final ThreadMXBean THREAD_MX_BEAN = ManagementFactory.getThreadMXBean();
    private static final com.sun.management.ThreadMXBean SUN_THREAD_MX_BEAN = initSunThreadMxBean();

    private static com.sun.management.ThreadMXBean initSunThreadMxBean() {
        if (THREAD_MX_BEAN instanceof com.sun.management.ThreadMXBean sunBean) {
            try {
                if (sunBean.isThreadAllocatedMemorySupported() && !sunBean.isThreadAllocatedMemoryEnabled()) {
                    sunBean.setThreadAllocatedMemoryEnabled(true);
                }
            } catch (SecurityException | UnsupportedOperationException ignored) {
                // Ignore if security manager or JVM restrictions prevent modification
            }
            return sunBean;
        }
        return null;
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        long startWallTime = System.nanoTime();
        long startCpuTime = isCpuTimeAvailable() ? THREAD_MX_BEAN.getCurrentThreadCpuTime() : -1L;
        long startAllocatedBytes = getCurrentThreadAllocatedBytes();

        BenchmarkSnapshot snapshot = new BenchmarkSnapshot(startWallTime, startCpuTime, startAllocatedBytes);
        getTestStore(context).put(context.getUniqueId(), snapshot);
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        long endWallTime = System.nanoTime();
        BenchmarkSnapshot snapshot = getTestStore(context).remove(context.getUniqueId(), BenchmarkSnapshot.class);
        if (snapshot == null) {
            return;
        }

        long elapsedWallNanos = endWallTime - snapshot.startTimeNanos();

        long currentCpuTime = isCpuTimeAvailable() ? THREAD_MX_BEAN.getCurrentThreadCpuTime() : -1L;
        long elapsedCpuNanos = (snapshot.startCpuTimeNanos() >= 0 && currentCpuTime >= 0)
                ? (currentCpuTime - snapshot.startCpuTimeNanos())
                : -1L;

        long currentAllocatedBytes = getCurrentThreadAllocatedBytes();
        long allocatedBytes = (snapshot.startAllocatedBytes() >= 0 && currentAllocatedBytes >= 0)
                ? Math.max(0, currentAllocatedBytes - snapshot.startAllocatedBytes())
                : -1L;

        BenchmarkMetrics metrics = new BenchmarkMetrics(elapsedWallNanos, elapsedCpuNanos, allocatedBytes);

        BenchmarkTest config = findBenchmarkConfig(context);
        boolean timeSlaViolated = config != null
                && config.maxTimeMillis() > 0
                && metrics.elapsedTimeMillis() > config.maxTimeMillis();

        boolean memorySlaViolated = config != null
                && config.maxMemoryBytes() >= 0
                && metrics.allocatedBytes() >= 0
                && metrics.allocatedBytes() > config.maxMemoryBytes();

        String testName = resolveSemanticTestName(context);

        // Record entry for the class-level leaderboard in root store
        getClassEntries(context).add(new BenchmarkEntry(testName, metrics, timeSlaViolated, memorySlaViolated));

        boolean printToConsole = config == null || config.printToConsole();
        if (printToConsole) {
            printReport(testName, metrics, config, timeSlaViolated, memorySlaViolated);
        }

        publishReport(context, testName, metrics, timeSlaViolated, memorySlaViolated);

        if (timeSlaViolated && config.failOnSlaViolation()) {
            throw new AssertionError(String.format(
                    Locale.ROOT,
                    "Execution time of %.2f ms exceeded SLA threshold of %d ms for test '%s'",
                    metrics.elapsedTimeMillis(),
                    config.maxTimeMillis(),
                    testName
            ));
        }

        if (memorySlaViolated && config.failOnMemoryViolation()) {
            throw new AssertionError(String.format(
                    Locale.ROOT,
                    "Allocated memory of %s exceeded SLA threshold of %,d bytes for test '%s'",
                    metrics.formatAllocatedMemory(),
                    config.maxMemoryBytes(),
                    testName
            ));
        }
    }

    @Override
    public void afterAll(ExtensionContext context) {
        ExtensionContext.Namespace classNamespace = ExtensionContext.Namespace.create(
                TestBenchmarkExtension.class,
                context.getRequiredTestClass()
        );
        try {
            BenchmarkTest config = findClassBenchmarkConfig(context);
            boolean summaryEnabled = config == null || config.summaryTable();
            if (!summaryEnabled) {
                return;
            }

            List<BenchmarkEntry> entries = getClassEntries(context);
            if (entries.isEmpty()) {
                return;
            }

            boolean excludeWarmup = config == null || config.excludeWarmup();
            List<BenchmarkEntry> sorted = new ArrayList<>();
            for (BenchmarkEntry entry : entries) {
                if (excludeWarmup && entry.isWarmup()) {
                    continue;
                }
                sorted.add(entry);
            }

            if (sorted.isEmpty()) {
                return;
            }

            sorted.sort(Comparator.comparingLong(e -> e.metrics().elapsedTimeNanos()));

            String className = context.getRequiredTestClass().getSimpleName();
            printLeaderboard(className, sorted, config);
        } finally {
            context.getRoot().getStore(classNamespace).remove(CLASS_ENTRIES_KEY);
        }
    }

    @SuppressWarnings("unchecked")
    private List<BenchmarkEntry> getClassEntries(ExtensionContext context) {
        ExtensionContext.Namespace classNamespace = ExtensionContext.Namespace.create(
                TestBenchmarkExtension.class,
                context.getRequiredTestClass()
        );
        return context.getRoot().getStore(classNamespace).getOrComputeIfAbsent(
                CLASS_ENTRIES_KEY,
                key -> Collections.synchronizedList(new ArrayList<BenchmarkEntry>()),
                List.class
        );
    }

    private BenchmarkTest findBenchmarkConfig(ExtensionContext context) {
        return AnnotationSupport.findAnnotation(context.getElement(), BenchmarkTest.class)
                .or(() -> context.getTestClass().flatMap(c -> AnnotationSupport.findAnnotation(c, BenchmarkTest.class)))
                .orElse(null);
    }

    private BenchmarkTest findClassBenchmarkConfig(ExtensionContext context) {
        return context.getTestClass()
                .flatMap(c -> AnnotationSupport.findAnnotation(c, BenchmarkTest.class))
                .orElse(null);
    }

    private String resolveSemanticTestName(ExtensionContext context) {
        String baseName = context.getRequiredTestClass().getSimpleName() + "." + context.getRequiredTestMethod().getName();
        String displayName = context.getDisplayName();
        String methodName = context.getRequiredTestMethod().getName();

        if (displayName.equals(methodName) || displayName.equals(methodName + "()")) {
            return baseName;
        }
        return String.format("%s [\"%s\"]", baseName, displayName);
    }

    private static boolean isCpuTimeAvailable() {
        return THREAD_MX_BEAN.isThreadCpuTimeSupported() && THREAD_MX_BEAN.isThreadCpuTimeEnabled();
    }

    private static long getCurrentThreadAllocatedBytes() {
        if (SUN_THREAD_MX_BEAN != null && SUN_THREAD_MX_BEAN.isThreadAllocatedMemorySupported()) {
            try {
                return SUN_THREAD_MX_BEAN.getThreadAllocatedBytes(Thread.currentThread().threadId());
            } catch (Exception ignored) {
                // Return -1 if not available
            }
        }
        return -1L;
    }

    private ExtensionContext.Store getTestStore(ExtensionContext context) {
        return context.getStore(TEST_NAMESPACE);
    }

    private void printReport(
            String testName,
            BenchmarkMetrics metrics,
            BenchmarkTest config,
            boolean timeSlaViolated,
            boolean memorySlaViolated) {

        var sb = new StringBuilder();
        sb.append("------------------------------------------------------------------------\n");
        sb.append(String.format("[BENCHMARK] Test: %s\n", testName));
        sb.append(String.format("- Elapsed Time: %s\n", metrics.formatElapsedTime()));
        sb.append(String.format("- CPU Time:     %s\n", metrics.formatCpuTime()));
        sb.append(String.format("- Memory Alloc: %s\n", metrics.formatAllocatedMemory()));

        if (timeSlaViolated && config != null) {
            sb.append(String.format(
                    Locale.ROOT,
                    "- [!] TIME SLA VIOLATION: Elapsed %.2f ms exceeded threshold of %d ms (TLE Risk)\n",
                    metrics.elapsedTimeMillis(),
                    config.maxTimeMillis()
            ));
        }

        if (memorySlaViolated && config != null) {
            sb.append(String.format(
                    Locale.ROOT,
                    "- [!] MEMORY SLA VIOLATION: Allocated %s exceeded threshold of %,d bytes (MLE Risk)\n",
                    metrics.formatAllocatedMemory(),
                    config.maxMemoryBytes()
            ));
        }

        sb.append("------------------------------------------------------------------------");
        System.out.println(sb);
    }

    private void printLeaderboard(String className, List<BenchmarkEntry> entries, BenchmarkTest config) {
        boolean useAnsi = config == null || config.ansiColors();
        String border = "=".repeat(LEADERBOARD_WIDTH);
        String divider = "-".repeat(LEADERBOARD_WIDTH);

        var sb = new StringBuilder();
        sb.append("\n").append(border).append("\n");
        sb.append(String.format(Locale.ROOT, "                          BENCHMARK SUMMARY LEADERBOARD: %s\n", className));
        sb.append(border).append("\n");
        sb.append(String.format(Locale.ROOT, "%-5s | %-44s | %-14s | %-14s | %-14s | %-6s\n",
                "Rank", "Test Name", "Elapsed Time", "CPU Time", "Memory Alloc", "SLA"));
        sb.append(divider).append("\n");

        for (int i = 0; i < entries.size(); i++) {
            BenchmarkEntry entry = entries.get(i);
            String plainRank = "#" + (i + 1);
            String paddedRank = String.format(Locale.ROOT, "%-5s", plainRank);
            String displayRank = useAnsi ? colorizeRank(paddedRank, i + 1) : paddedRank;

            String testName = truncate(entry.testName(), 44);
            String elapsed = entry.metrics().formatShortElapsedTime();
            String cpu = entry.metrics().formatShortCpuTime();
            String mem = entry.metrics().formatShortAllocatedMemory();

            String plainSla = resolveSlaLabel(entry);
            String paddedSla = String.format(Locale.ROOT, "%-6s", plainSla);
            String displaySla = useAnsi ? colorizeSla(paddedSla, plainSla) : paddedSla;

            sb.append(String.format(Locale.ROOT, "%s | %-44s | %-14s | %-14s | %-14s | %s\n",
                    displayRank, testName, elapsed, cpu, mem, displaySla));
        }

        sb.append(border).append("\n");
        System.out.println(sb);
    }

    private static String resolveSlaLabel(BenchmarkEntry entry) {
        if (!entry.isAnySlaViolated()) {
            return "OK";
        }
        if (entry.slaViolated() && entry.memoryViolated()) {
            return "FAIL";
        }
        return entry.slaViolated() ? "TLE" : "MLE";
    }

    private static String colorizeRank(String paddedRank, int rank) {
        return switch (rank) {
            case 1 -> ANSI_BOLD + ANSI_BRIGHT_GREEN + paddedRank + ANSI_RESET;
            case 2 -> ANSI_BOLD + ANSI_CYAN + paddedRank + ANSI_RESET;
            case 3 -> ANSI_BOLD + ANSI_BLUE + paddedRank + ANSI_RESET;
            default -> paddedRank;
        };
    }

    private static String colorizeSla(String paddedSla, String plainSla) {
        return switch (plainSla) {
            case "OK" -> ANSI_GREEN + paddedSla + ANSI_RESET;
            case "TLE", "FAIL" -> ANSI_BOLD + ANSI_BRIGHT_RED + paddedSla + ANSI_RESET;
            case "MLE" -> ANSI_BOLD + ANSI_YELLOW + paddedSla + ANSI_RESET;
            default -> paddedSla;
        };
    }

    private static String truncate(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }

    private void publishReport(
            ExtensionContext context,
            String testName,
            BenchmarkMetrics metrics,
            boolean timeSlaViolated,
            boolean memorySlaViolated) {

        Map<String, String> entries = new HashMap<>();
        entries.put("test", testName);
        entries.put("elapsedTime", metrics.formatElapsedTime());
        entries.put("cpuTime", metrics.formatCpuTime());
        entries.put("allocatedMemory", metrics.formatAllocatedMemory());
        if (timeSlaViolated) {
            entries.put("timeSlaStatus", "VIOLATED");
        }
        if (memorySlaViolated) {
            entries.put("memorySlaStatus", "VIOLATED");
        }
        context.publishReportEntry(entries);
    }
}
