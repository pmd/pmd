/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import apex.jorje.semantic.ast.expression.NewObjectExpression;

public class ASTNewObjectExpression extends AbstractApexNode<NewObjectExpression> {

    @Deprecated
    @InternalApi
    public ASTNewObjectExpression(NewObjectExpression newObjectExpression) {
        super(newObjectExpression);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public String getType() {
        return String.valueOf(node.getTypeRef());
    }
}
