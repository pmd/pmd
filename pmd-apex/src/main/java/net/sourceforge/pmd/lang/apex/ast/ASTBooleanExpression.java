/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.expression.BooleanExpression;

public class ASTBooleanExpression extends AbstractApexNode<BooleanExpression> {

    public ASTBooleanExpression(BooleanExpression booleanExpression) {
        super(booleanExpression);
    }

    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
