/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;

/**
 * Represents a pre-decrement expression on a variable.
 * This has the same precedence as {@linkplain ASTUnaryExpression UnaryExpression}
 * and the like.
 *
 * <pre>
 *
 * PreDecrementExpression ::= "--" {@linkplain ASTPrimaryExpression PrimaryExpression}
 *
 * </pre>
 */
public class ASTPreDecrementExpression extends AbstractJavaTypeNode {

    @InternalApi
    @Deprecated
    public ASTPreDecrementExpression(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTPreDecrementExpression(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
