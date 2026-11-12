package br.com.leetcode.support.memory;

import org.junit.jupiter.api.DisplayName;

import java.util.concurrent.atomic.AtomicLong;

@DisplayName("Declarative @MemoryProfileTest Infrastructure")
class MemoryProfileTestExtensionTest {

    private final AtomicLong sink = new AtomicLong();

    @MemoryProfileTest(expectedAllocation = MemoryAllocation.ZERO)
    @DisplayName("Should verify 0 bytes heap allocation on pure primitive arithmetic")
    void shouldVerifyZeroAllocation() {
        int x = 123456789;
        int reversed = 0;
        int tmp = x;
        while (tmp > 0) {
            reversed = reversed * 10 + (tmp % 10);
            tmp /= 10;
        }
        sink.set(reversed);
    }

    @MemoryProfileTest(expectedAllocation = MemoryAllocation.CONSTANT, maxAllocatedBytes = 256)
    @DisplayName("Should verify constant memory allocation within budget")
    void shouldVerifyConstantAllocation() {
        int[] smallBuffer = new int[4];
        smallBuffer[0] = 42;
        sink.set(smallBuffer[0]);
    }

    @MemoryProfileTest(expectedAllocation = MemoryAllocation.LINEAR, initialSize = 10_000, steps = 3)
    @DisplayName("Should detect linear heap memory growth with doubling array sizes")
    void shouldDetectLinearMemoryGrowth(int size) {
        int[] arr = new int[size];
        sink.set(arr.length);
    }
}
