/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.expression.NewMapInitExpression;

public class ASTNewMapInitExpression extends AbstractApexNode<NewMapInitExpression> {

    public ASTNewMapInitExpression(NewMapInitExpression newMapInitExpression) {
        super(newMapInitExpression);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
