package br.com.leetcode.problemset;

import br.com.leetcode.support.benchmark.BenchmarkTest;
import br.com.leetcode.support.memory.MemoryAllocation;
import br.com.leetcode.support.memory.MemoryProfileTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@BenchmarkTest
class P20ValidParenthesesTest {

    private P20ValidParentheses p20ValidParentheses;

    @BeforeEach
    void beforeEach() {
        p20ValidParentheses = new P20ValidParentheses();
    }

    @MemoryProfileTest(expectedAllocation = MemoryAllocation.LINEAR, initialSize = 10_000, steps = 3)
    @DisplayName("Should prove stack memory allocation scales linearly O(N) with input size")
    void measureMemoryAllocation(int size) {
        var s = "()".repeat(size / 2);
        p20ValidParentheses.isValid(s);
    }

    @Test
    void example1() {
        // Given
        var s = "()";
        // When
        var actual = p20ValidParentheses.isValid(s);
        // Then
        assertThat(actual)
                .isTrue();
    }

    @Test
    void example2() {
        // Given
        var s = "()[]{}";
        // When
        var actual = p20ValidParentheses.isValid(s);
        // Then
        assertThat(actual)
                .isTrue();
    }

    @Test
    void example3() {
        // Given
        var s = "(]";
        // When
        var actual = p20ValidParentheses.isValid(s);
        // Then
        assertThat(actual)
                .isFalse();
    }

    @Test
    void example4() {
        // Given
        var s = "([])";
        // When
        var actual = p20ValidParentheses.isValid(s);
        // Then
        assertThat(actual)
                .isTrue();
    }

    @Test
    void example5() {
        // Given
        var s = "([)]";
        // When
        var actual = p20ValidParentheses.isValid(s);
        // Then
        assertThat(actual)
                .isFalse();
    }

    @Test
    void example6() {
        // Given
        var s = "([])[]{[()]}";
        // When
        var actual = p20ValidParentheses.isValid(s);
        // Then
        assertThat(actual)
                .isTrue();
    }

    @Test
    void example7() {
        // Given
        var s = "([])[]{[()}]";
        // When
        var actual = p20ValidParentheses.isValid(s);
        // Then
        assertThat(actual)
                .isFalse();
    }

    @Test
    void example8() {
        // Given
        var s = "()]";
        // When
        var actual = p20ValidParentheses.isValid(s);
        // Then
        assertThat(actual)
                .isFalse();
    }

    @Test
    void example9() {
        // Given
        var s = "[";
        // When
        var actual = p20ValidParentheses.isValid(s);
        // Then
        assertThat(actual)
                .isFalse();
    }

    @Test
    void example10() {
        // Given
        var s = "((";
        // When
        var actual = p20ValidParentheses.isValid(s);
        // Then
        assertThat(actual)
                .isFalse();
    }

    @Test
    void example11() {
        // Given
        var s = "[[[[[[[[[[[";
        // When
        var actual = p20ValidParentheses.isValid(s);
        // Then
        assertThat(actual)
                .isFalse();
    }
}