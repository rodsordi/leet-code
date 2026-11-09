package br.com.leetcode.support.complexity;

import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares a test method as an empirical algorithmic complexity assessment.
 * The annotated method should accept a single int or Integer parameter representing the input size.
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@TestTemplate
@ExtendWith(ComplexityTestExtension.class)
public @interface ComplexityTest {

    /**
     * Expected asymptotic complexity order (e.g. LINEAR, QUADRATIC).
     */
    ComplexityOrder expected() default ComplexityOrder.LINEAR;

    /**
     * Initial input size for the doubling sequence (default: 20,000).
     */
    int initialSize() default 20_000;

    /**
     * Number of doubling steps to perform (default: 3, producing N, 2N, 4N).
     */
    int steps() default 3;

    /**
     * Permissible tolerance for the estimated polynomial exponent (default: 0.45).
     */
    double tolerance() default 0.45;

    /**
     * Whether to execute an initial warmup invocation to stabilize JIT compilation.
     */
    boolean warmup() default true;
}
