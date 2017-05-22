/**
 *
 */
package net.sourceforge.pmd.lang.java.oom;

import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.oom.visitor.PackageStats;

/**
 * Metric that can be computed on an operation.
 *
 * @author Clément Fournier
 */
public interface OperationMetric extends Metric {

    double computeFor(ASTMethodOrConstructorDeclaration node, PackageStats holder);

}
