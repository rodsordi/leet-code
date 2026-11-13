package br.com.leetcode.problemset;

import br.com.leetcode.support.benchmark.BenchmarkTest;
import br.com.leetcode.support.memory.MemoryAllocation;
import br.com.leetcode.support.memory.MemoryProfileTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@BenchmarkTest
class P14LongestCommonPrefixTest {

    private P14LongestCommonPrefix p14LongestCommonPrefix;

    @BeforeEach
    void beforeEach() {
        p14LongestCommonPrefix = new P14LongestCommonPrefix();
    }

    @MemoryProfileTest(expectedAllocation = MemoryAllocation.CONSTANT, maxAllocatedBytes = 256)
    @DisplayName("Should prove longestCommonPrefix maintains O(1) bounded heap allocation")
    void measureMemoryAllocation() {
        var strs = new String[] {
                "interspecies",
                "interstellar",
                "interstate",
                "interactive",
                "intersection"
        };
        p14LongestCommonPrefix.longestCommonPrefix(strs);
    }

    @Test
    void example1() {
        // Given
        var strs = new String[] {"flower","flow","flight"};
        // When
        var actual = p14LongestCommonPrefix.longestCommonPrefix(strs);
        // Then
        assertThat(actual)
                .isEqualTo("fl");
    }

    @Test
    void example2() {
        // Given
        var strs = new String[] {"dog","racecar","car"};
        // When
        var actual = p14LongestCommonPrefix.longestCommonPrefix(strs);
        // Then
        assertThat(actual)
                .isEqualTo("");
    }

    @Test
    void example3() {
        // Given
        var strs = new String[] {"ab","a"};
        // When
        var actual = p14LongestCommonPrefix.longestCommonPrefix(strs);
        // Then
        assertThat(actual)
                .isEqualTo("a");
    }
}
