/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.expression.NewListLiteralExpression;

public final class ASTNewListLiteralExpression extends AbstractApexNode<NewListLiteralExpression> {

    ASTNewListLiteralExpression(NewListLiteralExpression newListLiteralExpression) {
        super(newListLiteralExpression);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
