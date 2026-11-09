package br.com.leetcode.support.benchmark;

/**
 * Immutable snapshot capturing starting values of high-resolution execution timers and thread resources.
 *
 * @param startTimeNanos     High-resolution start wall-clock time in nanoseconds
 * @param startCpuTimeNanos  Thread CPU time in nanoseconds at start, or -1 if unsupported
 * @param startAllocatedBytes Heap memory allocated in bytes by the thread at start, or -1 if unsupported
 */
public record BenchmarkSnapshot(
        long startTimeNanos,
        long startCpuTimeNanos,
        long startAllocatedBytes
) {}
