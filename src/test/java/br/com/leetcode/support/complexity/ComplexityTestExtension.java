package br.com.leetcode.support.complexity;

import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

/**
 * JUnit 5 Extension that powers the @ComplexityTest declarative annotation.
 * Orchestrates warmup, input size generation, timing measurement with median sampling,
 * ratio computation, and automated Big-O assertion.
 */
public class ComplexityTestExtension implements TestTemplateInvocationContextProvider {

    private static final String MEASUREMENTS_KEY = "COMPLEXITY_MEASUREMENTS";

    @Override
    public boolean supportsTestTemplate(ExtensionContext context) {
        return AnnotationSupport.isAnnotated(context.getTestMethod(), ComplexityTest.class);
    }

    @Override
    public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {
        Method method = context.getRequiredTestMethod();
        ComplexityTest config = AnnotationSupport.findAnnotation(method, ComplexityTest.class)
                .orElseThrow(() -> new IllegalStateException("@ComplexityTest annotation missing"));

        List<TestTemplateInvocationContext> contexts = new ArrayList<>();

        if (config.warmup()) {
            contexts.add(new ComplexityInvocationContext(config.initialSize(), true, 0, config.steps(), config));
        }

        int currentSize = config.initialSize();
        for (int i = 0; i < config.steps(); i++) {
            contexts.add(new ComplexityInvocationContext(currentSize, false, i, config.steps(), config));
            currentSize *= 2;
        }

        return contexts.stream();
    }

    private static class ComplexityInvocationContext implements TestTemplateInvocationContext {

        private final int size;
        private final boolean isWarmup;
        private final int stepIndex;
        private final int totalSteps;
        private final ComplexityTest config;

        ComplexityInvocationContext(int size, boolean isWarmup, int stepIndex, int totalSteps, ComplexityTest config) {
            this.size = size;
            this.isWarmup = isWarmup;
            this.stepIndex = stepIndex;
            this.totalSteps = totalSteps;
            this.config = config;
        }

        @Override
        public String getDisplayName(int invocationIndex) {
            if (isWarmup) {
                return String.format(Locale.ROOT, "Warmup (N = %,d)", size);
            }
            return String.format(Locale.ROOT, "[Step %d/%d] N = %,d", stepIndex + 1, totalSteps, size);
        }

        @Override
        public List<Extension> getAdditionalExtensions() {
            return List.of(
                    new ComplexityParameterResolver(size),
                    new ComplexityInvocationInterceptor(size, isWarmup, stepIndex, totalSteps, config)
            );
        }
    }

    private static class ComplexityParameterResolver implements ParameterResolver {

        private final int size;

        ComplexityParameterResolver(int size) {
            this.size = size;
        }

        @Override
        public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
            Class<?> type = parameterContext.getParameter().getType();
            return type == int.class || type == Integer.class;
        }

