/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.api;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;

/**
 * Metric that can be computed on a class node.
 *
 * @author Clément Fournier
 */
public interface ClassMetric extends Metric {

    /**
     * Actually computes the value of a metric for an AST node.
     *
     * @param node    The node.
     * @param version A possibly empty list of options.
     *
     * @return The value of the metric.
     */
    double computeFor(ASTClassOrInterfaceDeclaration node, MetricVersion version, ResultOption option);

}
