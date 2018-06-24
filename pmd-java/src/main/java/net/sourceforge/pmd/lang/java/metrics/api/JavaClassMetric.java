/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.api;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.metrics.Metric;

/**
 * Metric that can be computed on a class node.
 *
 * @author Clément Fournier
 */
public interface JavaClassMetric extends Metric<ASTAnyTypeDeclaration> {


}
