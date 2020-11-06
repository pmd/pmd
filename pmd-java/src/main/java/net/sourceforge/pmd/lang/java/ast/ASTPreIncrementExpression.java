/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;

/**
 * Represents a pre-increment expression on a variable.
 * This has the same precedence as {@linkplain ASTUnaryExpression UnaryExpression}
 * and the like.
 *
 * <pre>
 *
 * PreIncrementExpression ::= "++" {@linkplain ASTPrimaryExpression PrimaryExpression}
 *
 * </pre>
 */
public class ASTPreIncrementExpression extends AbstractJavaTypeNode {

    @InternalApi
    @Deprecated
    public ASTPreIncrementExpression(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTPreIncrementExpression(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

}
