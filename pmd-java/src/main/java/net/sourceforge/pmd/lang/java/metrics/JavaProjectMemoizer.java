/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.metrics.ProjectMemoizer;

/**
 * Shorthand for a project mirror parameterized with Java-specific node types.
 *
 * @author Clément Fournier
 */

interface JavaProjectMemoizer extends ProjectMemoizer<ASTAnyTypeDeclaration, ASTMethodOrConstructorDeclaration> {

}
