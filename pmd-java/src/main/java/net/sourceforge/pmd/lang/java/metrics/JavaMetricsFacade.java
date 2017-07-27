/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaQualifiedName;
import net.sourceforge.pmd.lang.metrics.api.Metric.Version;
import net.sourceforge.pmd.lang.metrics.api.MetricKey;
import net.sourceforge.pmd.lang.metrics.api.MetricVersion;
import net.sourceforge.pmd.lang.metrics.api.ResultOption;

/**
 * Facade of the Java metrics framework.
 *
 * @author Clément Fournier
 */
class JavaMetricsFacade {

    private final PackageStats topLevelPackageStats = new PackageStats();


    /**
     * Returns the top level package statistics.
     *
     * @return The top level package stats
     */
    PackageStats getTopLevelPackageStats() {
        return topLevelPackageStats;
    }


    /** Resets the entire data structure. Used for tests. */
    void reset() {
        topLevelPackageStats.reset();
    }


    /**
     * Computes a metric identified by its code on a class AST node, possibly selecting a variant with the {@code
     * MetricVersion} parameter.
     *
     * @param key     The key identifying the metric to be computed
     * @param node    The node on which to compute the metric
     * @param version The version of the metric
     *
     * @return The value of the metric, or {@code Double.NaN} if the value couln't be computed
     */
    public double computeForType(MetricKey<ASTAnyTypeDeclaration> key, ASTAnyTypeDeclaration node, MetricVersion version) {

        if (!key.supports(node)) {
            return Double.NaN;
        }

        MetricVersion safeVersion = (version == null) ? Version.STANDARD : version;
        ClassStats memoizer = topLevelPackageStats.getClassStats(node.getQualifiedName(), false);

        return memoizer == null ? Double.NaN
                                : JavaMetricsComputer.INSTANCE.compute(key, node, false, safeVersion, memoizer);
    }


    /**
     * Computes a metric identified by its key on a operation AST node.
     *
     * @param key     The key identifying the metric to be computed
     * @param node    The node on which to compute the metric
     * @param version The version of the metric
     *
     * @return The value of the metric, or {@code Double.NaN} if the value couln't be computed
     */
    public double computeForOperation(MetricKey<ASTMethodOrConstructorDeclaration> key, ASTMethodOrConstructorDeclaration node,
                                      MetricVersion version) {

        if (!key.supports(node)) {
            return Double.NaN;
        }

        MetricVersion safeVersion = (version == null) ? Version.STANDARD : version;

        JavaQualifiedName qname = node.getQualifiedName();
        ClassStats container = topLevelPackageStats.getClassStats(qname, false);
        OperationStats memoizer = container == null ? null
                                                    : container.getOperationStats(qname.getOperation(), node.getSignature());

        return memoizer == null ? Double.NaN
                                : JavaMetricsComputer.INSTANCE.compute(key, node, false, safeVersion, memoizer);

    }


    /**
     * Compute the sum, average, or highest value of the operation metric on all operations of the class node. The type
     * of operation is specified by the {@link ResultOption} parameter.
     *
     * @param key     The key identifying the metric to be computed
     * @param node    The node on which to compute the metric
     * @param version The version of the metric
     * @param option  The result option to use
     *
     * @return The value of the metric, or {@code Double.NaN} if the value couln't be computed or {@code option} is
     * {@code null}
     */
    public double computeWithResultOption(MetricKey<ASTMethodOrConstructorDeclaration> key, ASTAnyTypeDeclaration node,
                                          MetricVersion version, ResultOption option) {

        if (option == null) {
            throw new IllegalArgumentException("The result option may not be null");
        }

        MetricVersion safeVersion = (version == null) ? Version.STANDARD : version;
        ClassStats memoizer = topLevelPackageStats.getClassStats(node.getQualifiedName(), false);

        return memoizer == null ? Double.NaN
                                : JavaMetricsComputer.INSTANCE.computeWithResultOption(key, node, false, safeVersion,
                                                                                       option, memoizer);
    }
}
