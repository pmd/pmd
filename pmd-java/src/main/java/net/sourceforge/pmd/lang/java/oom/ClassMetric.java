/**
 *
 */
package net.sourceforge.pmd.lang.java.oom;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.oom.visitor.PackageStats;

/**
 * Metric that can be computed on a class node.
 *
 * @author Clément Fournier
 */
public interface ClassMetric extends Metric {
    double computeFor(ASTClassOrInterfaceDeclaration node, PackageStats holder);
}
