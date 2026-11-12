package br.com.leetcode.problemset;

import br.com.leetcode.support.benchmark.BenchmarkTest;
import br.com.leetcode.support.memory.MemoryAllocation;
import br.com.leetcode.support.memory.MemoryProfileTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@BenchmarkTest
class P9PalindromeNumberTest {

    private P9PalindromeNumber p9PalindromeNumber;

    @BeforeEach
    void beforeEach() {
        p9PalindromeNumber = new P9PalindromeNumber();
    }

    @MemoryProfileTest(expectedAllocation = MemoryAllocation.ZERO)
    @DisplayName("Should prove isPalindrome operates with 0 bytes heap memory allocation")
    void measureMemoryAllocation() {
        var x = 1122332211;
        p9PalindromeNumber.isPalindrome(x);
    }

    @Test
    void example1() {
        // Given
        var x = 121;
        // When
        var actual = p9PalindromeNumber.isPalindrome(x);
        // Then
        assertTrue(actual);
    }

    @Test
    void example2() {
        // Given
        var x = -121;
        // When
        var actual = p9PalindromeNumber.isPalindrome(x);
        // Then
        assertFalse(actual);
    }

    @Test
    void example3() {
        // Given
        var x = 10;
        // When
        var actual = p9PalindromeNumber.isPalindrome(x);
        // Then
        assertFalse(actual);
    }

    @Test
    @DisplayName("Should confirm 0 is a palindrome")
    void exampleZero() {
        assertTrue(p9PalindromeNumber.isPalindrome(0));
    }
}
