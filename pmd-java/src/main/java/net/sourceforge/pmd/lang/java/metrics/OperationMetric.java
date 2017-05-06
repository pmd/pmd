/**
 *
 */
package net.sourceforge.pmd.lang.java.metrics;

import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;

/**
 * Metric that can be computed on an operation.
 *
 * @author Clément Fournier
 */
public interface OperationMetric extends Metric {

    double computeFor(ASTMethodOrConstructorDeclaration node, PackageStats holder);

}
