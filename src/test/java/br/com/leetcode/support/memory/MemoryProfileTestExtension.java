package br.com.leetcode.support.memory;

import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

/**
 * JUnit 5 Extension powering the @MemoryProfileTest declarative annotation.
 * Orchestrates JIT warmup, thread-allocated heap memory profiling via ThreadMXBean,
 * and asserts zero-allocation or space complexity constraints with detailed ASCII reporting.
 */
public class MemoryProfileTestExtension implements TestTemplateInvocationContextProvider {

    private static final String MEASUREMENTS_KEY = "MEMORY_PROFILE_MEASUREMENTS";
    private static final ThreadMXBean THREAD_MX_BEAN = ManagementFactory.getThreadMXBean();
    private static final com.sun.management.ThreadMXBean SUN_THREAD_MX_BEAN = initSunThreadMxBean();

    private static com.sun.management.ThreadMXBean initSunThreadMxBean() {
        if (THREAD_MX_BEAN instanceof com.sun.management.ThreadMXBean sunBean) {
            try {
                if (sunBean.isThreadAllocatedMemorySupported() && !sunBean.isThreadAllocatedMemoryEnabled()) {
                    sunBean.setThreadAllocatedMemoryEnabled(true);
                }
            } catch (SecurityException | UnsupportedOperationException ignored) {
                // Ignore if security manager or JVM restrictions prevent modification
            }
            return sunBean;
        }
        return null;
    }

    private static long getThreadAllocatedBytes(long threadId) {
        if (SUN_THREAD_MX_BEAN != null && SUN_THREAD_MX_BEAN.isThreadAllocatedMemorySupported()) {
            return SUN_THREAD_MX_BEAN.getThreadAllocatedBytes(threadId);
        }
        return -1L;
    }

    @Override
    public boolean supportsTestTemplate(ExtensionContext context) {
        return AnnotationSupport.isAnnotated(context.getTestMethod(), MemoryProfileTest.class);
    }

    @Override
    public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {
        Method method = context.getRequiredTestMethod();
        MemoryProfileTest config = AnnotationSupport.findAnnotation(method, MemoryProfileTest.class)
                .orElseThrow(() -> new IllegalStateException("@MemoryProfileTest annotation missing"));

        boolean isParameterized = method.getParameterCount() == 1
                && (method.getParameterTypes()[0] == int.class || method.getParameterTypes()[0] == Integer.class);

        List<TestTemplateInvocationContext> contexts = new ArrayList<>();

        if (!isParameterized) {
            if (config.warmup()) {
                contexts.add(new MemoryInvocationContext(0, true, 0, 1, config, false));
            }
            contexts.add(new MemoryInvocationContext(0, false, 0, 1, config, false));
            return contexts.stream();
        }

        int totalSteps = config.steps() > 1 ? config.steps() : 3;
        int currentSize = config.initialSize();

        if (config.warmup()) {
            contexts.add(new MemoryInvocationContext(currentSize, true, 0, totalSteps, config, true));
        }

        for (int i = 0; i < totalSteps; i++) {
            contexts.add(new MemoryInvocationContext(currentSize, false, i, totalSteps, config, true));
            currentSize *= 2;
        }

        return contexts.stream();
    }

    private static class MemoryInvocationContext implements TestTemplateInvocationContext {

        private final int size;
        private final boolean isWarmup;
        private final int stepIndex;
        private final int totalSteps;
        private final MemoryProfileTest config;
        private final boolean hasSizeParameter;

        MemoryInvocationContext(
                int size,
                boolean isWarmup,
                int stepIndex,
                int totalSteps,
                MemoryProfileTest config,
                boolean hasSizeParameter) {
            this.size = size;
            this.isWarmup = isWarmup;
            this.stepIndex = stepIndex;
            this.totalSteps = totalSteps;
            this.config = config;
            this.hasSizeParameter = hasSizeParameter;
        }

        @Override
        public String getDisplayName(int invocationIndex) {
            if (isWarmup) {
                return hasSizeParameter
                        ? String.format(Locale.ROOT, "Warmup (N = %,d)", size)
                        : "Warmup (JIT Stabilization)";
            }
            if (!hasSizeParameter) {
                return "Heap Allocation Profile";
            }
            return String.format(Locale.ROOT, "[Step %d/%d] N = %,d", stepIndex + 1, totalSteps, size);
        }

        @Override
        public List<Extension> getAdditionalExtensions() {
            List<Extension> extensions = new ArrayList<>();
            if (hasSizeParameter) {
                extensions.add(new MemoryParameterResolver(size));
            }
            extensions.add(new MemoryInvocationInterceptor(size, isWarmup, stepIndex, totalSteps, config, hasSizeParameter));
            return extensions;
        }
    }

    private static class MemoryParameterResolver implements ParameterResolver {

        private final int size;

