/**
 *
 */
package net.sourceforge.pmd.lang.java.metrics;

import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;

/**
 * @author Clément Fournier (clement.fournier@insa-rennes.fr)
 *
 */
public interface OperationMetric extends Metric {

    double computeFor(ASTMethodOrConstructorDeclaration node, PackageStats holder);

 }
