/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;

/**
 * Metric that can be computed on a class node.
 *
 * @author Clément Fournier
 */
public interface ClassMetric extends Metric {

    double computeFor(ASTClassOrInterfaceDeclaration node, PackageStats holder);

}
