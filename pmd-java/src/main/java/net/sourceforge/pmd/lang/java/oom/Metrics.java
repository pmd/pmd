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
import net.sourceforge.pmd.lang.java.oom.api.ResultOption;


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
    /* default */
    static PackageStats getTopLevelPackageStats() {
        return TOP_LEVEL_PACKAGE;
    }


    /**
     * Computes the standard value of the metric identified by its code on a class AST node.
     *
     * @param key  The key identifying the metric to be computed
     * @param node The node on which to compute the metric
     *
     * @return The value of the metric, or {@code Double.NaN} if the value couln't be computed.
     */
    public static double get(ClassMetricKey key, ASTClassOrInterfaceDeclaration node) {
        return get(key, node, Version.STANDARD);
    }


    /**
     * Computes a metric identified by its code on a class AST node, possibly selecting a variant with the {@code
     * MetricVersion} parameter.
     *
     * @param key     The key identifying the metric to be computed
     * @param node    The node on which to compute the metric
     * @param version The version of the metric.
     *
     * @return The value of the metric, or {@code Double.NaN} if the value couln't be computed.
     */
    public static double get(ClassMetricKey key, ASTClassOrInterfaceDeclaration node, MetricVersion version) {
        if (!key.getCalculator().supports(node)) {
            return Double.NaN;
        }

        MetricVersion safeVersion = (version == null) ? Version.STANDARD : version;

        return TOP_LEVEL_PACKAGE.compute(key, node, false, safeVersion);
    }

    /**
     * Computes the standard version of the metric identified by the key on a operation AST node.
     *
     * @param key  The key identifying the metric to be computed
     * @param node The node on which to compute the metric
     *
     * @return The value of the metric, or {@code Double.NaN} if the value couln't be computed.
     */
    public static double get(OperationMetricKey key, ASTMethodOrConstructorDeclaration node) {
        return get(key, node, Version.STANDARD);
    }

    /**
     * Computes a metric identified by its key on a operation AST node.
     *
     * @param key     The key identifying the metric to be computed
     * @param node    The node on which to compute the metric
     * @param version The version of the metric.
     *
     * @return The value of the metric, or {@code Double.NaN} if the value couln't be computed.
     */
    public static double get(OperationMetricKey key, ASTMethodOrConstructorDeclaration node, MetricVersion version) {
        if (!key.getCalculator().supports(node)) {
            return Double.NaN;
        }

        MetricVersion safeVersion = (version == null) ? Version.STANDARD : version;

        return TOP_LEVEL_PACKAGE.compute(key, node, false, safeVersion);
    }

    /**
     * Compute the sum, average, or highest value of the operation metric on all operations of the class node. The
     * result to aggregate is specified by the {@link ResultOption} parameter.
     *
     * @param key     The key identifying the metric to be computed
     * @param node    The node on which to compute the metric
     * @param version The version of the metric.
     * @param option  The result option to use.
     *
     * @return The value of the metric, or {@code Double.NaN} if the value couln't be computed or {@code option} is
     * {@literal null}.
     */
    public static double get(OperationMetricKey key, ASTClassOrInterfaceDeclaration node, MetricVersion version, ResultOption option) {

        MetricVersion safeVersion = (version == null) ? Version.STANDARD : version;
        return option == null ? Double.NaN
                              : TOP_LEVEL_PACKAGE.computeWithResultOption(key, node, false, safeVersion, option);
    }
}
