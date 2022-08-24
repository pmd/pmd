/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import apex.jorje.data.ast.PostfixOp;
import apex.jorje.semantic.ast.expression.PostfixExpression;


public class ASTPostfixExpression extends AbstractApexNode<PostfixExpression> {

    @Deprecated
    @InternalApi
    public ASTPostfixExpression(PostfixExpression postfixExpression) {
        super(postfixExpression);
    }


    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    /**
     * @deprecated Use {@link #getOp()} instead.
     */
    @Deprecated
    public PostfixOp getOperator() {
        return node.getOp();
    }

    public PostfixOperator getOp() {
        return PostfixOperator.valueOf(node.getOp());
    }
}
