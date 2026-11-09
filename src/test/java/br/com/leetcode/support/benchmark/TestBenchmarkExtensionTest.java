package br.com.leetcode.support.benchmark;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Test Benchmark Extension Infrastructure")
class TestBenchmarkExtensionTest {

    @Nested
    @DisplayName("BenchmarkMetrics Formatting Tests")
    class MetricsFormattingTests {

        @Test
        @DisplayName("Should format elapsed time across nanoseconds, microseconds, milliseconds, and seconds")
        void shouldFormatElapsedTime() {
            var nanosOnly = new BenchmarkMetrics(500, 0, 0);
            assertThat(nanosOnly.formatElapsedTime()).isEqualTo("500 ns");

            var micros = new BenchmarkMetrics(45_500, 0, 0);
            assertThat(micros.formatElapsedTime()).isEqualTo("45.50 us (45500 ns)");

            var millis = new BenchmarkMetrics(12_345_678, 0, 0);
            assertThat(millis.formatElapsedTime()).isEqualTo("12.346 ms (12345678 ns)");

            var seconds = new BenchmarkMetrics(2_500_000_000L, 0, 0);
            assertThat(seconds.formatElapsedTime()).isEqualTo("2.50 s (2500.000 ms)");
        }

        @Test
        @DisplayName("Should format short elapsed time without nanosecond suffix")
        void shouldFormatShortElapsedTime() {
            assertThat(new BenchmarkMetrics(500, 0, 0).formatShortElapsedTime()).isEqualTo("500 ns");
            assertThat(new BenchmarkMetrics(45_500, 0, 0).formatShortElapsedTime()).isEqualTo("45.50 us");
            assertThat(new BenchmarkMetrics(12_345_678, 0, 0).formatShortElapsedTime()).isEqualTo("12.346 ms");
            assertThat(new BenchmarkMetrics(2_500_000_000L, 0, 0).formatShortElapsedTime()).isEqualTo("2.50 s");
        }

        @Test
        @DisplayName("Should format CPU time or handle unsupported status across milliseconds and seconds")
        void shouldFormatCpuTime() {
            var unsupported = new BenchmarkMetrics(1_000_000, -1, 0);
            assertThat(unsupported.formatCpuTime()).contains("N/A");

            var validNanos = new BenchmarkMetrics(1_000_000, 800, 0);
            assertThat(validNanos.formatCpuTime()).isEqualTo("800 ns");

            var validMillis = new BenchmarkMetrics(10_000_000, 5_500_000, 0);
            assertThat(validMillis.formatCpuTime()).isEqualTo("5.500 ms (5500000 ns)");

            var validSeconds = new BenchmarkMetrics(10_000_000_000L, 3_250_000_000L, 0);
            assertThat(validSeconds.formatCpuTime()).isEqualTo("3.25 s (3250.000 ms)");
        }

        @Test
        @DisplayName("Should format short CPU time without extra suffix")
        void shouldFormatShortCpuTime() {
            assertThat(new BenchmarkMetrics(1_000_000, -1, 0).formatShortCpuTime()).isEqualTo("N/A");
            assertThat(new BenchmarkMetrics(1_000_000, 800, 0).formatShortCpuTime()).isEqualTo("800 ns");
            assertThat(new BenchmarkMetrics(1_000_000, 50_000, 0).formatShortCpuTime()).isEqualTo("50.00 us");
            assertThat(new BenchmarkMetrics(10_000_000, 5_500_000, 0).formatShortCpuTime()).isEqualTo("5.500 ms");
            assertThat(new BenchmarkMetrics(10_000_000_000L, 3_250_000_000L, 0).formatShortCpuTime()).isEqualTo("3.25 s");
        }

        @Test
        @DisplayName("Should format allocated memory across Bytes, KB, MB, and GB")
        void shouldFormatAllocatedMemory() {
            var unsupported = new BenchmarkMetrics(1_000, 0, -1);
            assertThat(unsupported.formatAllocatedMemory()).contains("N/A");

            var bytes = new BenchmarkMetrics(1_000, 0, 512);
            assertThat(bytes.formatAllocatedMemory()).isEqualTo("512 B");

            var kiloBytes = new BenchmarkMetrics(1_000, 0, 2048);
            assertThat(kiloBytes.formatAllocatedMemory()).isEqualTo("2.00 KB (2048 bytes)");

            var megaBytes = new BenchmarkMetrics(1_000, 0, 2 * 1024 * 1024);
            assertThat(megaBytes.formatAllocatedMemory()).isEqualTo("2.00 MB (2097152 bytes)");

            var gigaBytes = new BenchmarkMetrics(1_000, 0, 3L * 1024 * 1024 * 1024);
            assertThat(gigaBytes.formatAllocatedMemory()).isEqualTo("3.00 GB (3221225472 bytes)");
        }

        @Test
        @DisplayName("Should format short allocated memory")
        void shouldFormatShortAllocatedMemory() {
            assertThat(new BenchmarkMetrics(1_000, 0, -1).formatShortAllocatedMemory()).isEqualTo("N/A");
            assertThat(new BenchmarkMetrics(1_000, 0, 512).formatShortAllocatedMemory()).isEqualTo("512 B");
            assertThat(new BenchmarkMetrics(1_000, 0, 2048).formatShortAllocatedMemory()).isEqualTo("2.00 KB");
            assertThat(new BenchmarkMetrics(1_000, 0, 2 * 1024 * 1024).formatShortAllocatedMemory()).isEqualTo("2.00 MB");
            assertThat(new BenchmarkMetrics(1_000, 0, 3L * 1024 * 1024 * 1024).formatShortAllocatedMemory()).isEqualTo("3.00 GB");
        }
    }

    @Nested
    @BenchmarkTest
    @DisplayName("Extension Integration Execution")
    class ExtensionIntegrationTest {

        @Test
        @DisplayName("Should execute annotated test allocating memory and verify no extension exceptions")
        void shouldExecuteTestWithBenchmarkAnnotation() {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < 10_000; i++) {
                list.add("Item-" + i);
            }

            assertThat(list).hasSize(10_000);
        }

        @ParameterizedTest(name = "Invoking parameterized iteration with batchSize={0}")
        @ValueSource(ints = {100, 500, 1000})
        @DisplayName("Parameterized test safe uniqueId execution")
        void shouldExecuteParameterizedTestsSafely(int batchSize) {
            List<Integer> list = new ArrayList<>(batchSize);
            for (int i = 0; i < batchSize; i++) {
                list.add(i);
            }

            assertThat(list).hasSize(batchSize);
        }

        @Test
        @BenchmarkTest(maxTimeMillis = 5, failOnSlaViolation = false)
        @DisplayName("Should flag SLA warning without failing test when threshold is exceeded")
        void shouldFlagSlaWarningWithoutFailing() throws InterruptedException {
            Thread.sleep(10);
            assertThat(true).isTrue();
        }
    }
}
