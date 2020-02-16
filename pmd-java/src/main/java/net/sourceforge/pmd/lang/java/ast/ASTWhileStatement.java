/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;


/**
 * Represents a {@code while} statement.
 *
 * <pre>
 *
 * WhileStatement ::= "while" "(" {@linkplain ASTExpression Expression} ")" {@linkplain ASTStatement Statement}
 *
 * </pre>
 */
public class ASTWhileStatement extends AbstractJavaNode {

    @InternalApi
    @Deprecated
    public ASTWhileStatement(int id) {
        super(id);
    }


    @InternalApi
    @Deprecated
    public ASTWhileStatement(JavaParser p, int id) {
        super(p, id);
    }


    /**
     * Returns the node that represents the guard of this loop.
     * This may be any expression of type boolean.
     *
     * @deprecated Use {@link #getCondition()}
     */
    @Deprecated
    public ASTExpression getGuardExpressionNode() {
        return (ASTExpression) getChild(0);
    }

    /**
     * Returns the node that represents the guard of this loop.
     * This may be any expression of type boolean.
     */
    public ASTExpression getCondition() {
        return (ASTExpression) getChild(0);
    }


    /**
     * Returns the statement that will be run while the guard
     * evaluates to true.
     */
    public ASTStatement getBody() {
        return (ASTStatement) getChild(1);
    }


    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
