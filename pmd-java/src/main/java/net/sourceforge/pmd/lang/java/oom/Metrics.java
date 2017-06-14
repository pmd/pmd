/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom;


import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.oom.Metric.Option;
import net.sourceforge.pmd.lang.java.oom.metrics.AtfdMetric;
import net.sourceforge.pmd.lang.java.oom.metrics.CycloMetric;
import net.sourceforge.pmd.lang.java.oom.metrics.WmcMetric;


/**
 * User bound façade of the Metrics Framework. Provides a uniform interface for the calculation of metrics.
 *
 * @author Clément Fournier
 */
public final class Metrics {

    private static final PackageStats TOP_LEVEL_PACKAGE = new PackageStats();

    private Metrics() { // Cannot be instantiated

    }

    /**
     * Returns the top level package stats.
     *
     * @return The top level package stats.
     */
    static PackageStats getTopLevelPackageStats() {
        return TOP_LEVEL_PACKAGE;
    }

    /**
     * Computes a metric identified by its code on a class AST node.
     *
     * @param key  The key identifying the metric to be computed
     * @param node The node on which to compute the metric
     *
     * @return The value of the metric, or {@code Double.NaN} if the value couln't be computed.
     */
    public static double get(ClassMetricKey key, ASTClassOrInterfaceDeclaration node) {
        // TODO:cf think about caching
        return TOP_LEVEL_PACKAGE.compute(key, node, false, Option.STANDARD);
    }

    /**
     * Computes a metric identified by its code on a operation AST node.
     *
     * @param key  The key identifying the metric to be computed
     * @param node The node on which to compute the metric
     *
     * @return The value of the metric, or {@code Double.NaN} if the value couln't be computed.
     */
    public static double get(OperationMetricKey key, ASTMethodOrConstructorDeclaration node) {
        // TODO:cf think about caching
        return TOP_LEVEL_PACKAGE.compute(key, node, false, Option.STANDARD);
    }

    public static double get(OperationMetricKey key, ASTMethodOrConstructorDeclaration node, MetricOption option) {
        MetricOption safeOption = (option == null) ? Option.STANDARD : option;

        return TOP_LEVEL_PACKAGE.compute(key, node, false, safeOption);
    }

    public static double get(ClassMetricKey key, ASTClassOrInterfaceDeclaration node, MetricOption option) {
        MetricOption safeOption = (option == null) ? Option.STANDARD : option;

        return TOP_LEVEL_PACKAGE.compute(key, node, false, safeOption);
    }

    /**
     * Keys identifying class metrics.
     */
    public enum ClassMetricKey implements MetricKey {
        /** Access to Foreign Data. */
        ATFD(new AtfdMetric()),
        /** Weighed Method Count. */
        WMC(new WmcMetric()),
        /** Cyclometric complexity. */
        CYCLO(new CycloMetric());


        private final ClassMetric calculator;

        ClassMetricKey(ClassMetric m) {
            calculator = m;
        }

        /** Returns the object used to calculate the metric. @return The calculator. */
        ClassMetric getCalculator() {
            return calculator;
        }
    }

    /**
     * Keys identifying operation metrics.
     */
    public enum OperationMetricKey implements MetricKey {

        /** Access to Foreign Data. */ // TODO:cf add short description here for javadoc hints
        ATFD(new AtfdMetric()),
        /** Cyclometric complexity. */
        CYCLO(new CycloMetric());

        private final OperationMetric calculator;

        OperationMetricKey(OperationMetric m) {
            calculator = m;
        }

        /** Returns the object used to calculate the metric. @return The calculator. */
        OperationMetric getCalculator() {
            return calculator;
        }
    }

    /**
     * Key identifying a metric.
     */
    public interface MetricKey {

        /**
         * Returns the name of the metric.
         *
         * @return The name of the metric.
         */
        String name();

    }
}
