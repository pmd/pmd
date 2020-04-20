/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import apex.jorje.semantic.ast.expression.SoslExpression;

public class ASTSoslExpression extends AbstractApexNode<SoslExpression> {

    @Deprecated
    @InternalApi
    public ASTSoslExpression(SoslExpression soslExpression) {
        super(soslExpression);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
