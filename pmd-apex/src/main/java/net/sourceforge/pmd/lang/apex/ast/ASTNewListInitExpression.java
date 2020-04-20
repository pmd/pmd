/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.expression.NewListInitExpression;

public final class ASTNewListInitExpression extends AbstractApexNode<NewListInitExpression> {

    ASTNewListInitExpression(NewListInitExpression newListInitExpression) {
        super(newListInitExpression);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
