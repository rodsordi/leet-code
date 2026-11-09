package br.com.leetcode.support.complexity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Empirical complexity evaluator using Sedgewick's Doubling Hypothesis.
 * Measures execution time across doubled input sizes (N, 2N, 4N...) and computes
 * the growth ratio and polynomial exponent b = log2(T(2N) / T(N)).
 */
public class ComplexityEvaluator {

    private final int initialSize;
    private final int steps;
    private final int warmupIterations;
    private final int measurementIterations;

    public ComplexityEvaluator(int initialSize, int steps, int warmupIterations, int measurementIterations) {
        if (initialSize <= 0) {
            throw new IllegalArgumentException("initialSize must be greater than 0");
        }
        if (steps < 2) {
            throw new IllegalArgumentException("At least 2 steps are required to calculate a doubling ratio");
        }
        if (warmupIterations < 0) {
            throw new IllegalArgumentException("warmupIterations cannot be negative");
        }
        if (measurementIterations <= 0) {
            throw new IllegalArgumentException("measurementIterations must be greater than 0");
        }
        this.initialSize = initialSize;
        this.steps = steps;
        this.warmupIterations = warmupIterations;
        this.measurementIterations = measurementIterations;
    }

    public static ComplexityEvaluator ofDefault() {
        return new ComplexityEvaluator(1_000, 3, 10, 5);
    }

    public <T> ComplexityAssessment evaluate(Function<Integer, T> inputGenerator, Consumer<T> algorithm) {
        Objects.requireNonNull(inputGenerator, "inputGenerator cannot be null");
        Objects.requireNonNull(algorithm, "algorithm cannot be null");

        // 1. Warmup Phase at initialSize to trigger On-Stack Replacement (OSR) and C2 tiered compilation
        for (int w = 0; w < warmupIterations; w++) {
            T warmupInput = inputGenerator.apply(initialSize);
            algorithm.accept(warmupInput);
        }

        List<Integer> sizes = new ArrayList<>();
        List<Long> timesNanos = new ArrayList<>();

        // 2. Empirical Measurement Phase across doubled input sizes
        int currentSize = initialSize;
        for (int step = 0; step < steps; step++) {
            List<Long> sampleTimes = new ArrayList<>();

            for (int iter = 0; iter < measurementIterations; iter++) {
                T input = inputGenerator.apply(currentSize);
                long start = System.nanoTime();
                algorithm.accept(input);
                long elapsed = System.nanoTime() - start;
                sampleTimes.add(elapsed);
            }

            // Use median to filter out transient GC pauses or OS thread preemption
            Collections.sort(sampleTimes);
            long medianTime = sampleTimes.get(sampleTimes.size() / 2);

            sizes.add(currentSize);
            timesNanos.add(medianTime);

            currentSize *= 2;
        }

        // 3. Compute Doubling Ratios: R = T(2N) / T(N)
        List<Double> ratios = new ArrayList<>();
        double totalLog2Ratio = 0.0;
        int validRatioCount = 0;

        for (int i = 1; i < timesNanos.size(); i++) {
            double prevTime = Math.max(1.0, (double) timesNanos.get(i - 1));
            double currTime = Math.max(1.0, (double) timesNanos.get(i));
            double ratio = currTime / prevTime;
            ratios.add(ratio);

            if (ratio > 0.1) {
                double exponent = Math.log(ratio) / Math.log(2.0);
                totalLog2Ratio += exponent;
                validRatioCount++;
            }
        }

        double estimatedExponent = validRatioCount > 0 ? (totalLog2Ratio / validRatioCount) : 0.0;
        ComplexityOrder classifiedOrder = ComplexityOrder.fromExponent(estimatedExponent);

        ComplexityAssessment assessment = new ComplexityAssessment(
                sizes,
                timesNanos,
                ratios,
                estimatedExponent,
                classifiedOrder
        );

        System.out.println(assessment.formatReport());
        return assessment;
    }
}
