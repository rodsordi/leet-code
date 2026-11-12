package br.com.leetcode.support.memory;

/**
 * Expected memory allocation profile for an algorithm.
 */
public enum MemoryAllocation {

    /**
     * Strictly zero bytes allocated on the JVM Heap.
     * All variables reside entirely on CPU registers or the thread's stack frame.
     */
    ZERO("Zero Allocation (0 bytes)"),

    /**
     * Constant auxiliary space complexity O(1).
     * Bounded heap allocation not growing with input size.
     */
    CONSTANT("O(1) Constant Space"),

    /**
     * Linear auxiliary space complexity O(N).
     * Heap allocation grows proportionally to input size.
     */
    LINEAR("O(N) Linear Space");

    private final String description;

    MemoryAllocation(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }
}
