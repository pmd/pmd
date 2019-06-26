/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * An increment operator for {@link ASTIncrementExpression IncrementExpression}.
 *
 * <pre class="grammar">
 *
 * UnaryOp ::= "++" | "--"
 *
 * </pre>
 *
 * @see AssignmentOp
 * @see UnaryOp
 */
public enum IncrementOp {
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

}
