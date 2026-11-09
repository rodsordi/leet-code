package br.com.leetcode.support.complexity;

import org.assertj.core.api.AbstractAssert;

import java.util.function.IntConsumer;

/**
 * Custom AssertJ assertion for validating algorithmic asymptotic complexity.
 */
public class ComplexityAssert extends AbstractAssert<ComplexityAssert, ComplexityAssessment> {

    public ComplexityAssert(ComplexityAssessment actual) {
        super(actual, ComplexityAssert.class);
    }

    public static ComplexityAssert assertThatComplexity(ComplexityAssessment actual) {
        return new ComplexityAssert(actual);
    }

    /**
     * Convenient one-liner to assert O(N) linear complexity for a given algorithm.
     *
     * @param algorithm Consumer accepting input size N and executing the algorithm
     */
    public static void assertLinear(IntConsumer algorithm) {
        var evaluator = new ComplexityEvaluator(50_000, 3, 10, 5);
        ComplexityAssessment assessment = evaluator.evaluate(size -> size, algorithm::accept);
        assertThatComplexity(assessment).isLinear();
    }

    /**
     * Convenient one-liner to assert O(N^2) quadratic complexity for a given algorithm.
     *
     * @param algorithm Consumer accepting input size N and executing the algorithm
     */
    public static void assertQuadratic(IntConsumer algorithm) {
        var evaluator = new ComplexityEvaluator(1_000, 3, 10, 5);
        ComplexityAssessment assessment = evaluator.evaluate(size -> size, algorithm::accept);
        assertThatComplexity(assessment).isQuadratic();
    }

    public ComplexityAssert isQuadratic() {
        return isOrderOf(ComplexityOrder.QUADRATIC, 0.6);
    }

    public ComplexityAssert isLinear() {
        return isOrderOf(ComplexityOrder.LINEAR, 0.45);
    }

    public ComplexityAssert isOrderOf(ComplexityOrder expectedOrder, double tolerance) {
        isNotNull();

        double expectedExponent = expectedOrder.expectedExponent();
        double actualExponent = actual.estimatedExponent();

        if (Math.abs(actualExponent - expectedExponent) > tolerance) {
            failWithMessage("""
                    Expected algorithmic complexity %s (exponent ~%.2f +/- %.2f)
                    but empirical measurement produced exponent %.2f (classified as %s).
                    %s""",
                    expectedOrder.notation(),
                    expectedExponent,
                    tolerance,
                    actualExponent,
                    actual.classifiedOrder().notation(),
                    actual.formatReport()
            );
        }

        return this;
    }
}
