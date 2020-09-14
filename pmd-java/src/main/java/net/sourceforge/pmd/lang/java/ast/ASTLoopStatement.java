/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * A loop statement.
 *
 * <pre class="grammar">
 *
 * Statement ::= {@link ASTDoStatement DoStatement}
 *             | {@link ASTForeachStatement ForeachStatement}
 *             | {@link ASTForStatement ForStatement}
 *             | {@link ASTWhileStatement WhileStatement}
 *
 * </pre>
 *
 *
 */
public interface ASTLoopStatement extends ASTStatement {
}
