/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.keys;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.oom.PackageStats;
import net.sourceforge.pmd.lang.java.oom.keys.Metric;
import net.sourceforge.pmd.lang.java.oom.keys.MetricOption;

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
     * @param holder  The toplevel package stats (used to help the calculation).
     * @param option A possibly empty list of options.
     *
     * @return The value of the metric.
     */
    double computeFor(ASTClassOrInterfaceDeclaration node, PackageStats holder, MetricOption option);

}
