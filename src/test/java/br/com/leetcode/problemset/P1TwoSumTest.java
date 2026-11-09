package br.com.leetcode.problemset;

import br.com.leetcode.support.benchmark.BenchmarkTest;
import br.com.leetcode.support.complexity.ComplexityOrder;
import br.com.leetcode.support.complexity.ComplexityTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Random;

import static java.util.Arrays.fill;
import static org.assertj.core.api.Assertions.assertThat;

@BenchmarkTest
class P1TwoSumTest {

    private final int thousand = 1000;
    private final int million = thousand * thousand;

    private P1TwoSum p1TwoSum;

    @BeforeEach
    void beforeEach() {
        p1TwoSum = new P1TwoSum();
    }

    @ComplexityTest(expected = ComplexityOrder.LINEAR)
    @DisplayName("Should empirically prove twoSum is O(N) linear")
    void measureComplexity(int size) {
        int[] nums = new int[size];
        fill(nums, 1);
        nums[size - 2] = 500;
        nums[size - 1] = 501;
        p1TwoSum.twoSum(nums, 1001);
    }

    @ValueSource(ints = {thousand, million})
    @ParameterizedTest
    void load(int volume) {
        // Given
        int[] nums = new int[volume];
        fill(nums, 1);
        // And
        var random = new Random();
        var a = random.nextInt(volume / 2);
        nums[a] = 50000;
        var b = random.nextInt(volume / 2, volume);
        nums[b] = 50000;
        int target = 100000;

        // When
        var actual = p1TwoSum.twoSum(nums, target);

        // Then
        assertThat(actual)
                .containsExactly(a, b);
    }

    @Test
    void example1() {
        // Given
        int[] nums = new int[]{2, 7, 11, 15};
        int target = 9;

        // When
        var actual = p1TwoSum.twoSum(nums, target);

        // Then
        assertThat(actual)
                .containsExactly(0, 1);
    }

    @Test
    void example2() {
        // Given
        int[] nums = new int[]{3, 2, 4};
        int target = 6;

        // When
        var actual = p1TwoSum.twoSum(nums, target);

        // Then
        assertThat(actual)
                .containsExactly(1, 2);
    }

    @Test
    void example3() {
        // Given
        int[] nums = new int[]{3, 3};
        int target = 6;

        // When
        var actual = p1TwoSum.twoSum(nums, target);

        // Then
        assertThat(actual)
                .containsExactly(0, 1);
    }
}
