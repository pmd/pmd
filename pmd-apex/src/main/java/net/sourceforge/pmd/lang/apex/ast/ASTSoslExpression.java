/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.expression.SoslExpression;

public final class ASTSoslExpression extends AbstractApexNode<SoslExpression> {

    ASTSoslExpression(SoslExpression soslExpression) {
        super(soslExpression);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
