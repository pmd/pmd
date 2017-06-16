/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom;

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
     * @param holder  The toplevel package stats (used to help the calculation).
     * @param option A possibly empty list of options.
     *
     * @return The value of the metric.
     */
    double computeFor(ASTMethodOrConstructorDeclaration node, PackageStats holder, MetricOption option);


}
