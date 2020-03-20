/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

/**
 * Methods required to be considered as an executable piece of code.
 */
public interface ExecutableCode extends PLSQLNode {

    /**
     * Gets the name of the executable: named thus to match
     * {@link ASTMethodDeclaration}.
     *
     * @return a String representing the name of the method
     */
    String getMethodName();
}
