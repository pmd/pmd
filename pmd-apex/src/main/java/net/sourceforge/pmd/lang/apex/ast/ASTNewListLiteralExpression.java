/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import apex.jorje.semantic.ast.expression.NewListLiteralExpression;

public class ASTNewListLiteralExpression extends AbstractApexNode<NewListLiteralExpression> {

    @Deprecated
    @InternalApi
    public ASTNewListLiteralExpression(NewListLiteralExpression newListLiteralExpression) {
        super(newListLiteralExpression);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
