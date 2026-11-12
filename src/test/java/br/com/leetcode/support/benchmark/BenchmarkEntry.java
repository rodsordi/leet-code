package br.com.leetcode.support.benchmark;

import java.util.Objects;

/**
 * Immutable entry representing the benchmark result of a single test execution.
 *
 * @param testName       Semantic name of the executed test
 * @param metrics        Computed execution metrics (wall time, CPU time, memory)
 * @param slaViolated    Whether this test exceeded its defined time SLA threshold
 * @param memoryViolated Whether this test exceeded its defined memory SLA threshold
 */
public record BenchmarkEntry(
        String testName,
        BenchmarkMetrics metrics,
        boolean slaViolated,
        boolean memoryViolated
) {
    public BenchmarkEntry {
        Objects.requireNonNull(testName, "testName cannot be null");
        Objects.requireNonNull(metrics, "metrics cannot be null");
    }

    public BenchmarkEntry(String testName, BenchmarkMetrics metrics, boolean slaViolated) {
        this(testName, metrics, slaViolated, false);
    }

    public boolean isAnySlaViolated() {
        return slaViolated || memoryViolated;
    }

    public boolean isWarmup() {
        return testName.toLowerCase().contains("warmup");
    }
}
