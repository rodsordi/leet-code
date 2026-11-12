package br.com.leetcode.problemset;

import br.com.leetcode.support.benchmark.BenchmarkTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@BenchmarkTest
class P14LongestCommonPrefixTest {

    private P14LongestCommonPrefix p14LongestCommonPrefix;

    @BeforeEach
    void beforeEach() {
        p14LongestCommonPrefix = new P14LongestCommonPrefix();
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
}