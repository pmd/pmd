/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.data.ast.PrefixOp;
import apex.jorje.semantic.ast.expression.PrefixExpression;

public final class ASTPrefixExpression extends AbstractApexNode<PrefixExpression> {

    ASTPrefixExpression(PrefixExpression prefixExpression) {
        super(prefixExpression);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    public PrefixOp getOperator() {
        return node.getOp();
    }
}
