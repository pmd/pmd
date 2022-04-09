/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.data.ast.BooleanOp;
import apex.jorje.semantic.ast.expression.BooleanExpression;


public final class ASTBooleanExpression extends AbstractApexNode<BooleanExpression> {

    ASTBooleanExpression(BooleanExpression booleanExpression) {
        super(booleanExpression);
    }



    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }


    public BooleanOp getOperator() {
        return this.node.getOp();
    }

}
