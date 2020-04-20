/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import apex.jorje.data.ast.BooleanOp;
import apex.jorje.semantic.ast.expression.BooleanExpression;


public class ASTBooleanExpression extends AbstractApexNode<BooleanExpression> {

    @Deprecated
    @InternalApi
    public ASTBooleanExpression(BooleanExpression booleanExpression) {
        super(booleanExpression);
    }


    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    public BooleanOp getOperator() {
        return this.node.getOp();
    }

}
