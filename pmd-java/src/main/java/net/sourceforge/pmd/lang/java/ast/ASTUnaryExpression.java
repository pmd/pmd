/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;


/**
 * Represents a unary operation on a value. The syntactic form may be
 * prefix or postfix, which are represented with different nodes.
 *
 * <pre class="grammar">
 *
 * UnaryExpression ::= {@link ASTPrefixExpression PrefixExpression}
 *                   | {@link ASTPostfixExpression PostfixExpression}
 *
 * </pre>
 */
public interface ASTUnaryExpression extends ASTExpression {

    /** Returns the expression nested within this expression. */
    default ASTExpression getOperand() {
        return (ASTExpression) jjtGetChild(0);
    }


    /** Returns the constant representing the operator. */
    UnaryOp getOperator();


}
