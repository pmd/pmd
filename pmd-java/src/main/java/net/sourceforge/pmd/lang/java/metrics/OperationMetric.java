/**
 *
 */
package net.sourceforge.pmd.lang.java.metrics;

import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;

/**
 * @author Clément Fournier (clement.fournier@insa-rennes.fr)
 *
 */
public interface OperationMetric extends Metric {
    double computeFor(ASTMethodDeclaration node, PackageStats holder);

    double computeFor(ASTConstructorDeclaration node, PackageStats holder);
}
