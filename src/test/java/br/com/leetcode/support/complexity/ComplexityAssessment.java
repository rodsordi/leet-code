package br.com.leetcode.support.complexity;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Immutable assessment results produced by empirical scaling evaluation.
 *
 * @param sizes             List of input sizes tested (e.g. [1000, 2000, 4000])
 * @param timesNanos        Average execution time in nanoseconds for each size
 * @param ratios            Calculated doubling ratios T(2N) / T(N)
 * @param estimatedExponent Estimated polynomial exponent b = log2(averageRatio)
 * @param classifiedOrder   Classified ComplexityOrder based on estimated exponent
 */
public record ComplexityAssessment(
        List<Integer> sizes,
        List<Long> timesNanos,
        List<Double> ratios,
        double estimatedExponent,
        ComplexityOrder classifiedOrder
) {

    public ComplexityAssessment {
        sizes = List.copyOf(Objects.requireNonNull(sizes, "sizes cannot be null"));
        timesNanos = List.copyOf(Objects.requireNonNull(timesNanos, "timesNanos cannot be null"));
        ratios = List.copyOf(Objects.requireNonNull(ratios, "ratios cannot be null"));
        Objects.requireNonNull(classifiedOrder, "classifiedOrder cannot be null");
    }

    public String formatReport() {
        var sb = new StringBuilder();
        sb.append("\n========================================================================\n");
        sb.append("                  EMPIRICAL COMPLEXITY ASSESSMENT REPORT                \n");
        sb.append("========================================================================\n");
        sb.append(String.format(Locale.ROOT, "%-12s | %-18s | %-12s\n", "Size (N)", "Avg Time (ms)", "Doubling Ratio"));
        sb.append("------------------------------------------------------------------------\n");

        for (int i = 0; i < sizes.size(); i++) {
            double timeMs = timesNanos.get(i) / 1_000_000.0;
            String ratioStr = (i == 0 || i - 1 >= ratios.size())
                    ? "baseline"
                    : String.format(Locale.ROOT, "%.2fx", ratios.get(i - 1));
            sb.append(String.format(Locale.ROOT, "%-12d | %-18.4f | %-12s\n", sizes.get(i), timeMs, ratioStr));
        }

        sb.append("------------------------------------------------------------------------\n");
        sb.append(String.format(Locale.ROOT, "Estimated Big-O Exponent: %.2f (Expected ~2.00 for O(N^2), ~1.00 for O(N))\n", estimatedExponent));
        sb.append(String.format(Locale.ROOT, "Classified Complexity:    %s (%s)\n", classifiedOrder.notation(), classifiedOrder.name()));
        sb.append("========================================================================\n");
        return sb.toString();
    }
}
