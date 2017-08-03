/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.metrics.ProjectMirror;

/**
 * Shorthand for a project mirror parameterized with Java-specific node types.
 *
 * @author Clément Fournier
 */
interface JavaProjectMirror extends ProjectMirror<ASTAnyTypeDeclaration, ASTMethodOrConstructorDeclaration> {

}
