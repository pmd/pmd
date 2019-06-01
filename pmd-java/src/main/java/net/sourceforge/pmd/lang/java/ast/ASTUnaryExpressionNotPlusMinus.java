/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;

/**
 * Represents a boolean negation or bitwise inverse operation.
 * This has the same precedence as {@linkplain ASTUnaryExpression UnaryExpression}
 * and the like.
 *
 * <p>Note that the child of this node is not necessarily an {@link ASTUnaryExpression},
 * rather, it can be an expression with an operator precedence greater or equal to a UnaryExpression.
 *
 * <pre>
 *
 * UnaryExpressionNotPlusMinus ::=  ( "~" | "!" ) {@linkplain ASTUnaryExpression UnaryExpression}
 *
 * </pre>
 */
public class ASTUnaryExpressionNotPlusMinus extends AbstractJavaTypeNode {

    @InternalApi
    @Deprecated
    public ASTUnaryExpressionNotPlusMinus(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTUnaryExpressionNotPlusMinus(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    /**
     * Returns the image of this unary operator, i.e. "~" or "!".
     */
    public String getOperator() {
        return getImage();
    }


}