        MemoryParameterResolver(int size) {
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

    private record StepMeasurement(int size, long allocatedBytes) {}

    private static class MemoryInvocationInterceptor implements InvocationInterceptor {

        private final int size;
        private final boolean isWarmup;
        private final int stepIndex;
        private final int totalSteps;
        private final MemoryProfileTest config;
        private final boolean hasSizeParameter;

        MemoryInvocationInterceptor(
                int size,
                boolean isWarmup,
                int stepIndex,
                int totalSteps,
                MemoryProfileTest config,
                boolean hasSizeParameter) {
            this.size = size;
            this.isWarmup = isWarmup;
            this.stepIndex = stepIndex;
            this.totalSteps = totalSteps;
            this.config = config;
            this.hasSizeParameter = hasSizeParameter;
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
                // Warmup runs to trigger C2 JIT compilation and escape analysis
                for (int w = 0; w < config.warmupIterations(); w++) {
                    invokeSafely(method, target, args);
                }
                invocation.proceed();
                return;
            }

            // Measurement iterations: collect sample allocations and take minimum
            long threadId = Thread.currentThread().threadId();
            List<Long> samples = new ArrayList<>();

            for (int iter = 0; iter < 5; iter++) {
                long before = getThreadAllocatedBytes(threadId);
                invokeSafely(method, target, args);
                long after = getThreadAllocatedBytes(threadId);
                long delta = (before != -1L && after != -1L) ? Math.max(0L, after - before) : 0L;
                samples.add(delta);
            }

            Collections.sort(samples);
            long measuredAllocatedBytes = samples.get(0); // Take minimum to eliminate stray JVM background allocations

            recordAndAssert(extensionContext, size, measuredAllocatedBytes, stepIndex, totalSteps, config, hasSizeParameter);

            invocation.proceed();
        }

        private void invokeSafely(Method method, Object target, Object[] args) throws Throwable {
            try {
                method.invoke(target, args);
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        }

        private void recordAndAssert(
                ExtensionContext context,
                int size,
                long allocatedBytes,
                int stepIndex,
                int totalSteps,
                MemoryProfileTest config,
                boolean hasSizeParameter) {

            ExtensionContext.Namespace methodNamespace = ExtensionContext.Namespace.create(
                    MemoryProfileTestExtension.class,
                    context.getRequiredTestMethod()
            );

            @SuppressWarnings("unchecked")
            List<StepMeasurement> measurements = context.getRoot().getStore(methodNamespace).getOrComputeIfAbsent(
                    MEASUREMENTS_KEY,
                    k -> Collections.synchronizedList(new ArrayList<StepMeasurement>()),
                    List.class
            );

            measurements.add(new StepMeasurement(size, allocatedBytes));

            if (stepIndex == totalSteps - 1) {
                try {
                    evaluateAndAssert(context.getRequiredTestMethod().getName(), measurements, config, hasSizeParameter);
                } finally {
                    context.getRoot().getStore(methodNamespace).remove(MEASUREMENTS_KEY);
                }
            }
        }

        private void evaluateAndAssert(
                String testName,
                List<StepMeasurement> measurements,
                MemoryProfileTest config,
                boolean hasSizeParameter) {

            List<Integer> sizes = new ArrayList<>();
            List<Long> allocatedBytesList = new ArrayList<>();

            for (StepMeasurement m : measurements) {
                if (hasSizeParameter) {
                    sizes.add(m.size());
                }
                allocatedBytesList.add(m.allocatedBytes());
            }

            long finalAllocatedBytes = allocatedBytesList.get(allocatedBytesList.size() - 1);
            boolean passed;
            String failureReason = "";
            MemoryAllocation classified;

            if (config.expectedAllocation() == MemoryAllocation.ZERO) {
                passed = finalAllocatedBytes <= config.maxAllocatedBytes();
                classified = finalAllocatedBytes == 0 ? MemoryAllocation.ZERO : MemoryAllocation.CONSTANT;
                if (!passed) {
                    failureReason = String.format(Locale.ROOT, "Expected 0 bytes allocated, but measured %d bytes", finalAllocatedBytes);
                }
            } else if (config.expectedAllocation() == MemoryAllocation.CONSTANT) {
                long maxBudget = config.maxAllocatedBytes() > 0 ? config.maxAllocatedBytes() : 1024L;
                passed = finalAllocatedBytes <= maxBudget;
                classified = MemoryAllocation.CONSTANT;
                if (!passed) {
                    failureReason = String.format(Locale.ROOT, "Exceeded constant budget of %d bytes (measured %d bytes)", maxBudget, finalAllocatedBytes);
                }
            } else { // LINEAR
                classified = MemoryAllocation.LINEAR;
                passed = true;
                if (allocatedBytesList.size() >= 2) {
                    long first = Math.max(1L, allocatedBytesList.get(0));
                    long last = allocatedBytesList.get(allocatedBytesList.size() - 1);
                    double growth = (double) last / first;
                    if (growth < 1.2) {
                        passed = false;
                        failureReason = String.format(Locale.ROOT, "Expected linear growth, but memory remained constant (growth: %.2fx)", growth);
                        classified = MemoryAllocation.CONSTANT;
                    }
                }
            }

            MemoryAssessment assessment = new MemoryAssessment(
                    testName,
                    sizes,
                    allocatedBytesList,
                    classified,
                    passed,
                    failureReason
            );

            System.out.println(assessment.formatReport());

            if (!passed) {
                throw new AssertionError(String.format(
                        Locale.ROOT,
                        """
                        Memory profile assertion failed for @MemoryProfileTest:
                        Expected: %s
                        Measured: %s
                        Reason:   %s
                        %s""",
                        config.expectedAllocation().description(),
                        MemoryAssessment.formatBytes(finalAllocatedBytes),
                        failureReason,
                        assessment.formatReport()
                ));
            }
        }
    }
}