        @Override
        public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
            return size;
        }
    }

    private record StepMeasurement(int size, long elapsedNanos) {}

    private static class ComplexityInvocationInterceptor implements InvocationInterceptor {

        private final int size;
        private final boolean isWarmup;
        private final int stepIndex;
        private final int totalSteps;
        private final ComplexityTest config;

        ComplexityInvocationInterceptor(int size, boolean isWarmup, int stepIndex, int totalSteps, ComplexityTest config) {
            this.size = size;
            this.isWarmup = isWarmup;
            this.stepIndex = stepIndex;
            this.totalSteps = totalSteps;
            this.config = config;
        }

        @Override
        public void interceptTestTemplateMethod(
                Invocation<Void> invocation,
                ReflectiveInvocationContext<Method> invocationContext,
                ExtensionContext extensionContext) throws Throwable {

            Method method = invocationContext.getExecutable();
            method.setAccessible(true);
            Object target = invocationContext.getTarget().orElse(null);
            Object[] args = invocationContext.getArguments().toArray();

            if (isWarmup) {
                // Warmup iterations to trigger Tier-4 C2 JIT compilation
                for (int w = 0; w < 15; w++) {
                    invokeSafely(method, target, args);
                }
                invocation.proceed();
                return;
            }

            // Measurement iterations: collect sample times and take the median
            List<Long> samples = new ArrayList<>();
            for (int iter = 0; iter < 5; iter++) {
                long start = System.nanoTime();
                invokeSafely(method, target, args);
                long elapsed = System.nanoTime() - start;
                samples.add(elapsed);
            }
            Collections.sort(samples);
            long medianElapsed = samples.get(samples.size() / 2);

            recordAndAssert(extensionContext, size, medianElapsed, stepIndex, totalSteps, config);

            invocation.proceed();
        }

        private void invokeSafely(Method method, Object target, Object[] args) throws Throwable {
            try {
                method.invoke(target, args);
            } catch (java.lang.reflect.InvocationTargetException e) {
                throw e.getTargetException();
            }
        }

        private void recordAndAssert(
                ExtensionContext context,
                int size,
                long elapsed,
                int stepIndex,
                int totalSteps,
                ComplexityTest config) {

            ExtensionContext.Namespace methodNamespace = ExtensionContext.Namespace.create(
                    ComplexityTestExtension.class,
                    context.getRequiredTestMethod()
            );

            @SuppressWarnings("unchecked")
            List<StepMeasurement> measurements = context.getRoot().getStore(methodNamespace).getOrComputeIfAbsent(
                    MEASUREMENTS_KEY,
                    k -> Collections.synchronizedList(new ArrayList<StepMeasurement>()),
                    List.class
            );

            measurements.add(new StepMeasurement(size, elapsed));

            // On the final step, calculate doubling hypothesis ratios and validate assertion
            if (stepIndex == totalSteps - 1) {
                try {
                    evaluateAndAssert(measurements, config);
                } finally {
                    context.getRoot().getStore(methodNamespace).remove(MEASUREMENTS_KEY);
                }
            }
        }

        private void evaluateAndAssert(List<StepMeasurement> measurements, ComplexityTest config) {
            List<Integer> sizes = new ArrayList<>();
            List<Long> timesNanos = new ArrayList<>();
            List<Double> ratios = new ArrayList<>();

            for (StepMeasurement m : measurements) {
                sizes.add(m.size());
                timesNanos.add(m.elapsedNanos());
            }

            double totalLog2Ratio = 0.0;
            int validCount = 0;

            for (int i = 1; i < timesNanos.size(); i++) {
                double prev = Math.max(1.0, (double) timesNanos.get(i - 1));
                double curr = Math.max(1.0, (double) timesNanos.get(i));
                double ratio = curr / prev;
                ratios.add(ratio);

                if (ratio > 0.1) {
                    totalLog2Ratio += (Math.log(ratio) / Math.log(2.0));
                    validCount++;
                }
            }

            double estimatedExponent = validCount > 0 ? (totalLog2Ratio / validCount) : 0.0;
            ComplexityOrder classifiedOrder = ComplexityOrder.fromExponent(estimatedExponent);

            ComplexityAssessment assessment = new ComplexityAssessment(
                    sizes,
                    timesNanos,
                    ratios,
                    estimatedExponent,
                    classifiedOrder
            );

            System.out.println(assessment.formatReport());

            double expectedExponent = config.expected().expectedExponent();
            if (Math.abs(estimatedExponent - expectedExponent) > config.tolerance()) {
                throw new AssertionError(String.format(
                        Locale.ROOT,
                        """
                        Algorithmic complexity assertion failed for @ComplexityTest:
                        Expected: %s (exponent ~%.2f +/- %.2f)
                        Measured: %.2f (classified as %s)
                        %s""",
                        config.expected().notation(),
                        expectedExponent,
                        config.tolerance(),
                        estimatedExponent,
                        classifiedOrder.notation(),
                        assessment.formatReport()
                ));
            }
        }
    }
}
