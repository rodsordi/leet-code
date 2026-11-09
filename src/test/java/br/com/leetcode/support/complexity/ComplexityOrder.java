package br.com.leetcode.support.complexity;

/**
 * Canonical asymptotic complexity orders with expected doubling ratios and exponents.
 */
public enum ComplexityOrder {
    CONSTANT(0.0, 1.0, "O(1)"),
    LOGARITHMIC(0.0, 1.0, "O(log N)"),
    LINEAR(1.0, 2.0, "O(N)"),
    LINEARITHMIC(1.15, 2.3, "O(N log N)"),
    QUADRATIC(2.0, 4.0, "O(N^2)"),
    CUBIC(3.0, 8.0, "O(N^3)");

    private final double expectedExponent;
    private final double expectedRatio;
    private final String notation;

    ComplexityOrder(double expectedExponent, double expectedRatio, String notation) {
        this.expectedExponent = expectedExponent;
        this.expectedRatio = expectedRatio;
        this.notation = notation;
    }

    public double expectedExponent() {
        return expectedExponent;
    }

    public double expectedRatio() {
        return expectedRatio;
    }

    public String notation() {
        return notation;
    }

    public static ComplexityOrder fromExponent(double exponent) {
        if (exponent < 0.5) {
            return CONSTANT;
        } else if (exponent < 1.10) {
            return LINEAR;
        } else if (exponent < 1.45) {
            return LINEARITHMIC;
        } else if (exponent < 2.5) {
            return QUADRATIC;
        } else {
            return CUBIC;
        }
    }
}
