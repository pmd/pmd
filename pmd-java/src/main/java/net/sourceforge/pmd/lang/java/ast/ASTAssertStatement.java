/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;

/**
 * Represents an {@code assert} statement.
 *
 * <pre>
 *
 * AssertStatement ::= "assert" {@linkplain ASTExpression Expression} ( ":" {@linkplain ASTExpression Expression} )? ";"
 *
 * </pre>
 */
public class ASTAssertStatement extends AbstractJavaNode {

    @InternalApi
    @Deprecated
    public ASTAssertStatement(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTAssertStatement(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    /**
     * Returns the expression tested by this assert statement.
     *
     * @deprecated Use {@link #getCondition()}
     */
    @Deprecated
    public ASTExpression getGuardExpressionNode() {
        return getCondition();
    }

    /**
     * Returns the expression tested by this assert statement.
     */
    public ASTExpression getCondition() {
        return (ASTExpression) getChild(0);
    }


    /**
     * Returns true if this assert statement has a "detail message"
     * expression. In that case, {@link #getDetailMessageNode()} doesn't
     * return null.
     */
    public boolean hasDetailMessage() {
        return getNumChildren() == 2;
    }


    /**
     * Returns the expression that corresponds to the detail message,
     * i.e. the expression after the colon, if it's present.
     */
    public ASTExpression getDetailMessageNode() {
        return hasDetailMessage() ? (ASTExpression) getChild(1) : null;
    }

}
