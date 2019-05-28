/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;

/**
 * Represents a multiplication, division, or modulo operation on
 * two or more values. This has a precedence greater than {@link ASTAdditiveExpression},
 * and lower than {@linkplain ASTUnaryExpression UnaryExpression}.
 *
 * <p>Note that the children of this node are not necessarily {@link ASTUnaryExpression}s,
 * rather, they are expressions with an operator precedence greater or equal to UnaryExpression.
 *
 * <pre>
 *
 * MultiplicativeExpression ::= {@linkplain ASTUnaryExpression UnaryExpression} ( ( "*" | "/" | "%" ) {@linkplain ASTUnaryExpression UnaryExpression} )+
 *
 * </pre>
 */
public class ASTMultiplicativeExpression extends AbstractJavaTypeNode {

    @InternalApi
    @Deprecated
    public ASTMultiplicativeExpression(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTMultiplicativeExpression(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    /**
     * Returns the image of the operator, i.e. "*", "/" or "%".
     */
    public String getOperator() {
        return getImage();
    }
}
