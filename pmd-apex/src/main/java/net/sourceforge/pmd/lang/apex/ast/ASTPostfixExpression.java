/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.expression.PostfixExpression;


public final class ASTPostfixExpression extends AbstractApexNode<PostfixExpression> {

    ASTPostfixExpression(PostfixExpression postfixExpression) {
        super(postfixExpression);
    }

    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public PostfixOperator getOp() {
        return PostfixOperator.valueOf(node.getOp());
    }
}
