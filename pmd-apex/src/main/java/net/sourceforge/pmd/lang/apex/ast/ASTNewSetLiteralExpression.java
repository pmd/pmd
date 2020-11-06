/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import apex.jorje.semantic.ast.expression.NewSetLiteralExpression;

public class ASTNewSetLiteralExpression extends AbstractApexNode<NewSetLiteralExpression> {

    @Deprecated
    @InternalApi
    public ASTNewSetLiteralExpression(NewSetLiteralExpression newSetLiteralExpression) {
        super(newSetLiteralExpression);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
