/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.java.ast.InternalInterfaces.OperatorLike;

/**
 * An increment operator for {@link ASTIncrementExpression IncrementExpression}.
 *
 * <pre class="grammar">
 *
 * IncrementOp ::= "++" | "--"
 *
 * </pre>
 *
 * @see AssignmentOp
 * @see UnaryOp
 */
public enum IncrementOp implements OperatorLike {
    /** "++" */
    INCREMENT("++"),
    /** "--" */
    DECREMENT("--");

    private final String code;


    IncrementOp(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return this.code;
    }

    @Override
    public String getToken() {
        return code;
    }
}
