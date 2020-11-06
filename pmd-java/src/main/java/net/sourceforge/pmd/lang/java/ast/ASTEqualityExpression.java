/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;

/**
 * Represents an identity test between two values or more values.
 * This has a precedence greater than {@link ASTAndExpression},
 * and lower than {@link ASTInstanceOfExpression} and {@link ASTRelationalExpression}.
 *
 * <p>Note that the children of this node are not necessarily {@link ASTInstanceOfExpression},
 * rather, they are expressions with an operator precedence greater or equal to InstanceOfExpression.
 *
 *
 * <pre>
 *
 * EqualityExpression ::=  {@linkplain ASTInstanceOfExpression InstanceOfExpression}  ( ( "==" | "!=" ) {@linkplain ASTInstanceOfExpression InstanceOfExpression}  )+
 *
 * </pre>
 */
public class ASTEqualityExpression extends AbstractJavaTypeNode {

    @InternalApi
    @Deprecated
    public ASTEqualityExpression(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTEqualityExpression(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    /**
     * Returns the image of the operator, i.e. "==" or "!=".
     */
    public String getOperator() {
        return getImage();
    }

}
