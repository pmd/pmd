/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.api;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;

/**
 * Metric that can be computed on an operation.
 *
 * @author Clément Fournier
 */
public interface OperationMetric extends Metric {

    /**
     * Actually computes the value of a metric for an AST node.
     *
     * @param node    The node.
     * @param version The version of the metric.
     *
     * @return The value of the metric.
     */
    double computeFor(ASTMethodOrConstructorDeclaration node, MetricVersion version);

    /**
     * Compute the sum, average, or highest value of the operation metric on all operations of the class node. The
     * result to aggregate is specified by the {@link ResultOption} parameter.
     *
     * @param node    The class node in which the operations will be fetched.
     * @param version The version of the metric.
     * @param option  The result option to use.
     *
     * @return The value of the metric, or {@code Double.NaN} if the value couln't be computed.
     */
    double computeFor(ASTClassOrInterfaceDeclaration node, MetricVersion version, ResultOption option);

}
