package br.com.leetcode.problemset;

/**
 * You are given the heads of two sorted linked lists list1 and list2.
 * <p>
 * Merge the two lists into one sorted list. The list should be made by splicing together the nodes of the first two lists.
 * <p>
 * Return the head of the merged linked list.
 * <p>
 * <p>
 * <p>
 * Example 1:
 * <p>
 * <p>
 * Input: list1 = [1,2,4], list2 = [1,3,4]
 * Output: [1,1,2,3,4,4]
 * <p>
 * Example 2:
 * <p>
 * Input: list1 = [], list2 = []
 * Output: []
 * <p>
 * Example 3:
 * <p>
 * Input: list1 = [], list2 = [0]
 * Output: [0]
 * <p>
 * <p>
 * Constraints:
 * <p>
 * The number of nodes in both lists is in the range [0, 50].
 * -100 <= Node.val <= 100
 * Both list1 and list2 are sorted in non-decreasing order.
 */
public class P21MergeTwoSortedLists {

    public ListNode mergeTwoLists(ListNode list1, ListNode list2) {
        sort(list1);
        sort(list2);

        ListNode result;
        if (list1 == null) {
            result = list2;
        } else if (list2 == null) {
            result = list1;
        } else if (list1.val < list2.val) {
            result = list1;
            list1 = list1.next;
            if (list1 == null)
                result.next = list2;
        } else {
            result = list2;
            list2 = list2.next;
            if (list2 == null)
                result.next = list1;
        }

        ListNode current = result;

        while (list1 != null && list2 != null) {
            if (list1.val < list2.val) {
                current.next = list1;
                current = list1;
                if (list1.next != null) {
                    list1 = list1.next;
                } else {
                    current.next = list2;
                    list2 = null;
                }
            } else {
                current.next = list2;
                current = list2;
                if (list2.next != null) {
                    list2 = list2.next;
                } else {
                    current.next = list1;
                    list1 = null;
                }
            }
        }

        return result;
    }

    private ListNode sort(ListNode listNode) {
        if (listNode == null)
            return null;

        while (listNode.next != null) {
            if (listNode.val > listNode.next.val) {
                var aux = listNode;
                listNode = listNode.next;
                aux.next = listNode.next;
                listNode.next = aux;
            }
            listNode = listNode.next;
        }
        return listNode;
    }

    public static class ListNode {

        int val;
        ListNode next;

        ListNode() {
        }

        ListNode(int val) {
            this.val = val;
        }

        ListNode(int val, ListNode next) {
            this.val = val;
            this.next = next;
        }

        public ListNode(int... ints) {
            ListNode prev = null;
            for (var i = ints.length - 1; i >= 0; i--) {
                if (i == 0) {
                    val = ints[i];
                    next = prev;
                } else if (i == ints.length - 1)
                    prev = new ListNode(ints[i]);
                else
                    prev = new ListNode(ints[i], prev);
            }
        }

        public int[] getList() {
            ListNode next = this.next;
            int size = 1;
            while (next != null) {
                size++;
                next = next.next;
            }

            var result = new int[size];
            next = this;
            int i = 0;
            while (next != null) {
                result[i] = next.val;
                next = next.next;
                i++;
            }
            return result;
        }

        @Override
        public String toString() {
            return "val: " + val + ", next: " + (next != null ? next.val : "null");
        }
    }
}
