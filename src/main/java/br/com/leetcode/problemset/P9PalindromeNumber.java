package br.com.leetcode.problemset;

/**
 * https://leetcode.com/problems/palindrome-number/
 *
 * Constraints:
 *
 * -231 <= x <= 231 - 1
 *
 *
 * Follow up: Could you solve it without converting the integer to a string?
 */
public class P9PalindromeNumber {

    public boolean isPalindrome(int x) {
        if (x < 0)
            return false;

        var size = (int) Math.log10(x) + 1;
        var tmp = x;
        var result = 0;
        for (var i = 0; i < size; i++) {
            var lastDigit = tmp % 10;
            result *= 10;
            result += lastDigit;
            tmp /= 10;
        }

        return x == result;
    }
}
