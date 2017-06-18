/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom;


import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.oom.api.ClassMetricKey;
import net.sourceforge.pmd.lang.java.oom.api.Metric.Version;
import net.sourceforge.pmd.lang.java.oom.api.MetricVersion;
import net.sourceforge.pmd.lang.java.oom.api.OperationMetricKey;


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
    /* default */ static PackageStats getTopLevelPackageStats() {
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
        return TOP_LEVEL_PACKAGE.compute(key, node, false, Version.STANDARD);
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
        return TOP_LEVEL_PACKAGE.compute(key, node, false, Version.STANDARD);
    }


    /**
     * Computes a metric identified by its code on a class AST node.
     *
     * @param key     The key identifying the metric to be computed
     * @param node    The node on which to compute the metric
     * @param version The version of the metric.
     *
     * @return The value of the metric, or {@code Double.NaN} if the value couln't be computed.
     */
    public static double get(ClassMetricKey key, ASTClassOrInterfaceDeclaration node, MetricVersion version) {
        MetricVersion safeOption = (version == null) ? Version.STANDARD : version;

        return TOP_LEVEL_PACKAGE.compute(key, node, false, safeOption);
    }


    /**
     * Computes a metric identified by its code on a operation AST node.
     *
     * @param key     The key identifying the metric to be computed
     * @param node    The node on which to compute the metric
     * @param version The version of the metric.
     *
     * @return The value of the metric, or {@code Double.NaN} if the value couln't be computed.
     */
    public static double get(OperationMetricKey key, ASTMethodOrConstructorDeclaration node, MetricVersion version) {
        MetricVersion safeOption = (version == null) ? Version.STANDARD : version;

        return TOP_LEVEL_PACKAGE.compute(key, node, false, safeOption);
    }
}
