/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics;


import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.metrics.MetricKey;
import net.sourceforge.pmd.lang.metrics.MetricOption;
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
     * Returns the underlying façade.
     *
     * @return The underlying façade instance
     */
    static JavaMetricsFacade getFacade() {
        return FACADE;
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
     * @return The value of the metric, or {@code Double.NaN} if the value couldn't be computed
     */
    public static double get(MetricKey<ASTAnyTypeDeclaration> key, ASTAnyTypeDeclaration node) {
        return FACADE.computeForType(key, node);
    }


    /**
     * Computes a metric identified by its code on a class AST node, possibly selecting a variant with the {@code
     * MetricVersion} parameter.
     *
     * @param key     The key identifying the metric to be computed
     * @param node    The node on which to compute the metric
     * @param options The options of the metric
     *
     * @return The value of the metric, or {@code Double.NaN} if the value couldn't be computed
     */
    public static double get(MetricKey<ASTAnyTypeDeclaration> key, ASTAnyTypeDeclaration node, MetricOption... options) {
        return FACADE.computeForType(key, node, options);
    }


    /**
     * Computes the standard version of the metric identified by the key on a operation AST node.
     *
     * @param key  The key identifying the metric to be computed
     * @param node The node on which to compute the metric
     *
     * @return The value of the metric, or {@code Double.NaN} if the value couldn't be computed
     */
    public static double get(MetricKey<ASTMethodOrConstructorDeclaration> key, ASTMethodOrConstructorDeclaration node) {
        return FACADE.computeForOperation(key, node);
    }


    /**
     * Computes a metric identified by its key on a operation AST node.
     *
     * @param key     The key identifying the metric to be computed
     * @param node    The node on which to compute the metric
     * @param options The options of the metric
     *
     * @return The value of the metric, or {@code Double.NaN} if the value couldn't be computed
     */
    public static double get(MetricKey<ASTMethodOrConstructorDeclaration> key, ASTMethodOrConstructorDeclaration node,
                             MetricOption... options) {
        return FACADE.computeForOperation(key, node, options);
    }


    /**
     * Compute the sum, average, or highest value of the standard operation metric on all operations of the class node.
     * The type of operation is specified by the {@link ResultOption} parameter.
     *
     * @param key          The key identifying the metric to be computed
     * @param node         The node on which to compute the metric
     * @param resultOption The result option to use
     *
     * @return The value of the metric, or {@code Double.NaN} if the value couldn't be computed or {@code option} is
     * {@code null}
     */
    public static double get(MetricKey<ASTMethodOrConstructorDeclaration> key, ASTAnyTypeDeclaration node, ResultOption resultOption) {
        return FACADE.computeWithResultOption(key, node, resultOption);
    }


    /**
     * Compute the sum, average, or highest value of the operation metric on all operations of the class node. The type
     * of operation is specified by the {@link ResultOption} parameter.
     *
     * @param key          The key identifying the metric to be computed
     * @param node         The node on which to compute the metric
     * @param resultOption The result option to use
     * @param options      The version of the metric
     *
     * @return The value of the metric, or {@code Double.NaN} if the value couldn't be computed or {@code option} is
     * {@code null}
     */
    public static double get(MetricKey<ASTMethodOrConstructorDeclaration> key, ASTAnyTypeDeclaration node,
                             ResultOption resultOption, MetricOption... options) {
        return FACADE.computeWithResultOption(key, node, resultOption, options);
    }

}
