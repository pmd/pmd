/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.expression.BinaryExpression;

public class ASTBinaryExpression extends AbstractApexNode<BinaryExpression> {

    public ASTBinaryExpression(BinaryExpression binaryExpression) {
        super(binaryExpression);
    }

    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
