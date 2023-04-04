/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.expression.NewKeyValueObjectExpression;

public final class ASTNewKeyValueObjectExpression extends AbstractApexNode<NewKeyValueObjectExpression> {

    ASTNewKeyValueObjectExpression(NewKeyValueObjectExpression node) {
        super(node);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public String getType() {
        return node.getTypeRef().getNames().get(0).getValue();
    }

    public int getParameterCount() {
        return node.getParameters().size();
    }
}
