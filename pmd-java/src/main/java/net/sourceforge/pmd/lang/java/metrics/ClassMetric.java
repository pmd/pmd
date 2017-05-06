/**
 *
 */
package net.sourceforge.pmd.lang.java.metrics;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;

/**
 * @author Clément Fournier
 *
 */
public interface ClassMetric extends Metric {
    double computeFor(ASTClassOrInterfaceDeclaration node, PackageStats holder);
}
