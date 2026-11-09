package br.com.leetcode.support.complexity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicLong;

import static br.com.leetcode.support.complexity.ComplexityAssert.assertThatComplexity;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Algorithmic Complexity Evaluator Infrastructure")
class ComplexityEvaluatorTest {

    @Test
    @DisplayName("Should empirically prove O(N) complexity for a linear algorithm")
    void shouldDetectLinearComplexity() {
        var evaluator = new ComplexityEvaluator(500_000, 3, 5, 5);
        var counter = new AtomicLong();

        ComplexityAssessment assessment = evaluator.evaluate(
                size -> size,
                size -> {
                    long hash = 1125899906842597L;
                    for (int i = 0; i < size; i++) {
                        hash = 31 * hash + (i ^ 0x5DEECE66DL);
                    }
                    counter.set(hash);
                }
        );

        assertThat(assessment.classifiedOrder()).isEqualTo(ComplexityOrder.LINEAR);
        assertThatComplexity(assessment).isLinear();
    }

    @Test
    @DisplayName("Should empirically prove O(N^2) complexity for a nested loop algorithm")
    void shouldDetectQuadraticComplexity() {
        var evaluator = new ComplexityEvaluator(1_000, 3, 10, 5);
        var counter = new AtomicLong();

        ComplexityAssessment assessment = evaluator.evaluate(
                size -> {
                    int[] arr = new int[size];
                    for (int i = 0; i < size; i++) {
                        arr[i] = i;
                    }
                    return arr;
                },
                arr -> {
                    long count = 0;
                    for (int i = 0; i < arr.length; i++) {
                        for (int j = i; j < arr.length; j++) {
                            count += (arr[i] ^ arr[j]);
                        }
                    }
                    counter.set(count);
                }
        );

        assertThat(assessment.classifiedOrder()).isEqualTo(ComplexityOrder.QUADRATIC);
        assertThatComplexity(assessment).isQuadratic();
    }

    @Test
    @DisplayName("Should format report containing expected notation and metrics")
    void shouldFormatAssessmentReport() {
        var assessment = new ComplexityAssessment(
                java.util.List.of(1000, 2000, 4000),
                java.util.List.of(1_000_000L, 4_000_000L, 16_000_000L),
                java.util.List.of(4.0, 4.0),
                2.0,
                ComplexityOrder.QUADRATIC
        );

        String report = assessment.formatReport();
        assertThat(report)
                .contains("O(N^2)")
                .contains("QUADRATIC")
                .contains("4.00x")
                .contains("Estimated Big-O Exponent: 2.00");
    }
}
