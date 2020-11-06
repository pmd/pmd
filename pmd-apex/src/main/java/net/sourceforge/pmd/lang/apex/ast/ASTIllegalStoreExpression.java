/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import apex.jorje.semantic.ast.expression.IllegalStoreExpression;

public class ASTIllegalStoreExpression extends AbstractApexNode<IllegalStoreExpression> {

    @Deprecated
    @InternalApi
    public ASTIllegalStoreExpression(IllegalStoreExpression node) {
        super(node);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
