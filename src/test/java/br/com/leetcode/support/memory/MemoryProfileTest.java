package br.com.leetcode.support.memory;

import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares a test method as an empirical memory profiling assessment.
 * Supports zero-allocation assertions on parameterless methods, as well as
 * auxiliary space complexity assessments on methods accepting an int size parameter.
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@TestTemplate
@ExtendWith(MemoryProfileTestExtension.class)
public @interface MemoryProfileTest {

    /**
     * Expected memory allocation category (default: ZERO).
     */
    MemoryAllocation expectedAllocation() default MemoryAllocation.ZERO;

    /**
     * Maximum permissible allocated heap bytes (default: 0).
     * Used when expectedAllocation is ZERO or CONSTANT.
     */
    long maxAllocatedBytes() default 0L;

    /**
     * Initial input size for doubling sequence (default: 20,000) when method accepts (int size).
     */
    int initialSize() default 20_000;

    /**
     * Number of doubling steps to perform (default: 1 for parameterless, 3 for parameterized).
     */
    int steps() default 1;

    /**
     * Whether to execute an initial warmup invocation to stabilize JIT compilation.
     */
    boolean warmup() default true;

    /**
     * Number of warmup iterations to execute before measuring.
     */
    int warmupIterations() default 15;
}
