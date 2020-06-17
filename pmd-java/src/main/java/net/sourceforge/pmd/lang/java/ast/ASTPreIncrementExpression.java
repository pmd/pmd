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

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

}
