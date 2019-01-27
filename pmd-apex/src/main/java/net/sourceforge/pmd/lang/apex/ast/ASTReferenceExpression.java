/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.expression.IdentifierContext;
import apex.jorje.semantic.ast.expression.ReferenceExpression;
import apex.jorje.semantic.ast.expression.ReferenceType;


public class ASTReferenceExpression extends AbstractApexNode<ReferenceExpression> {

    public ASTReferenceExpression(ReferenceExpression referenceExpression) {
        super(referenceExpression);
    }


    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    public IdentifierContext getContext() {
        return node.getContext();
    }


    public ReferenceType getReferenceType() {
        return node.getReferenceType();
    }
}
