/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

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


    /** Returns the statement that represents the body of this loop. */
    default ASTStatement getBody() {
        return (ASTStatement) getLastChild();
    }


    /**
     * Returns the node that represents the condition of this loop.
     * This may be any expression of type boolean.
     *
     * <p>If there is no specified guard, then returns null (in particular,
     * returns null if this is a foreach loop).
     */
    default @Nullable ASTExpression getCondition() {
        return null;
    }

}
