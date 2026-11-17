package br.com.leetcode.problemset;

import br.com.leetcode.support.benchmark.BenchmarkTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@BenchmarkTest
class P26RemoveDuplicatesFromSortedArrayTest {

    private P26RemoveDuplicatesFromSortedArray p26RemoveDuplicatesFromSortedArray;

    @BeforeEach
    void beforeEach() {
        p26RemoveDuplicatesFromSortedArray = new P26RemoveDuplicatesFromSortedArray();
    }

    @Test
    void example1() {
        // Given
        var nums = new int[] {1,1,2};
        // When
        var actual = p26RemoveDuplicatesFromSortedArray.removeDuplicates(nums);
        // Then
        assertThat(actual)
                .isEqualTo(2);
        assertThat(nums)
                .startsWith(1, 2);
    }

    @Test
    void example2() {
        // Given
        var nums = new int[] {0,0,1,1,1,2,2,3,3,4};
        // When
        var actual = p26RemoveDuplicatesFromSortedArray.removeDuplicates(nums);
        // Then
        assertThat(actual)
                .isEqualTo(5);
        assertThat(nums)
                .startsWith(0,1,2,3,4);
    }
}