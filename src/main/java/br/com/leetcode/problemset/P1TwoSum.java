package br.com.leetcode.problemset;

/**
 * https://leetcode.com/problems/two-sum/
 *
 * Constraints:
 *
 * 2 <= nums.length <= 104
 * -109 <= nums[i] <= 109
 * -109 <= target <= 109
 * Only one valid answer exists.
 *
 *
 * Follow-up: Can you come up with an algorithm that is less than O(n2) time complexity?
 */
public class P1TwoSum {

    public int[] twoSum(int[] nums, int target) {
        var map = new java.util.HashMap<Integer, Integer>(nums.length);
        for (var i = 0; i < nums.length; i++)
                map.put(nums[i], i);

        for (var i = 0; i < nums.length; i++) {
            var key = target - nums[i];
            if (map.containsKey(key)) {
                int result = map.get(key);
                if (i == result)
                    continue;

                if (i < result)
                    return new int[] {i, result};
                else
                    return new int[] {result, i};
            }
        }

        throw new RuntimeException("Not found");
    }
}
