package br.com.leetcode.support.benchmark;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a test class or test method to measure execution wall time, CPU time, and allocated memory.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ExtendWith(TestBenchmarkExtension.class)
public @interface BenchmarkTest {

    /**
     * Maximum tolerated execution time in milliseconds before flagging an SLA / TLE violation.
     * Default is 0 (disabled).
     */
    long maxTimeMillis() default 0;

    /**
     * Whether to fail the test execution when the elapsed time exceeds maxTimeMillis.
     * If false, logs a visible warning banner instead of failing.
     */
    boolean failOnSlaViolation() default false;

    /**
     * Maximum tolerated heap memory allocation in bytes before flagging an SLA violation.
     * Default is -1 (disabled).
     */
    long maxMemoryBytes() default -1;

    /**
     * Whether to fail the test execution when the allocated memory exceeds maxMemoryBytes.
     * If false, logs a visible warning banner instead of failing.
     */
    boolean failOnMemoryViolation() default false;

    /**
     * Whether to exclude warmup invocations from the final summary leaderboard table.
     */
    boolean excludeWarmup() default true;

    /**
     * Whether to use ANSI terminal colors in the console output for IDEs and modern terminals.
     */
    boolean ansiColors() default true;

    /**
     * Whether to print the formatted benchmark banner to System.out for individual tests.
     */
    boolean printToConsole() default true;

    /**
     * Whether to print a consolidated summary leaderboard table after all tests in the class finish.
     */
    boolean summaryTable() default true;
}
