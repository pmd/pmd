/**
 *
 */
package net.sourceforge.pmd.lang.java.metrics;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;

/**
 * Metric that can be computed on a class node.
 *
 * @author Clément Fournier
 */
public interface ClassMetric extends Metric {
    double computeFor(ASTClassOrInterfaceDeclaration node, PackageStats holder);
}
