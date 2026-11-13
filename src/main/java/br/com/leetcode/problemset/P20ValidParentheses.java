package br.com.leetcode.problemset;

/**
 * Given a string s containing just the characters '(', ')', '{', '}', '[' and ']', determine if the input string is valid.
 * <p>
 * An input string is valid if:
 * <p>
 * Open brackets must be closed by the same type of brackets.
 * Open brackets must be closed in the correct order.
 * Every close bracket has a corresponding open bracket of the same type.
 * <p>
 * <p>
 * Example 1:
 * <p>
 * Input: s = "()"
 * <p>
 * Output: true
 * <p>
 * Example 2:
 * <p>
 * Input: s = "()[]{}"
 * <p>
 * Output: true
 * <p>
 * Example 3:
 * <p>
 * Input: s = "(]"
 * <p>
 * Output: false
 * <p>
 * Example 4:
 * <p>
 * Input: s = "([])"
 * <p>
 * Output: true
 * <p>
 * Example 5:
 * <p>
 * Input: s = "([)]"
 * <p>
 * Output: false
 * <p>
 * <p>
 * <p>
 * Constraints:
 * <p>
 * 1 <= s.length <= 104
 * s consists of parentheses only '()[]{}'.
 */
public class P20ValidParentheses {

    public boolean isValid(String s) {
        var brackets = new char[(s.length() / 2) + 1];
        var currentBracketIndex = 0;
        var stringArray = s.toCharArray();
        for (var i = 0; i < stringArray.length; i++) {
            var currentBracket = stringArray[i];
            if (currentBracketIndex == 0) {
                brackets[currentBracketIndex] = currentBracket;
                currentBracketIndex++;
            } else {
                var previousBracketIndex = currentBracketIndex - 1;
                var previousBracket = brackets[previousBracketIndex];
                if (shouldClose(previousBracket, currentBracket)) {
                    currentBracketIndex--;
                    brackets[currentBracketIndex] = '\u0000';
                } else if (currentBracketIndex > brackets.length - 1) {
                    return false;
                } else {
                    brackets[currentBracketIndex] = currentBracket;
                    currentBracketIndex++;
                }
            }
        }

        if (currentBracketIndex > 0)
            return false;

        return true;
    }

    private boolean shouldClose(char c, char other) {
        return c == '(' && other == ')' ||
                c == '[' && other == ']' ||
                c == '{' && other == '}';
    }
}
