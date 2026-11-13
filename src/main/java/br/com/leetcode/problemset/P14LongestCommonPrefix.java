package br.com.leetcode.problemset;

/**
 * https://leetcode.com/problems/longest-common-prefix/description/
 *
 * Write a function to find the longest common prefix string amongst an array of strings.
 *
 * If there is no common prefix, return an empty string "".
 *
 *
 *
 * Example 1:
 *
 * Input: strs = ["flower","flow","flight"]
 * Output: "fl"
 * Example 2:
 *
 * Input: strs = ["dog","racecar","car"]
 * Output: ""
 * Explanation: There is no common prefix among the input strings.
 *
 *
 * Constraints:
 *
 * 1 <= strs.length <= 200
 * 0 <= strs[i].length <= 200
 * strs[i] consists of only lowercase English letters if it is non-empty.
 */
public class P14LongestCommonPrefix {

    public String longestCommonPrefix(String[] strs) {
        char[] chars = null;
        int maxLength = 0;
        for (var s : strs) {
            if (chars == null) {
                chars = s.toCharArray();
                maxLength = s.length();
            } else {
                if (maxLength > s.length())
                    maxLength = s.length();

                for (int i = maxLength - 1; i >= 0; i--) {
                    if (chars[i] != s.charAt(i)) {
                        chars[i] = ' ';
                        maxLength = i;
                    }
                }
            }
        }

        for (var i = maxLength; i < chars.length; i++)
            chars[i] = ' ';

        return new String(chars).trim();
    }
}
