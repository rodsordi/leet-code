package br.com.leetcode.problemset;

import br.com.leetcode.support.benchmark.BenchmarkTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@BenchmarkTest
class P21MergeTwoSortedListsTest {

    private P21MergeTwoSortedLists p21MergeTwoSortedLists;

    @BeforeEach
    void beforeEach() {
        p21MergeTwoSortedLists = new P21MergeTwoSortedLists();
    }

    @Test
    void example1() {
        // Given
        var list1 = new P21MergeTwoSortedLists.ListNode(1, 2, 4);
        var list2 = new P21MergeTwoSortedLists.ListNode(1, 3, 4);
        // When
        var actual = p21MergeTwoSortedLists.mergeTwoLists(list1, list2);
        // Then
        assertThat(actual.getList())
                .containsExactly(1, 1, 2, 3, 4, 4);
    }

    @Test
    void example2() {
        // Given
        P21MergeTwoSortedLists.ListNode list1 = null;
        P21MergeTwoSortedLists.ListNode list2 = null;
        // When
        var actual = p21MergeTwoSortedLists.mergeTwoLists(list1, list2);
        // Then
        assertThat(actual)
                .isNull();
    }

    @Test
    void example3() {
        // Given
        P21MergeTwoSortedLists.ListNode list1 = null;
        var list2 = new P21MergeTwoSortedLists.ListNode(0);
        // When
        var actual = p21MergeTwoSortedLists.mergeTwoLists(list1, list2);
        // Then
        assertThat(actual.getList())
                .containsExactly(0);
    }

    @Test
    void example4() {
        // Given
        var list1 = new P21MergeTwoSortedLists.ListNode(1);
        P21MergeTwoSortedLists.ListNode list2 = null;
        // When
        var actual = p21MergeTwoSortedLists.mergeTwoLists(list1, list2);
        // Then
        assertThat(actual.getList())
                .containsExactly(1);
    }

    @Test
    void example5() {
        // Given
        var list1 = new P21MergeTwoSortedLists.ListNode(2);
        var list2 = new P21MergeTwoSortedLists.ListNode(1);
        // When
        var actual = p21MergeTwoSortedLists.mergeTwoLists(list1, list2);
        // Then
        assertThat(actual.getList())
                .containsExactly(1, 2);
    }
}