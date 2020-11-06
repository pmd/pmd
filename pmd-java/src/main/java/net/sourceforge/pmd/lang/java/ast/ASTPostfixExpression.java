/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;

/**
 * Represents a unary postfix operation on a value.
 * This is one of the {@linkplain ASTUnaryExpression PrefixExpression}
 * and has the same precedence.
 *
 * <pre>
 *
 * PostfixExpression ::= {@linkplain ASTPrimaryExpression PrimaryExpression} ( "++" | "--" )
 *
 * </pre>
 */
public class ASTPostfixExpression extends AbstractJavaTypeNode {

    @InternalApi
    @Deprecated
    public ASTPostfixExpression(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTPostfixExpression(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    /**
     * Returns the image of this unary operator, i.e. "++" or "--".
     */
    public String getOperator() {
        return getImage();
    }

}
