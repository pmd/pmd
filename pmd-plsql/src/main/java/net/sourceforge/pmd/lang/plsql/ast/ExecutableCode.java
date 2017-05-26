/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import net.sourceforge.pmd.lang.ast.Node;

/**
 * Methods required to be considered as an executable piece of code.
 */
public interface ExecutableCode extends Node {

    /**
     * Gets the name of the executable: named thus to match
     * {@link ASTMethodDeclaration}.
     *
     * @return a String representing the name of the method
     */
    String getMethodName();
}
