package br.com.leetcode.support.complexity;

import org.junit.jupiter.api.DisplayName;

import java.util.concurrent.atomic.AtomicLong;

@DisplayName("Declarative @ComplexityTest Annotation Infrastructure")
class ComplexityTestExtensionTest {

    private final AtomicLong counter = new AtomicLong();

    @ComplexityTest(expected = ComplexityOrder.LINEAR, initialSize = 500_000)
    @DisplayName("Should validate O(N) linear algorithm with declarative annotation")
    void shouldValidateLinearComplexity(int size) {
        long hash = 1125899906842597L;
        for (int i = 0; i < size; i++) {
            hash = 31 * hash + (i ^ 0x5DEECE66DL);
        }
        counter.set(hash);
    }

    @ComplexityTest(expected = ComplexityOrder.QUADRATIC, initialSize = 1_000)
    @DisplayName("Should validate O(N^2) quadratic algorithm with declarative annotation")
    void shouldValidateQuadraticComplexity(int size) {
        long count = 0;
        for (int i = 0; i < size; i++) {
            for (int j = i; j < size; j++) {
                count += (i ^ j);
            }
        }
        counter.set(count);
    }
}
