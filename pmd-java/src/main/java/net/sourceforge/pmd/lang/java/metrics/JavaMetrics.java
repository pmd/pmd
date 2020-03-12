/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics;


import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.MethodLikeNode;
import net.sourceforge.pmd.lang.metrics.MetricKey;
import net.sourceforge.pmd.lang.metrics.MetricOptions;
import net.sourceforge.pmd.lang.metrics.MetricsUtil;
import net.sourceforge.pmd.lang.metrics.ResultOption;


/**
 * User bound façade of the Metrics Framework. Provides a uniform interface for the calculation of metrics.
 *
 * @author Clément Fournier
 * @deprecated Use {@link MetricsUtil}
 */
@Deprecated
public final class JavaMetrics {


    private JavaMetrics() { // Cannot be instantiated

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
        return get(key, node, MetricOptions.emptyOptions());
    }


    /**
     * Computes a metric identified by its code on a class AST node, possibly selecting a variant with the {@code
     * MetricOptions} parameter.
     *
     * @param key     The key identifying the metric to be computed
     * @param node    The node on which to compute the metric
     * @param options The options of the metric
     *
     * @return The value of the metric, or {@code Double.NaN} if the value couldn't be computed
     */
    public static double get(MetricKey<ASTAnyTypeDeclaration> key, ASTAnyTypeDeclaration node, MetricOptions options) {
        return MetricsUtil.computeMetricOrNaN(key, node, options);
    }


    /**
     * Computes the standard version of the metric identified by the key on a operation AST node.
     *
     * @param key  The key identifying the metric to be computed
     * @param node The node on which to compute the metric
     *
     * @return The value of the metric, or {@code Double.NaN} if the value couldn't be computed
     */
    public static double get(MetricKey<MethodLikeNode> key, MethodLikeNode node) {
        return get(key, node, MetricOptions.emptyOptions());
    }


    /**
     * @see #get(MetricKey, MethodLikeNode)
     * @deprecated Provided here for backwards binary compatibility with {@link #get(MetricKey, MethodLikeNode)}.
     *     Please explicitly link your code to that method and recompile your code. Will be remove with 7.0.0
     */
    public static double get(MetricKey<MethodLikeNode> key, ASTMethodOrConstructorDeclaration node) {
        return get(key, (MethodLikeNode) node);
    }


    /**
     * @see #get(MetricKey, MethodLikeNode, MetricOptions)
     * @deprecated Provided here for backwards binary compatibility with {@link #get(MetricKey, MethodLikeNode, MetricOptions)}.
     *     Please explicitly link your code to that method and recompile your code. Will be remove with 7.0.0
     */
    @Deprecated
    public static double get(MetricKey<MethodLikeNode> key, ASTMethodOrConstructorDeclaration node, MetricOptions options) {
        return get(key, (MethodLikeNode) node, options);
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
    public static double get(MetricKey<MethodLikeNode> key, MethodLikeNode node, MetricOptions options) {
        return MetricsUtil.computeMetricOrNaN(key, node, options);
    }


    /**
     * Compute the sum, average, or highest value of the standard operation metric on all operations of the class node.
     * The type of operation is specified by the {@link ResultOption} parameter.
     *
     * @param key          The key identifying the metric to be computed
     * @param node         The node on which to compute the metric
     * @param resultOption The result option to use
     *
     * @return The value of the metric, or {@code Double.NaN} if the value couldn't be computed
     */
    public static double get(MetricKey<MethodLikeNode> key, ASTAnyTypeDeclaration node, ResultOption resultOption) {
        return MetricsUtil.computeAggregate(key, findOps(node), resultOption);
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
     * @return The value of the metric, or {@code Double.NaN} if the value couldn't be computed
     */
    public static double get(MetricKey<MethodLikeNode> key, ASTAnyTypeDeclaration node,
                             MetricOptions options, ResultOption resultOption) {
        return MetricsUtil.computeAggregate(key, findOps(node), options, resultOption);
    }

    public static List<MethodLikeNode> findOps(ASTAnyTypeDeclaration node) {
        List<MethodLikeNode> operations = new ArrayList<>();

        for (ASTAnyTypeBodyDeclaration decl : node.getDeclarations()) {
            if (decl.getDeclarationNode() instanceof ASTMethodOrConstructorDeclaration) {
                operations.add((MethodLikeNode) decl.getDeclarationNode());
            }
        }
        return operations;
    }
}
