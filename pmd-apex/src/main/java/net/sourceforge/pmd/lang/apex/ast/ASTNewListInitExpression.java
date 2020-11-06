/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import apex.jorje.semantic.ast.expression.NewListInitExpression;

public class ASTNewListInitExpression extends AbstractApexNode<NewListInitExpression> {

    @Deprecated
    @InternalApi
    public ASTNewListInitExpression(NewListInitExpression newListInitExpression) {
        super(newListInitExpression);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
