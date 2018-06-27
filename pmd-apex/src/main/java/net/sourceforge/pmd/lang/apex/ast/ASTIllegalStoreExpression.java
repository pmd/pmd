/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.expression.IllegalStoreExpression;

public class ASTIllegalStoreExpression extends AbstractApexNode<IllegalStoreExpression> {

    public ASTIllegalStoreExpression(IllegalStoreExpression node) {
        super(node);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
