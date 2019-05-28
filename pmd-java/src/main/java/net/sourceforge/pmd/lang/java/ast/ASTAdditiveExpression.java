/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;

/**
 * Represents an addition operation on two or more values, or string concatenation.
 * This has a precedence greater than {@link ASTShiftExpression}, and lower
 * than {@link ASTMultiplicativeExpression}.
 *
 * <p>Note that the children of this node are not necessarily {@link ASTMultiplicativeExpression},
 * rather, they are expressions with an operator precedence greater or equal to MultiplicativeExpression.
 *
 * <pre>
 *
 * AdditiveExpression ::= {@linkplain ASTMultiplicativeExpression MultiplicativeExpression} ( ( "+" | "-" ) {@linkplain ASTMultiplicativeExpression MultiplicativeExpression} )+
 *
 * </pre>
 */
public class ASTAdditiveExpression extends AbstractJavaTypeNode {

    @InternalApi
    @Deprecated
    public ASTAdditiveExpression(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTAdditiveExpression(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    /**
     * Returns the image of the operator, i.e. "+" or "-".
     */
    public String getOperator() {
        return getImage();
    }
}
