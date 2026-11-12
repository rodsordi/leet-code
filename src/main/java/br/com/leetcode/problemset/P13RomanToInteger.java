package br.com.leetcode.problemset;

/**
 * https://leetcode.com/problems/roman-to-integer
 * <p>
 * Roman numerals are represented by seven different symbols: I, V, X, L, C, D and M.
 * <p>
 * Symbol       Value
 * I             1
 * V             5
 * X             10
 * L             50
 * C             100
 * D             500
 * M             1000
 * For example, 2 is written as II in Roman numeral, just two ones added together. 12 is written as XII, which is simply X + II. The number 27 is written as XXVII, which is XX + V + II.
 * <p>
 * Roman numerals are usually written largest to smallest from left to right. However, the numeral for four is not IIII. Instead, the number four is written as IV. Because the one is before the five we subtract it making four. The same principle applies to the number nine, which is written as IX. There are six instances where subtraction is used:
 * <p>
 * I can be placed before V (5) and X (10) to make 4 and 9.
 * X can be placed before L (50) and C (100) to make 40 and 90.
 * C can be placed before D (500) and M (1000) to make 400 and 900.
 * Given a roman numeral, convert it to an integer.
 * <p>
 * <p>
 * <p>
 * Example 1:
 * <p>
 * Input: s = "III"
 * Output: 3
 * Explanation: III = 3.
 * Example 2:
 * <p>
 * Input: s = "LVIII"
 * Output: 58
 * Explanation: L = 50, V= 5, III = 3.
 * Example 3:
 * <p>
 * Input: s = "MCMXCIV"
 * Output: 1994
 * Explanation: M = 1000, CM = 900, XC = 90 and IV = 4.
 * <p>
 * <p>
 * Constraints:
 * <p>
 * 1 <= s.length <= 15
 * s contains only the characters ('I', 'V', 'X', 'L', 'C', 'D', 'M').
 * It is guaranteed that s is a valid roman numeral in the range [1, 3999].
 */
public class P13RomanToInteger {

    public int romanToInt(String s) {
        if (s.isEmpty() || s.length() > 15)
            throw new RuntimeException("s length should be between 1 and 15");

        var previousCharValue = 0;
        var result = 0;
        int consecutiveCount = 1;

        for (var i = s.length() - 1; i >= 0; i--) {
            var c = s.charAt(i);

            if (i < s.length() - 1 && c == s.charAt(i + 1)) {
                consecutiveCount++;
                if (consecutiveCount > 3) {
                    throw new RuntimeException("Sequence of 4 equal digits are forbidden");
                }
            } else {
                consecutiveCount = 1;
            }

            int value = switch (c) {
                case 'I' -> 1;
                case 'V' -> 5;
                case 'X' -> 10;
                case 'L' -> 50;
                case 'C' -> 100;
                case 'D' -> 500;
                case 'M' -> 1000;
                default -> throw new RuntimeException("s should be 'I', 'V', 'X', 'L', 'C', 'D' or 'M'");
            };

            if (value < previousCharValue)
                result -= value;
            else
                result += value;
            previousCharValue = value;

            if (result > 3999)
                throw new RuntimeException("s should be between 1 and 3999");
        }

        return result;
    }
}
