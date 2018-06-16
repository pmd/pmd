/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.expression.ReferenceExpression;

public class ASTReferenceExpression extends AbstractApexNode<ReferenceExpression> {

    public ASTReferenceExpression(ReferenceExpression referenceExpression) {
        super(referenceExpression);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
