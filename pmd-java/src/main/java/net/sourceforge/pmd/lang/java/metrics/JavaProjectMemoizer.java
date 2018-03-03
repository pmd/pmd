/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.MethodLikeNode;
import net.sourceforge.pmd.lang.metrics.BasicProjectMemoizer;

/**
 * Shorthand for a project memoizer parameterized with Java-specific node types.
 *
 * @author Clément Fournier
 */
class JavaProjectMemoizer extends BasicProjectMemoizer<ASTAnyTypeDeclaration, MethodLikeNode> {

}
