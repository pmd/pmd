/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.expression.NewObjectExpression;

public final class ASTNewObjectExpression extends AbstractApexNode<NewObjectExpression> {

    ASTNewObjectExpression(NewObjectExpression newObjectExpression) {
        super(newObjectExpression);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public String getType() {
        return String.valueOf(node.getTypeRef());
    }
}
