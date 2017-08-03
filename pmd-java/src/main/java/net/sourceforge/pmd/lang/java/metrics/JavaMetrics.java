/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics;


import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.metrics.Metric.Version;
import net.sourceforge.pmd.lang.metrics.MetricKey;
import net.sourceforge.pmd.lang.metrics.MetricVersion;
import net.sourceforge.pmd.lang.metrics.ResultOption;


/**
 * User bound façade of the Metrics Framework. Provides a uniform interface for the calculation of metrics.
 *
 * @author Clément Fournier
 */
public final class JavaMetrics {

    private static final JavaMetricsFacade FACADE = new JavaMetricsFacade();


    private JavaMetrics() { // Cannot be instantiated

    }


    /**
     * Returns the project mirror of the analysed project.
     *
     * @return The project mirror
     */
    static PackageStats getTopLevelPackageStats() {
        return FACADE.getLanguageSpecificProjectMemoizer();
    }


    /** Resets the entire data structure. Used for tests. */
    static void reset() {
        FACADE.reset();
    }


    /**
     * Computes the standard value of the metric identified by its code on a class AST node.
     *
     * @param key  The key identifying the metric to be computed
     * @param node The node on which to compute the metric
     *
     * @return The value of the metric, or {@code Double.NaN} if the value couln't be computed
     */
    public static double get(MetricKey<ASTAnyTypeDeclaration> key, ASTAnyTypeDeclaration node) {
        return FACADE.computeForType(key, node, Version.STANDARD);
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
    public static double get(MetricKey<ASTAnyTypeDeclaration> key, ASTAnyTypeDeclaration node, MetricVersion version) {
        return FACADE.computeForType(key, node, version);
    }


    /**
     * Computes the standard version of the metric identified by the key on a operation AST node.
     *
     * @param key  The key identifying the metric to be computed
     * @param node The node on which to compute the metric
     *
     * @return The value of the metric, or {@code Double.NaN} if the value couln't be computed
     */
    public static double get(MetricKey<ASTMethodOrConstructorDeclaration> key, ASTMethodOrConstructorDeclaration node) {
        return FACADE.computeForOperation(key, node, Version.STANDARD);
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
    public static double get(MetricKey<ASTMethodOrConstructorDeclaration> key, ASTMethodOrConstructorDeclaration node, MetricVersion version) {
        return FACADE.computeForOperation(key, node, version);
    }


    /**
     * Compute the sum, average, or highest value of the standard operation metric on all operations of the class node.
     * The type of operation is specified by the {@link ResultOption} parameter.
     *
     * @param key    The key identifying the metric to be computed
     * @param node   The node on which to compute the metric
     * @param option The result option to use
     *
     * @return The value of the metric, or {@code Double.NaN} if the value couln't be computed or {@code option} is
     * {@code null}
     */
    public static double get(MetricKey<ASTMethodOrConstructorDeclaration> key, ASTAnyTypeDeclaration node, ResultOption option) {
        return FACADE.computeWithResultOption(key, node, Version.STANDARD, option);
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
    public static double get(MetricKey<ASTMethodOrConstructorDeclaration> key, ASTAnyTypeDeclaration node, MetricVersion version,
                             ResultOption option) {
        return FACADE.computeWithResultOption(key, node, version, option);
    }

}
