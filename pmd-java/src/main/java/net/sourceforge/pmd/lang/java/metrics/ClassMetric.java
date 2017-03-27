/**
 *
 */
package net.sourceforge.pmd.lang.java.metrics;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;

/**
 * @author Clément Fournier (clement.fournier@insa-rennes.fr)
 *
 */
public interface ClassMetric extends Metric {
    double computeFor(ASTClassOrInterfaceDeclaration node, PackageStats holder);
}
