package br.com.leetcode.problemset;

import br.com.leetcode.support.benchmark.BenchmarkTest;
import br.com.leetcode.support.memory.MemoryAllocation;
import br.com.leetcode.support.memory.MemoryProfileTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@BenchmarkTest
class P13RomanToIntegerTest {

    private P13RomanToInteger p13RomanToInteger;

    @BeforeEach
    void beforeEach() {
        p13RomanToInteger = new P13RomanToInteger();
    }

    @MemoryProfileTest(expectedAllocation = MemoryAllocation.ZERO)
    void measureMemoryAllocation() {
        var s = "MCMXCIV";
        p13RomanToInteger.romanToInt(s);
    }

    @Test
    void example1() {
        // Given
        var s = "III";
        // When
        var actual = p13RomanToInteger.romanToInt(s);
        // Then
        assertThat(actual)
                .isEqualTo(3);
    }

    @Test
    void example2() {
        // Given
        var s = "LVIII";
        // When
        var actual = p13RomanToInteger.romanToInt(s);
        // Then
        assertThat(actual)
                .isEqualTo(58);
    }

    @Test
    void example3() {
        // Given
        var s = "MCMXCIV";
        // When
        var actual = p13RomanToInteger.romanToInt(s);
        // Then
        assertThat(actual)
                .isEqualTo(1994);
    }
}