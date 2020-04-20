/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.data.ast.BinaryOp;
import apex.jorje.semantic.ast.expression.BinaryExpression;

public final class ASTBinaryExpression extends AbstractApexNode<BinaryExpression> {

    ASTBinaryExpression(BinaryExpression binaryExpression) {
        super(binaryExpression);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public BinaryOp getOperator() {
        return node.getOp();
    }
}
